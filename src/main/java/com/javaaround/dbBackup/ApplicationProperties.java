package com.javaaround.dbBackup;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "backup.database")
public class ApplicationProperties {

    private String databaseIp;
    private List<String> databases;
    private String databaseUserName;
    private String databaseUserPassword;
    private BackupClientInfo backupClientInfo;
    private String cloudStorage;
    private String cloudStorageKey;

}
