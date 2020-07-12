Db backup service automatically for mysql
### Feature ###
1. Support how many days backup need
2. Support cloud storage eg.dropbox,google drive
3. Support FTP
 
### How to use ###
  1. Install jdk >=8  
  2. Change setting at src/main/resources/appliation.properties
  
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
  #backup.database.cloudStorageKey=
  ```
  3. ./mvnw clean package
  4. Run as service
    1. For windows. 
    Exectue following command at root dir
        1.dbbackupservice.exe install
        then dbbackupservice.exe start or reboot your pc 
       
        To verify
        dbbackupservice.exe status
        
        [Detail!](https://github.com/kohsuke/winsw)
    
    2. For Linux,
    update location jar location at db-backup-app.service.service <br /> 
    and then copy to /etc/systemd/system/<br /> 
    systemctl enable db-backup-app.service
          
