package com.antra.syncapi.service;

import com.antra.syncapi.controller.SyncAPIController;
import com.antra.syncapi.pojo.CombinedResponse;
import com.antra.syncapi.pojo.ExcelResponse;
import com.antra.syncapi.pojo.PDFResponse;
import com.antra.syncapi.pojo.ReportRequest;
import com.antra.syncapi.thread.ExcelThread;
import com.antra.syncapi.thread.PdfThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Service
public class SyncServiceImpl implements SyncService{
    private static final Logger log = LoggerFactory.getLogger(SyncAPIController.class);
    @Override
    public CombinedResponse generateSyncFiles(ReportRequest request) {
        log.info("Begin to generate files");
        ExcelThread excel = new ExcelThread(request);
        PdfThread pdf = new PdfThread(request);
        FutureTask<ExcelResponse> excelFuture = new FutureTask<>(excel);
        FutureTask<PDFResponse> pdfFuture = new FutureTask<>(pdf);
        Thread excelThread = new Thread(excelFuture);
        Thread pdfThread = new Thread(pdfFuture);
        excelThread.start();
        pdfThread.start();
        ExcelResponse excelResult = null;
        PDFResponse pdfResult = null;

        try {
            excelResult = excelFuture.get();
        } catch (ExecutionException e) {
            log.error("Thread execution error: Excel - {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("Thread interrupted before finished: Excel - {}", e.getMessage());
        }

        try {
            pdfResult = pdfFuture.get();
        } catch (ExecutionException e) {
            log.error("Thread execution error: PDF - {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("Thread interrupted before finished: PDF - {}", e.getMessage());
        }

        log.info("Got the excel: {}", excelResult);
        log.info("Got the pdf: {}", pdfResult);
        return new CombinedResponse(excelResult, pdfResult);
    }
}
