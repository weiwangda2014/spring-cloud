package org.cloud.controller;

import org.cloud.jpa.domain.ComponentMapping;
import org.cloud.jpa.domain.Relation;
import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.domain.UnmappedTrace;
import org.cloud.jpa.domain.component.Hardware;
import org.cloud.jpa.domain.component.Instance;
import org.cloud.jpa.domain.component.Service;
import org.cloud.jpa.domain.enums.HttpMethod;
import org.cloud.service.*;
import org.cloud.service.component.HardwareService;
import org.cloud.service.component.InstanceService;
import org.cloud.service.component.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import zipkin.Annotation;
import zipkin.BinaryAnnotation;
import zipkin.Endpoint;
import zipkin.Span;
import zipkin.storage.*;
import zipkin.storage.mysql.MySQLStorage;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

@Controller
public class SpanController {

    private final ServiceService serviceService;

    private final InstanceService instanceService;

    private final HardwareService hardwareService;

    private final RevisionService revisionService;

    private final RelationService relationService;

    private final ComponentMappingService componentMappingService;

    private final UnmappedTraceService unmappedTraceService;

    private Map<String, Revision> componentRevisionMap;

    private StorageComponent zipkinStorageComponent = null;

    private SpanMappingService spanMappingService;

    @Autowired
    public SpanController(ServiceService serviceService,
                          InstanceService instanceService,
                          HardwareService hardwareService,
                          RevisionService revisionService,
                          RelationService relationService,
                          ComponentMappingService componentMappingService,
                          UnmappedTraceService unmappedTraceService,
                          SpanMappingService spanMappingService) {
        this.serviceService = serviceService;
        this.instanceService = instanceService;
        this.hardwareService = hardwareService;
        this.revisionService = revisionService;
        this.relationService = relationService;
        this.componentMappingService = componentMappingService;
        this.unmappedTraceService = unmappedTraceService;
        this.spanMappingService = spanMappingService;
    }

    private Map<Long, Span> localComponentSpans = new LinkedHashMap<Long, Span>(1000) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Span> entry) {
            return size() > 1000;
        }
    };

    private Map<Long, Span> tempStorageSpans = new LinkedHashMap<Long, Span>(1000) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Span> entry) {
            return size() > 1000;
        }
    };

    private Map<Long, List<Relation>> latestRelationsByParent = new LinkedHashMap<Long, List<Relation>>(100) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, List<Relation>> entry) {
            return size() > 100;
        }
    };

    private Map<Long, Relation> incompleteRelations = new LinkedHashMap<Long, Relation>(1000) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Relation> entry) {
            return size() > 1000;
        }
    };

    private Map<Long, List<Relation>> transactionRelations = new LinkedHashMap<Long, List<Relation>>(100) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, List<Relation>> entry) {
            return size() > 100;
        }
    };

    @PostConstruct
    private void initialize() {
        componentRevisionMap = revisionService.getCurrentRevisionsByComponentName();
    }

    // Proceeding of all ingoing spans via a stream or the ZipKin-REST API.
    // components and relations of them are discovered and persistently stored here
    public synchronized void proceedSpans(List<Span> spans) {
        for (Span span : spans) {
            Annotation csAnnotation = null;
            Annotation srAnnotation = null;

            Span tempSpan = this.tempStorageSpans.get(span.id);
            if (tempSpan == null)
                this.tempStorageSpans.put(span.id, span);

            //// 0 Update Components based on the endpoints
            // 0.1 Create Components (Service, Instance, Hardware) from endpoint information
            boolean allEndpointsAreValid = true;
            for (Annotation annotation : span.annotations) {
                allEndpointsAreValid &= updateComponents(annotation.endpoint);

                if (annotation.value.toLowerCase().equals("cs"))
                    csAnnotation = annotation;
                if (annotation.value.toLowerCase().equals("sr"))
                    srAnnotation = annotation;
            }

            // 0.2 if one of the (not binary) endpoints is not valid (== no ip address, happens from time to time, probably a sleuth bug), don't process the span
            if (!allEndpointsAreValid)
                continue;

            //// 1  Create Components
            // 1.1  Generate Services, Instances and Hardware components and relations between them from the binary endpoints
            for (BinaryAnnotation annotation : span.binaryAnnotations)
                updateComponents(annotation.endpoint);

            /*
            //// 2  Discover relations between services
            // 2.1  compute the first span of a transaction (usually Zuul's SR-Response to the not instrumented client)
            //      Mapping of the first span's path (name) with a service via the ComponentMapping-collection
            //      and creation of newly discovered relations between activities and services.
            if (span.parentId == null && srAnnotation != null) {
                String path = span.name;
                String method = "GET";

                for (BinaryAnnotation annotation : span.binaryAnnotations) {
                    if (annotation.key.toLowerCase().equals("http.method")) {
                        method = new String(annotation.value).toUpperCase();
                    }
                }


                // 2.1.1 Create discovered relations between service <-> activities
                Boolean mappingFound = false;
                List<ComponentMapping> deprecatedMappings = new ArrayList<>();
                for (ComponentMapping componentMapping : componentMappingService.findAll()) {
                    if ((componentMapping.getHttpMethods() & HttpMethod.valueOf(method).getValue()) != 0 && Pattern.compile(componentMapping.getHttpPathRegex()).matcher(path).find()) {
                        Revision activityRevision = revisionService.getCurrentRevisionsByComponentId().get(componentMapping.getComponent().getId());
                        if (activityRevision != null) {
                            Relation activityRelation = new Relation();
                            activityRelation.setCaller(activityRevision);
                            activityRelation.setCallee(getServiceRevision(srAnnotation.endpoint));
                            activityRelation.setOwner(activityRevision);
                            addTransactionRelation(activityRelation, span);

                            Revision serviceRevision = getServiceRevision(srAnnotation.endpoint);
                            Revision instanceRevision = getInstanceRevision(srAnnotation.endpoint);
                            Revision hardwareRevision = getHardwareRevision(srAnnotation.endpoint);
                            addTransactionRelation(relationService.findByOwnerAndCallerAndCallee(serviceRevision, serviceRevision, instanceRevision), span);
                            addTransactionRelation(relationService.findByOwnerAndCallerAndCallee(instanceRevision, instanceRevision, hardwareRevision), span);

                            mappingFound = true;
                        } else
                            deprecatedMappings.add(componentMapping);
                    }
                }

                // 2.1.2 if a mapping of activity <-> service exists for a activity without current revision (means, the activity was removed from the modeled processes), remove the mapping
                if (deprecatedMappings.size() > 0)
                    componentMappingService.delete(deprecatedMappings);

                // 2.1.3 if a trace could not be mapped, add it to the list of unmappedTraces (only if no other trace with same path was already added)
                if (!mappingFound && unmappedTraceService.findByHttpPathAndMethod(path, HttpMethod.valueOf(method)) == null) {
                    UnmappedTrace unmappedTrace = new UnmappedTrace();
                    unmappedTrace.setHttpMethod(HttpMethod.valueOf(method));
                    unmappedTrace.setHttpPath(path);
                    unmappedTrace.setTraceId(span.traceId);
                    unmappedTraceService.saveUnmappedTrace(unmappedTrace);
                }

            }
            // 2.2  if a span contains a client-sent(CS) or server-received(SR) annotation, add the span to a incomplete relation.
            //      After the CS and the SR of one span was discovered, fulfill the relation and save it persistently, if not already existent.
            else if (csAnnotation != null || srAnnotation != null) {
                Annotation annotation = (csAnnotation != null) ? csAnnotation : srAnnotation;
                Revision serviceRevision = getServiceRevision(annotation.endpoint);
                Revision instanceRevision = getInstanceRevision(annotation.endpoint);
                Revision hardwareRevision = getHardwareRevision(annotation.endpoint);
                addTransactionRelation(relationService.findByOwnerAndCallerAndCallee(serviceRevision, serviceRevision, instanceRevision), span);
                addTransactionRelation(relationService.findByOwnerAndCallerAndCallee(instanceRevision, instanceRevision, hardwareRevision), span);

                Relation relation = incompleteRelations.get(span.id);
                // if relation is null, the equivalent Server- or Client-span was not yet processed
                if (relation == null) {
                    relation = new Relation();
                    incompleteRelations.put(span.id, relation);
                }

                if (csAnnotation != null)
                    relation.setCaller(serviceRevision);
                else
                    relation.setCallee(serviceRevision);

                // add all annotations to the new discovered relation
                relation.setAnnotationsFromBinaryAnnotations(span.binaryAnnotations);

                //if the relation is complete (has caller and callee), save it persistently and generate transitive relations (S1 -> S2 and S2-> S3 => S1 -> S3)
                if (relation.getCaller() != null && relation.getCallee() != null) {
                    incompleteRelations.remove(span.id);
                    if (span.parentId != null) {
                        if (!latestRelationsByParent.containsKey(span.parentId))
                            latestRelationsByParent.put(span.parentId, new LinkedList<>());
                        latestRelationsByParent.get(span.parentId).add(relation);
                        proceedLocalComponentForRelation(span.parentId, relation);
                    }

                    relation.setOwner(relation.getCaller());
                    addTransactionRelation(relation, span);
                }
                // Store local components
            } else {
                for (BinaryAnnotation annotation : span.binaryAnnotations) {
                    if (annotation.key.toLowerCase().equals("lc")) {
                        localComponentSpans.put(span.id, span);
                        if (latestRelationsByParent.containsKey(span.id)) {
                            for (Relation relation : latestRelationsByParent.get(span.id)) {
                                proceedLocalComponentForRelation(span.id, relation);
                            }
                        }
                        break;
                    }
                }
            }
            */

            //// 3  Discover relations between services through parent - child ids
            // 3.1  compute the first span of a transaction (usually Zuul's SR-Response to the not instrumented client)
            //      Mapping of the first span's path (name) with a service via the ComponentMapping-collection
            //      and creation of newly discovered relations between activities and services.
            Annotation annotation = (csAnnotation != null) ? csAnnotation : srAnnotation;
            Revision serviceRevision = getServiceRevision(annotation.endpoint);
            Revision instanceRevision = getInstanceRevision(annotation.endpoint);
            Revision hardwareRevision = getHardwareRevision(annotation.endpoint);
            addTransactionRelation(checkForExistingRelation(serviceRevision, serviceRevision, instanceRevision), span);
            addTransactionRelation(checkForExistingRelation(instanceRevision, instanceRevision, hardwareRevision), span);

            if (span.parentId != null) {

                Span parentSpan = this.tempStorageSpans.get(span.parentId);

                if (parentSpan != null) {
                    Revision callerRevision = getServiceRevision(parentSpan.annotations.get(0).endpoint);
                    Relation r = this.relationService.findByOwnerAndCallerAndCallee(callerRevision, callerRevision, serviceRevision);
                    if (r == null) {
                        Relation relation = new Relation();
                        relation.setOwner(callerRevision);
                        relation.setCaller(callerRevision);
                        relation.setCallee(serviceRevision);

                        // add all annotations to the new discovered relation
                        relation.setAnnotationsFromBinaryAnnotations(span.binaryAnnotations);

                        //if the relation is complete (has caller and callee), save it persistently and generate transitive relations (S1 -> S2 and S2-> S3 => S1 -> S3)
                        if (relation.getCaller() != null && relation.getCallee() != null) {
                            addTransactionRelation(relation, span);
                        }
                    }
                }

                // Store local components
            } else {
                for (BinaryAnnotation bannotation : span.binaryAnnotations) {
                    if (bannotation.key.toLowerCase().equals("lc")) {
                        localComponentSpans.put(span.id, span);
                        if (latestRelationsByParent.containsKey(span.id)) {
                            for (Relation relation : latestRelationsByParent.get(span.id)) {
                                proceedLocalComponentForRelation(span.id, relation);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void proceedLocalComponentForRelation(Long spanId, Relation relation) {
        if (spanId != null) {
            Span span = localComponentSpans.get(spanId);
            if (span != null) {
                for (BinaryAnnotation lcAnnotation : span.binaryAnnotations) {
                    if (lcAnnotation.key.toLowerCase().equals("thread")) {
                        relation.setAnnotation("ad.async", "true");
                    }
                }
            }
        }
    }

    // Add newly discovered relation to a transaction and create transitive relations from the collection of transaction-related relations
    private void addTransactionRelation(Relation relation, Span span) {
        List<Relation> relations = transactionRelations.computeIfAbsent(span.traceId, k -> new ArrayList<>());
        List<Relation> relationsToCheck = new ArrayList<>();
        relationsToCheck.add(relation);
        if (relation != null) {
            while (relationsToCheck.size() > 0) {
                relation = relationsToCheck.get(0);

                // if the id is null, its uncertainly, if the relation already exists, checking against the object-repository required
                if (relation.getId() == null) {
                    Relation existingRelation = relationService.findByOwnerAndCallerAndCallee(relation.getOwner(), relation.getCaller(), relation.getCallee());

                    // the relation exists already and is not new discovered. So use the found relation-object for further processing and add newly discovered annotations
                    if (existingRelation != null) {
                        // Todo: Check if there are really new annotations and if not, dont update/save the object! (many wrong update-Changelogs because of useless object-updates without changes)
                        existingRelation.setAnnotations(relation.getAnnotations());
                        relation = existingRelation;
                    }
                    relation = relationService.saveRelation(relation);
                }
                // if the id is not null, this relation is already persistently stored,
                else if (relation.annotationsRequireSave())
                    relation = relationService.saveRelation(relation);

                relations.add(relation);
                // get all new transitive relations of the set of transaction-relations and the current relation
                relationsToCheck.addAll(getTransitiveRelations(relation, relations));
                relationsToCheck.remove(relation);
            }
        }
    }

    // check if relation exists, if not create one, but do not save
    private Relation checkForExistingRelation(Revision owner, Revision caller, Revision callee) {
        Relation relation = null;
        Relation checkRelation = relationService.findByOwnerAndCallerAndCallee(owner, caller, callee);
        if (checkRelation == null) {
            relation = new Relation();
            relation.setOwner(owner);
            relation.setCaller(caller);
            relation.setCallee(callee);
        } else {
            relation = checkRelation;
        }

        return relation;
    }

    // Finds and returns transitive relations between a relation and a list of relations
    // returns only relations, which are not already in the submitted list of relations
    private List<Relation> getTransitiveRelations(Relation relation, List<Relation> relations) {
        List<Relation> transitiveRelations = new ArrayList<>();
        for (Relation currentRelation : relations) {
            Relation topRelation;
            Relation bottomRelation;

            if (currentRelation.getCaller().getId().equals(relation.getCallee().getId())) {
                topRelation = relation;
                bottomRelation = currentRelation;
            } else if (relation.getCaller().getId().equals(currentRelation.getCallee().getId())) {
                topRelation = currentRelation;
                bottomRelation = relation;
            } else
                continue;

            Relation newRelation = new Relation();
            newRelation.setCaller(bottomRelation.getCaller());
            newRelation.setCallee(bottomRelation.getCallee());
            newRelation.setOwner(topRelation.getOwner());
            newRelation.setAnnotations(bottomRelation.getAnnotations());

            if (!relations.contains(newRelation))
                transitiveRelations.add(newRelation);
        }
        return transitiveRelations;
    }

    private Revision getServiceRevision(Endpoint endpoint) {
        String serviceName = endpoint.serviceName.toUpperCase();
        if (!revisionService.getCurrentRevisionsByComponentName().containsKey(serviceName))
            updateComponents(endpoint);
        return revisionService.getCurrentRevisionsByComponentName().get(serviceName);
    }

    private Revision getInstanceRevision(Endpoint endpoint) {
        String instanceName = getInstanceName(endpoint);
        if (!revisionService.getCurrentRevisionsByComponentName().containsKey(instanceName))
            updateComponents(endpoint);
        return revisionService.getCurrentRevisionsByComponentName().get(instanceName);
    }

    private Revision getHardwareRevision(Endpoint endpoint) {
        String hardwareName = getHardwareName(endpoint);
        if (!revisionService.getCurrentRevisionsByComponentName().containsKey(hardwareName))
            updateComponents(endpoint);
        return revisionService.getCurrentRevisionsByComponentName().get(hardwareName);
    }

    // extracts information about components (Hardware, Service, Instance) from an endpoint-object
    // and creates new component-objects for them if they are not existing yet.
    // Additionally it creates relations between these components
    private boolean updateComponents(Endpoint endpoint) {
        String serviceName = endpoint.serviceName.toUpperCase();
        String hardwareName = getHardwareName(endpoint);
        String instanceName = getInstanceName(endpoint);
        if (instanceName.equals("unknown"))
            return false;

        if (!componentRevisionMap.containsKey(serviceName)) {

            Service service = serviceService.findByName(serviceName);
            if (service == null) {
                serviceService.createService(serviceName);
            } else
                revisionService.createRevision(service);
        }


        if (!componentRevisionMap.containsKey(hardwareName)) {
            Hardware hardware = hardwareService.findByName(hardwareName);
            if (hardware == null)
                hardwareService.createHardware(hardwareName);
            else
                revisionService.createRevision(hardware);
        }


        if (!componentRevisionMap.containsKey(instanceName)) {

            Instance instance = instanceService.findByName(instanceName);
            if (instance == null) {
                instance = instanceService.createInstance(instanceName);
                instance.setAnnotation("ad.port", endpoint.port.toString());
                instance.setAnnotation("ad.ip", getHardwareName(endpoint));
                instanceService.saveInstance(instance);
            } else
                revisionService.createRevision(instance);

            Revision instanceRevision = componentRevisionMap.get(instanceName);
            Revision serviceRevision = componentRevisionMap.get(serviceName);
            Revision hardwareRevision = componentRevisionMap.get(hardwareName);

            relationService.setInstanceRevisionRelations(instanceRevision, serviceRevision, hardwareRevision);
        }
        return true;
    }


    private String getInstanceName(Endpoint endpoint) {
        String serviceName = endpoint.serviceName.toUpperCase();
        String ip = getHardwareName(endpoint);
        return ip + ":" + serviceName + ":" + endpoint.port;
    }

    private String getHardwareName(Endpoint endpoint) {
        String ip = "";
        try {
            if (endpoint.ipv4 != 0)
                ip = InetAddress.getByAddress(BigInteger.valueOf(endpoint.ipv4).toByteArray()).getHostAddress();
            else if (endpoint.ipv6 != null)
                ip = InetAddress.getByAddress(endpoint.ipv6).getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (ip.isEmpty()) {
            ip = "unknown";
        }
        return ip;
    }

    private void setZipkinStorageComponent(StorageComponent zipkinStorageComponent) {
        this.zipkinStorageComponent = zipkinStorageComponent;
    }

    public StorageComponent getZipkinStorageComponent() {
        return zipkinStorageComponent;
    }

    private void mapSpansAndSave(List<Span> spans) {
        spanMappingService.mapAndSaveSpans(spans);
    }

    // Storage-class which overrides the default storage behaviour of the ZipKin server.
    // Behaves like a extended Zipkin-Mysql-Storage by being a proxy.
    // It forwards any spans to the standard mysql-storage. IF they could be validated and stored by the standard-storage,
    // they are additionally forwarded to a custom span-progressing method for architecture discovery
    @SuppressWarnings({"unused", "SpringJavaAutowiredMembersInspection"})
    static class ZipkinStorage {
        private final ApplicationContext context;

        @Autowired
        public ZipkinStorage(ApplicationContext context) {
            this.context = context;
        }

        @Bean
        StorageComponent storage(Executor executor, DataSource dataSource) {
            MySQLStorage mysqlStorage = MySQLStorage.builder()
                    .executor(executor)
                    .datasource(dataSource).build();
            AsyncSpanConsumer consumer = (spans, callback) -> {
                Callback<Void> myCallback = new Callback<Void>() {
                    @Override
                    public void onSuccess(Void value) {
                        callback.onSuccess(value);
                        System.out.println("SPANS SAVED, START PROCESSING");
                        context.getBean(SpanController.class).proceedSpans(spans);
                        System.out.println("SPANS PROCESSED");
                        context.getBean(SpanController.class).mapSpansAndSave(spans);
                    }

                    @Override
                    public void onError(Throwable t) {
                        callback.onError(t);
                    }
                };
                System.out.println("SPANS INCOMING");
                mysqlStorage.asyncSpanConsumer().accept(spans, myCallback);
            };

            StorageComponent storageComponent = new StorageComponent() {
                @Override
                public SpanStore spanStore() {
                    return mysqlStorage.spanStore();
                }

                @Override
                public AsyncSpanStore asyncSpanStore() {
                    return mysqlStorage.asyncSpanStore();
                }

                @Override
                public AsyncSpanConsumer asyncSpanConsumer() {
                    return consumer;
                }

                @Override
                public CheckResult check() {
                    return mysqlStorage.check();
                }

                @Override
                public void close() {
                    mysqlStorage.close();
                }
            };
            context.getBean(SpanController.class).setZipkinStorageComponent(storageComponent);
            return storageComponent;
        }
    }
}