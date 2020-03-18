package org.cloud.service.component;

import org.cloud.jpa.domain.component.Hardware;
import org.cloud.jpa.repository.component.HardwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class HardwareService extends ComponentBaseService<Hardware> {

    @Autowired
    private HardwareRepository hardwareRepository;

    @PostConstruct
    private void initialize() {
        super.initialize(hardwareRepository);
    }

    public Hardware createHardware(String hardwareName) {
        return super.createComponent(new Hardware(), hardwareName);
    }

    public Hardware saveHardware(Hardware hardware) {
        return super.saveComponent(hardware);
    }
}
