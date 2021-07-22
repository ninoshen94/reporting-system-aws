package com.antra.evaluation.reporting_system.pojo.report;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.LocalDateTime;

@DynamoDBTable(tableName="Pdf")
public class PDFFile {
    @DynamoDBHashKey(attributeName="Id")
    private String id;
    @DynamoDBAttribute(attributeName="FileName")
    private String fileName;
    @DynamoDBAttribute(attributeName="Location")
    private String fileLocation;
    @DynamoDBAttribute(attributeName="Submitter")
    private String submitter;
    @DynamoDBAttribute(attributeName="FileSize")
    private Long fileSize;
    @DynamoDBAttribute(attributeName="Description")
    private String description;
    @DynamoDBAttribute(attributeName="Time")
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    private LocalDateTime generatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

    @Override
    public String toString() {
        return "PDFFile{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileLocation='" + fileLocation + '\'' +
                ", submitter='" + submitter + '\'' +
                ", fileSize=" + fileSize +
                ", description='" + description + '\'' +
                ", generatedTime=" + generatedTime +
                '}';
    }
}
