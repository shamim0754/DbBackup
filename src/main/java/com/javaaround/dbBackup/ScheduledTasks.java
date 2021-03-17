package com.javaaround.dbBackup;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.nonNull;
import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    @Value("${how.many.days.backup.you.need}")
    private Integer dayBackupNeed;

    @Scheduled(cron = "${cron.expression}")
    //@Scheduled(fixedRate = 15000)
    public void reportCurrentTime() {
        applicationProperties.getDatabases().forEach(databaseName -> {
            try {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_h_m_s_a");
                String todaysDateString = LocalDateTime.now().format(formatter);

                File backupFile = new File(applicationProperties.getBackupClientInfo().getBackupLocation() + "/" + databaseName + "_" +
                        todaysDateString
                        + ".sql");

                //Create the file
                if (backupFile.createNewFile()) {
                    System.out.println("File is created!");
                } else {
                    System.out.println("File already exists.");
                }
                //if(backupFile.createNewFile()){
                Process runtimeProcess = Runtime.getRuntime().exec(applicationProperties.getBackupClientInfo().getToolLocation() + "/mysqldump --single-transaction -u" + applicationProperties.getDatabaseUserName() + " -h" + applicationProperties.getDatabaseIp() + " -p" + applicationProperties.getDatabaseUserPassword() + " -B " + databaseName + " -r " + backupFile.getAbsolutePath());
                int processComplete = runtimeProcess.waitFor();

                /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
                if (processComplete == 0) {
                    System.out.println("Backup Complete");
                    if(nonNull(applicationProperties.getCloudStorage()) && !applicationProperties.getCloudStorage().isEmpty()){
                        CloudStorage cloudStorage = null;
                        if(applicationProperties.getCloudStorage().equals("dropbox"))
                            cloudStorage   = new DropBoxCloudStorage();
                        else if(applicationProperties.getCloudStorage().equals("googledrive"))
                            cloudStorage   = new GoogleDriveCloudStorage();
                        System.out.println("trying upload cloud storage");
                        cloudStorage.upload(applicationProperties.getCloudStorageKey(), backupFile);

                        System.out.println("Uploading cloud storage is done");
                    }
                } else {
                    System.out.println("Backup Failure");
                    System.out.println("Failed to execute the  command:  due to the following error(s):");
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(runtimeProcess.getErrorStream()))) {
                        String line;
                        while ((line = b.readLine()) != null)
                            System.out.println(line);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        oldestFileRemove();
        try {
            deleteFilesOlderThanXDays();
        } catch (DbxException e) {
            e.printStackTrace();
        }


    }
    private long deleteFilesOlderThanXDays() throws DbxException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, applicationProperties.getCloudStorageKey());
        //ALPHA - add protection against going above long.MAXIMUM
        long numberFilesDeleted = 0;
        ListFolderBuilder listFolderBuilder = client.files().listFolderBuilder("");
        ListFolderResult result = listFolderBuilder.withRecursive(true).start();

        while (!result.getHasMore()) {
            if (result != null) {
                for (Metadata entry : result.getEntries()) {
//                    System.out.println("Analysing: " + entry.getPathLower());
                    if (entry instanceof FileMetadata) {
                        Date dateOfFile = ((FileMetadata) entry).getServerModified();
                        //Check to see if the current file is too old
                        if (DAYS.between(new Date().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime(), dateOfFile.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()) > dayBackupNeed) {
                            client.files().deleteV2(entry.getPathLower());
                            //entry.deleteFile(entry.getPathLower());
                            numberFilesDeleted++;
                        }
                    }
                }
                if (!result.getHasMore()) {
                    return numberFilesDeleted;
                }
                try {
                    result = client.files().listFolderContinue(result.getCursor());
                } catch (DbxException e) {
                    //ALPHA - TIDY UP HERE.  CATCHING or THROWING??? - GDPR - Note what you're logging
                    //  log.info("Could NOT get listFolderContinue");
                }
            }
        }
        return numberFilesDeleted;
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
