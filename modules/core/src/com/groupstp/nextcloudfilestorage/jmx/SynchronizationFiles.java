package com.groupstp.nextcloudfilestorage.jmx;

import com.groupstp.nextcloudfilestorage.service.NextCloudService;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.security.app.Authenticated;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@Component("nextcloudfilestorage_SynchronizationFiles")
public class SynchronizationFiles implements SynchronizationFilesMBean {
    @Inject
    DataManager dataManager;

    @Inject
    NextCloudService nextCloudService;

    @Inject
    FileStorageAPI fileStorageAPI;

    @Authenticated
    @Override
    public String syncAllFiles() {
        List<FileDescriptor> fileDescriptors = dataManager.load(FileDescriptor.class).list();
        return syncFilesByList(fileDescriptors);
    }

    @Authenticated
    @Override
    public String syncFilesByYear(String year) {
        Preconditions.checkNotEmptyString(year, "Year is required");
        Year yearUtil = Year.parse(year);
        LocalDate start = yearUtil.atMonth(Month.JANUARY).atDay(1);
        LocalDate end = yearUtil.atMonth(Month.DECEMBER).atEndOfMonth();
        List<FileDescriptor> fileDescriptors = dataManager.load(FileDescriptor.class)
                .query("select fd from sys$FileDescriptor fd where fd.createDate between :dateStart and :dateEnd")
                .parameter("dateStart", Date.valueOf(start.toString()))
                .parameter("dateEnd", Date.valueOf(end.toString()))
                .list();
        return syncFilesByList(fileDescriptors);
    }

    @Authenticated
    @Override
    public String syncFilesByMonth(String month) {
        Preconditions.checkNotEmptyString(month, "Month is required");
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        List<FileDescriptor> fileDescriptors = dataManager.load(FileDescriptor.class)
                .query("select fd from sys$FileDescriptor fd where fd.createDate between :dateStart and :dateEnd")
                .parameter("dateStart", Date.valueOf(start.toString()))
                .parameter("dateEnd", Date.valueOf(end.toString()))
                .list();
        return syncFilesByList(fileDescriptors);
    }

    @Authenticated
    @Override
    public String syncFilesByDay(String day) {
        Preconditions.checkNotEmptyString(day, "Day is required");
        List<FileDescriptor> fileDescriptors = dataManager.load(FileDescriptor.class)
                .query("select fd from sys$FileDescriptor fd where fd.createDate = :date")
                .parameter("date", Date.valueOf(day))
                .list();
        return syncFilesByList(fileDescriptors);
    }

    /**
     * Synchronization files by list
     *
     * @param fileDescriptors list files descriptor
     * @return synchronization files
     */
    private String syncFilesByList(List<FileDescriptor> fileDescriptors) {
        StringBuilder result = new StringBuilder();
        for (FileDescriptor fileDescriptor : fileDescriptors) {
            try {
                if (fileStorageAPI.fileExists(fileDescriptor)) {
                    nextCloudService.saveStream(fileDescriptor, fileStorageAPI.openStream(fileDescriptor));
                    result.append(nextCloudService.getSourceFileName(fileDescriptor)).append("\n");
                }
            } catch (FileStorageException e) {
                e.printStackTrace();
            }
        }
        return "Synchronization files: \n" + (result.toString().isEmpty() ? "Files not found for current period" : result.toString());
    }
}