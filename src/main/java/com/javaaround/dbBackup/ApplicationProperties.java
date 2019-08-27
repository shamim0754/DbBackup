package com.javaaround.dbBackup;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "backup.database")
public class ApplicationProperties {

    private String databaseIp;
    private String databaseName;
    private String databaseUserName;
    private String databaseUserPassword;
    private BackupClientInfo backupClientInfo;

}
