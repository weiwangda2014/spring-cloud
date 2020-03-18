package org.cloud.service.component;

import org.cloud.jpa.domain.component.Service;
import org.cloud.jpa.repository.component.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@org.springframework.stereotype.Service
public class ServiceService extends ComponentBaseService<Service> {

    @Autowired
    private ServiceRepository serviceRepository;

    @PostConstruct
    private void initialize() {
        super.initialize(serviceRepository);
    }

    public Service createService(String serviceName) {
        return super.createComponent(new Service(), serviceName);
    }

    public Service saveService(Service service) {
        return super.saveComponent(service);
    }
}
