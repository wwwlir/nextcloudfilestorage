package com.groupstp.nextcloudfilestorage.service;

import com.groupstp.nextcloudfilestorage.config.NextCloudConfig;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.entity.FileDescriptor;
import org.aarboard.nextcloud.api.ServerConfig;
import org.aarboard.nextcloud.api.webdav.Files;
import org.aarboard.nextcloud.api.webdav.Folders;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

@Service(NextCloudService.NAME)
public class NextCloudServiceBean implements NextCloudService {
    @Inject
    protected NextCloudConfig nextCloudConfig;

    /**
     * Create subfolders
     *
     * @param fileDescriptor file descriptor
     */
    private void createDirectory(FileDescriptor fileDescriptor) {
        Folders folders = new Folders(getConfig());
        String[] dirsArray = getStorageDir(fileDescriptor.getCreateDate()).substring(1).split("/");
        StringBuilder createdDir = new StringBuilder();
        for (String dir : dirsArray) {
            createdDir.append("/");
            createdDir.append(dir);
            String directoryUrl = createdDir.toString();
            if (!folders.exists(directoryUrl)) {
                folders.createFolder(directoryUrl);
            }
        }
    }

    @Override
    public void saveStream(FileDescriptor fileDescriptor, InputStream inputStream) {
        if (!fileExists(fileDescriptor)) {
            createDirectory(fileDescriptor);
            Files files = new Files(getConfig());
            files.uploadFile(inputStream, resolveFileName(fileDescriptor));
        }
    }

    @Override
    public InputStream openStream(FileDescriptor fileDescriptor) throws IOException {
        Files files = new Files(getConfig());
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + getFileName(fileDescriptor));
        files.downloadFile(resolveFileName(fileDescriptor), file.getParent());
        return new FileInputStream(file);
    }

    @Override
    public byte[] loadFile(FileDescriptor fileDescriptor) throws IOException {
        Files files = new Files(getConfig());
        File file = new File(System.getProperty("java.io.tmpdir") + getFileName(fileDescriptor));
        files.downloadFile(resolveFileName(fileDescriptor), file.getParent());
        return java.nio.file.Files.readAllBytes(file.toPath());
    }

    @Override
    public boolean fileExists(FileDescriptor fileDescriptor) {
        Files files = new Files(getConfig());
        return files.fileExists(resolveFileName(fileDescriptor));
    }

    @Override
    public void removeFile(FileDescriptor fileDescriptor) {
        Files files = new Files(getConfig());
        files.removeFile(resolveFileName(fileDescriptor));
    }

    @Override
    public ServerConfig getConfig() {
        Preconditions.checkNotEmptyString(nextCloudConfig.getUsername(), "Username for NextCloud is required");
        Preconditions.checkNotEmptyString(nextCloudConfig.getPassword(), "Password for NextCloud is required");
        Preconditions.checkNotEmptyString(nextCloudConfig.getServerName(), "Server name for NextCloud is required");
        return new ServerConfig(nextCloudConfig.getServerName(),
                nextCloudConfig.getUseHTTPS(),
                nextCloudConfig.getPort(),
                nextCloudConfig.getUsername(),
                nextCloudConfig.getPassword());
    }

    /**
     * Generate path directory
     *
     * @param createDate date
     * @return path directory
     */
    private String getStorageDir(Date createDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return "/" + String.format("%d/%s/%s",
                year, StringUtils.leftPad(String.valueOf(month), 2, '0'), StringUtils.leftPad(String.valueOf(day), 2, '0'));
    }

    /**
     * Get file name
     *
     * @param fileDescriptor file descriptor
     * @return file name
     */
    private String getFileName(FileDescriptor fileDescriptor) {
        if (StringUtils.isNotBlank(fileDescriptor.getExtension())) {
            return fileDescriptor.getId().toString() + "." + fileDescriptor.getExtension();
        } else {
            return fileDescriptor.getId().toString();
        }
    }

    @Override
    public String getSourceFileName(FileDescriptor fileDescriptor) {
        return fileDescriptor.getName();
    }

    /**
     * Get the path to file
     *
     * @param fileDescriptor file descriptor
     * @return path to file
     */
    private String resolveFileName(FileDescriptor fileDescriptor) {
        return getStorageDir(fileDescriptor.getCreateDate()) + "/" + getFileName(fileDescriptor);
    }
}