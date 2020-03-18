package org.cloud.service.component;

import org.cloud.jpa.domain.component.*;
import org.cloud.jpa.domain.component.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ComponentService {

    private Map<Long, Component> cachedComponentMap = new ConcurrentHashMap<>();

    @Autowired
    ActivityService activityService;

    @Autowired
    HardwareService hardwareService;

    @Autowired
    InstanceService instanceService;

    @Autowired
    ProcessService processService;

    @Autowired
    ServiceService serviceService;

    void registerComponent(Component component) {
        cachedComponentMap.put(component.getId(), component);
    }

    void unregisterComponent(Component component) {
        cachedComponentMap.remove(component.getId());
    }

    void registerComponents(List<? extends Component> components) {
        for (Component component : components)
            cachedComponentMap.put(component.getId(), component);
    }

    public Component findById(Long id) {
        return cachedComponentMap.get(id);
    }

    public List<Component> findAll() {
        return new ArrayList<>(cachedComponentMap.values());
    }

    public Component saveComponent(Component component) throws Exception {
        if (component instanceof Activity)
            return activityService.saveActivity((Activity) component);
        else if (component instanceof Hardware)
            return hardwareService.saveHardware((Hardware) component);
        else if (component instanceof Process)
            return processService.saveProcess((Process) component);
        else if (component instanceof org.cloud.jpa.domain.component.Service)
            return serviceService.saveService((org.cloud.jpa.domain.component.Service) component);
        else if (component instanceof Instance)
            return instanceService.saveInstance((Instance) component);

        throw new Exception("Component type has no defined save method: " + component.getClass().toString());
    }
}
