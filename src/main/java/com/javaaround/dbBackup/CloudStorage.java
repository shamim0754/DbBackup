package com.javaaround.dbBackup;

import java.io.File;

public interface CloudStorage {
    void upload(String key, File file);
}
