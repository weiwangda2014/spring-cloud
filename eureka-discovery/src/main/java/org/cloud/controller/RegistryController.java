package org.cloud.controller;

import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.domain.component.Component;
import org.cloud.jpa.domain.component.Hardware;
import org.cloud.jpa.domain.component.Instance;
import org.cloud.jpa.domain.component.Service;
import org.cloud.service.RelationService;
import org.cloud.service.RevisionService;
import org.cloud.service.component.HardwareService;
import org.cloud.service.component.InstanceService;
import org.cloud.service.component.ServiceService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
public class RegistryController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private HardwareService hardwareService;

    @Autowired
    private RevisionService revisionService;

    @Autowired
    private RelationService relationService;

    @Autowired
    private InstanceService instanceService;

    @Value("${ad.registry.eureka.endpoint}")
    private String eurekaEndpoint;

    @SuppressWarnings("unchecked")
    public void eurekaCall() {
        Set<String> eurekaServices = new HashSet<>();
        Set<String> eurekaHardware = new HashSet<>();
        Map<String, Pair<String, String>> eurekaInstances = new HashMap<>();
        Map<String, Date> eurekainstanceUptime = new HashMap<>();
        Map<String, Map<String, Object>> eurekaInstanceDetails = new HashMap<>();

        // 0. collect services, hardware and instances from eureka
        Map<?, ?> responseEntity = restTemplate.getForObject(eurekaEndpoint, Map.class);
        if (responseEntity.containsKey("applications") && ((HashMap<?, ?>) responseEntity.get("applications")).containsKey("application")) {
            ArrayList<HashMap<String, Object>> applications = (ArrayList<HashMap<String, Object>>) ((HashMap<String, Object>) responseEntity.get("applications")).get("application");
            for (HashMap<String, Object> application : applications) {
                for (HashMap<String, Object> instance : (ArrayList<HashMap<String, Object>>) application.get("instance"))
                    if (instance.get("status").equals("UP")) {
                        String serviceName = ((String) application.get("name")).toUpperCase();
                        eurekaServices.add(serviceName);

                        String hardwareName = instance.get("ipAddr").toString();
                        eurekaHardware.add(hardwareName);

                        String port = ((HashMap<String, Object>) instance.get("port")).get("$").toString();
                        String instanceName = hardwareName + ":" + instance.get("app") + ":" + port;
                        eurekaInstances.put(instanceName, new Pair<>(serviceName, hardwareName));
                        String registrationTimestamp = ((HashMap<String, Object>) instance.get("leaseInfo")).get("registrationTimestamp").toString();
                        eurekainstanceUptime.put(instanceName, new Date(Long.parseLong(registrationTimestamp)));
                        eurekaInstanceDetails.put(instanceName, instance);
                    }
            }
        }

        // 1. Get all currently existing services, hardware and instances from datamodel
        Set<String> currentServices = new HashSet<>();
        Set<String> currentHardware = new HashSet<>();
        Set<String> currentInstances = new HashSet<>();

        Map<String, Revision> componentRevisionMap = revisionService.getCurrentRevisionsByComponentName();
        for (Revision revision : componentRevisionMap.values()) {
            Component component = revision.getComponent();
            if (component instanceof Service)
                currentServices.add(component.getName());
            else if (component instanceof Hardware)
                currentHardware.add(component.getName());
            else if (component instanceof Instance)
                currentInstances.add(component.getName());
        }


        // 2.1 add newly discovered services
        Set<String> registryServices = new HashSet<>();
        registryServices.addAll(eurekaServices);
        registryServices.removeAll(currentServices);
        for (String serviceName : registryServices) {
            Service service = serviceService.findByName(serviceName);
            if (service == null)
                serviceService.createService(serviceName);
            else
                revisionService.createRevision(service);
        }

        // 2.2 disable disappeared services
        currentServices.removeAll(eurekaServices);
        for (String serviceName : currentServices) {
            revisionService.closeRevision(componentRevisionMap.get(serviceName));
        }

        // 3.1 add newly discovered hardware
        Set<String> registryHardware = new HashSet<>();
        registryHardware.addAll(eurekaHardware);
        registryHardware.removeAll(currentHardware);
        for (String hardwareName : registryHardware) {
            Hardware hardware = hardwareService.findByName(hardwareName);
            if (hardware == null)
                hardwareService.createHardware(hardwareName);
            else
                revisionService.createRevision(hardware);
        }

        // 3.2 disable disappeared hardware
        currentHardware.removeAll(eurekaHardware);
        for (String hardwareName : currentHardware) {
            revisionService.closeRevision(componentRevisionMap.get(hardwareName));
        }

        // 4.1 add newly discovered instances and create relations to corresponding service and hardware
        Set<String> registryInstances = new HashSet<>();
        registryInstances.addAll(eurekaInstances.keySet());
        registryInstances.removeAll(currentInstances);
        for (String instanceName : registryInstances) {
            Instance instance = instanceService.findByName(instanceName);
            if (instance == null) {
                instance = instanceService.createInstance(instanceName);
                String port = ((HashMap<String, Object>) eurekaInstanceDetails.get(instanceName).get("port")).get("$").toString();
                instance.setAnnotation("ad.port", port);
                instance.setAnnotation("ad.ip", eurekaInstanceDetails.get(instanceName).get("ipAddr").toString());
                instanceService.saveInstance(instance);
            } else
                revisionService.createRevision(instance);

            Revision serviceRevision = componentRevisionMap.get(eurekaInstances.get(instanceName).getKey());
            Revision hardwareRevision = componentRevisionMap.get(eurekaInstances.get(instanceName).getValue());
            relationService.setInstanceRevisionRelations(componentRevisionMap.get(instanceName), serviceRevision, hardwareRevision);
        }

        // 4.2 remove disappeared instances and create new revisions for instances which were offline in between and now are online again (incl. creation of relations to corresponding service and hardware)
        for (String instanceName : currentInstances) {
            Revision revision = componentRevisionMap.get(instanceName);
            if (!eurekaInstances.containsKey(instanceName))
                revisionService.closeRevision(revision);
            else if (revision.getValidFrom().before(eurekainstanceUptime.get(instanceName))) {
                revisionService.closeRevision(revision);
                Revision instanceRevision = revisionService.createRevision(revision.getComponent());
                Revision serviceRevision = componentRevisionMap.get(eurekaInstances.get(instanceName).getKey());
                Revision hardwareRevision = componentRevisionMap.get(eurekaInstances.get(instanceName).getValue());
                relationService.setInstanceRevisionRelations(instanceRevision, serviceRevision, hardwareRevision);
            }
        }
    }


}
