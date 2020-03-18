package org.cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import zipkin.storage.StorageComponent;
import zipkin.storage.mysql.MySQLStorage;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

/*
@Service
public class UserActivityService {
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
        return mysqlStorage;
    }


}
*/