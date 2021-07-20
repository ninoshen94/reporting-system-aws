package com.antra.report.client.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name="report_request")
public class ReportRequestEntity {
    @Id
    private String reqId;
    private String submitter;
    private String description;
    private String headers;
    private String data;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private boolean isSync;

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER) // default is already eager here
    @JoinColumn(name="pdf_report_id")
    private PDFReportEntity pdfReport;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER) // default is already eager here
    @JoinColumn(name="excel_report_id")
    private ExcelReportEntity excelReport;

    public PDFReportEntity getPdfReport() {
        return pdfReport;
    }

    public void setPdfReport(PDFReportEntity pdfReport) {
        this.pdfReport = pdfReport;
    }

    public ExcelReportEntity getExcelReport() {
        return excelReport;
    }

    public void setExcelReport(ExcelReportEntity excelReport) {
        this.excelReport = excelReport;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
}
