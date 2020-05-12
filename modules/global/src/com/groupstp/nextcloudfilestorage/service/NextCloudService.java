package com.groupstp.nextcloudfilestorage.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import org.aarboard.nextcloud.api.ServerConfig;

import java.io.IOException;
import java.io.InputStream;

public interface NextCloudService {
    String NAME = "nextcloudfilestorage_NextCloudService";

    /**
     * Checking if a file exists
     *
     * @param fileDescriptor
     * @return
     */
    boolean fileExists(FileDescriptor fileDescriptor);

    /**
     * Upload file from InputStream
     *
     * @param fileDescriptor file descriptor
     * @param inputStream
     * @return
     */
    void saveStream(FileDescriptor fileDescriptor, InputStream inputStream);

    /**
     * Download file
     *
     * @param fileDescriptor file descriptor
     * @return InputStream
     */
    InputStream openStream(FileDescriptor fileDescriptor) throws IOException;

    /**
     * Download file
     *
     * @param fileDescriptor file descriptor
     * @return byte[]
     */
    byte[] loadFile(FileDescriptor fileDescriptor) throws IOException;

    /**
     * Remove file
     *
     * @param fileDescriptor file descriptor
     */
    void removeFile(FileDescriptor fileDescriptor);

    /**
     * Get config NextCloud
     *
     * @return config
     */
    ServerConfig getConfig();

    /**
     * Get source file name
     *
     * @param fileDescriptor file descriptor
     * @return file name
     */
    String getSourceFileName(FileDescriptor fileDescriptor);
}