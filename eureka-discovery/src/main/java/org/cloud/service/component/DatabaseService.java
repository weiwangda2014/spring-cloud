package org.cloud.service.component;

import org.cloud.jpa.domain.component.Database;
import org.cloud.jpa.repository.component.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DatabaseService extends ComponentBaseService<Database> {

    @Autowired
    private DatabaseRepository databaseRepository;

    @PostConstruct
    private void initialize() {
        super.initialize(databaseRepository);
    }

    public Database createHardware(String databaseName) {
        return super.createComponent(new Database(), databaseName);
    }

    public Database saveHardware(Database database) {
        return super.saveComponent(database);
    }
}
