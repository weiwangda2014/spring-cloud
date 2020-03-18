package org.cloud.service.component;

import org.cloud.jpa.domain.component.Process;
import org.cloud.jpa.repository.component.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ProcessService extends ComponentBaseService<Process> {

    @Autowired
    private ProcessRepository processRepository;

    private Long highestId = 0L;

    @PostConstruct
    private void initialize() {
        super.initialize(processRepository);
        for (Process process : cachedComponents) {
            if (process.getId() > highestId)
                highestId = process.getId();
        }
    }

    public Process createProcess() {
        return super.createComponent(new Process(), "PROCESS_" + ++highestId);
    }

    public Process saveProcess(Process process) {
        return super.saveComponent(process);
    }

}
