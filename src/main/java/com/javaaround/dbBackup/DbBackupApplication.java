package com.javaaround.dbBackup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DbBackupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbBackupApplication.class, args);
	}

}
