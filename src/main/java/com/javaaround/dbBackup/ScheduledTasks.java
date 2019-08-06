package com.javaaround.dbBackup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.Executors;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

   // @Scheduled(cron = "${cron.expression}")
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        try {
            String dbName = "blog";
            String dbUser = "root";
            String dbPass = "12345";
            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase().startsWith("windows");
            System.out.println(isWindows);

//            File backupFile = new File("E:/java/practice/backup//blog_"+ LocalDateTime.now()+".sql");

            /*File file = new File("e://java//practice//backup//blog_"+now.format(dateTimeFormatter)+".sql");

//Create the file
            if (file.createNewFile())
            {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }*/
            //if(backupFile.createNewFile()){
            Process runtimeProcess = Runtime.getRuntime().exec("C:/Program Files/MySQL/MySQL Server 5.7/bin/mysqldump.exe -uroot -p12345 -B blog -r " + "e:/java/practice/backup/blog.sql");
            int processComplete = runtimeProcess.waitFor();

            /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
            if (processComplete == 0) {
                System.out.println("Backup Complete");
            } else {
                System.out.println("Backup Failure");
            }
         //   }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
