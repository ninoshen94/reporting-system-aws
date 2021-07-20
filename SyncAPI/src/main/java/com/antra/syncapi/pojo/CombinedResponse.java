package com.antra.syncapi.pojo;

public class CombinedResponse {
    private ExcelResponse excelResponse;
    private PDFResponse pdfResponse;

    public CombinedResponse(ExcelResponse excelResponse, PDFResponse pdfResponse) {
        this.excelResponse = excelResponse;
        this.pdfResponse = pdfResponse;
    }

    public ExcelResponse getExcelResponse() {
        return excelResponse;
    }

    public void setExcelResponse(ExcelResponse excelResponse) {
        this.excelResponse = excelResponse;
    }

    public PDFResponse getPdfResponse() {
        return pdfResponse;
    }

    public void setPdfResponse(PDFResponse pdfResponse) {
        this.pdfResponse = pdfResponse;
    }
}
