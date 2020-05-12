package com.groupstp.nextcloudfilestorage.jmx;

import com.haulmont.cuba.core.sys.jmx.JmxRunAsync;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(description = "Synchronization of the current file storage to NextCloud")
public interface SynchronizationFilesMBean {
    /**
     * Synchronization of all files
     *
     * @return
     */
    @JmxRunAsync
    @ManagedOperation(description = "Synchronization of all files")
    String syncAllFiles();

    /**
     * Synchronization of all files for the year
     *
     * @param year
     * @return
     */
    @JmxRunAsync
    @ManagedOperation(description = "Synchronization of all files for the year")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "year", description = "value of pattern yyyy")})
    String syncFilesByYear(String year);

    /**
     * Synchronization of all files per month
     *
     * @param month
     * @return
     */
    @JmxRunAsync
    @ManagedOperation(description = "Synchronization of all files per month")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "month", description = "value of pattern yyyy-MM")})
    String syncFilesByMonth(String month);

    /**
     * Synchronize all files in a day
     *
     * @param day
     * @return
     */
    @JmxRunAsync
    @ManagedOperation(description = "Synchronize all files in a day")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "day", description = "value of pattern yyyy-MM-dd")})
    String syncFilesByDay(String day);
}