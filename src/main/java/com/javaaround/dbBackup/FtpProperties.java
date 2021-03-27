package com.javaaround.dbBackup;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ftp.host")
public class FtpProperties {

    private String url;
    private String username;
    private String password;
    private String dir;

}
