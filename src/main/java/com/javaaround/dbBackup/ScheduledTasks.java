package com.javaaround.dbBackup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;

import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    @Value("${how.many.days.backup.you.need}")
    private Integer dayBackupNeed;

   // @Scheduled(cron = "${cron.expression}")
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        applicationProperties.getDatabases().forEach(databaseName -> {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_h_m_s_a");
                String todaysDateString = LocalDateTime.now().format(formatter).toString();

                File backupFile = new File(applicationProperties.getBackupClientInfo().getBackupLocation() + "/" + databaseName + "_" +
                        todaysDateString
                        + ".sql");

                /*File file = new File("e://java//practice//backup//blog_"+now.format(dateTimeFormatter)+".sql");*/

                //Create the file
                if (backupFile.createNewFile()) {
                    System.out.println("File is created!");
                } else {
                    System.out.println("File already exists.");
                }
                //if(backupFile.createNewFile()){
                Process runtimeProcess = Runtime.getRuntime().exec(applicationProperties.getBackupClientInfo().getToolLocation() + "/mysqldump -u" + applicationProperties.getDatabaseUserName() + " -h" + applicationProperties.getDatabaseIp() + " -p" + applicationProperties.getDatabaseUserPassword() + " -B " + databaseName + " -r " + backupFile.getAbsolutePath());
                int processComplete = runtimeProcess.waitFor();

                /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
                if (processComplete == 0) {
                    System.out.println("Backup Complete");
                } else {
                    System.out.println("Backup Failure");
                }
                //   }
                oldestFileRemove();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    private void oldestFileRemove() {
        LocalDate today = LocalDate.now();
        LocalDate eailer = today.minusDays(dayBackupNeed);

        Date threshold = Date.from(eailer.atStartOfDay(ZoneId.systemDefault()).toInstant());
        AgeFileFilter filter = new AgeFileFilter(threshold);

        File targetDir  = new File(applicationProperties.getBackupClientInfo().getBackupLocation());
        Iterator<File> filesToDelete =
                FileUtils.iterateFiles(targetDir , filter, TRUE);
        while (filesToDelete.hasNext()){
            File aFile  = filesToDelete.next();
            aFile.delete();
        }
    }
}
