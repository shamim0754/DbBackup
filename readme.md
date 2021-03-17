Db backup service automatically for mysql
### Feature ###
1. Support how many days backup need
2. Support cloud storage eg.dropbox,google drive
3. Support FTP
 
### How to use ###
  1. Install jdk >=8  
  
  2. Change setting at src/main/resources/application.properties
  
  ```java
  cron.expression=0 0 11,19 * * *
  how.many.days.backup.you.need = 30
  #server than you want backup
  backup.database.databaseIp=localhost
  backup.database.databases[0]=erp_app
  #backup.database.databases[1]=erp_app_composite2
  backup.database.databaseUserName=root
  backup.database.databaseUserPassword=12345
  #client
  backup.database.backupClientInfo.toolLocation=C:/Program Files/MySQL/MySQL Server 5.7/bin
  backup.database.backupClientInfo.backupLocation=e:/java/practice/backup
  backup.database.cloudStorage=dropbox
  backup.database.cloudStorageKey=
  ```
  3. ./mvnw clean package
  4. Run as service <br />
    1. For windows. <br />
    Execute following command at root dir <br />
        1. `dbbackupservice.exe install`
        2. `dbbackupservice.exe start` 
       
        To verify
        `dbbackupservice.exe status`
        
        [Detail!](https://github.com/kohsuke/winsw)<br />
    2. For Linux,
        1. Update app jar location at `db-backup-app.service` <br /> 
        2. Copy db-backup-app.service to /etc/systemd/system/<br /> 
        2. Copy db-backup-app.service.service to /etc/systemd/system/<br /> 
        cp db-backup-app.service.service /etc/systemd/system/<br /> 
        3. systemctl enable db-backup-app.service
        4. systemctl start db-backup-app.service
   <br/>
       if any further change on db-backup-app.service
       then systemctr daemon-reload
          
