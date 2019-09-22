package com.javaaround.dbBackup;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DropBoxCloudStorage implements  CloudStorage {
    @Override
    public void upload(String key, File backupFile) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, key);
        try {
            InputStream in = new FileInputStream(backupFile);
            FileMetadata metadata = client.files().uploadBuilder("/" + backupFile.getName())
                    .uploadAndFinish(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
