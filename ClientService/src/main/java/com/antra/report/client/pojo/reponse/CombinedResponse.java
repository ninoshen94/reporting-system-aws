package com.antra.report.client.pojo.reponse;

public class CombinedResponse {
    private ExcelResponse excelResponse;
    private PDFResponse pdfResponse;

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
