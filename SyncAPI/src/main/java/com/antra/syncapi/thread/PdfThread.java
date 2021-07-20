package com.antra.syncapi.thread;


import com.antra.syncapi.controller.SyncAPIController;
import com.antra.syncapi.pojo.PDFResponse;
import com.antra.syncapi.pojo.ReportRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

public class PdfThread implements Callable<PDFResponse> {
    private static final Logger log = LoggerFactory.getLogger(PdfThread.class);
    private PDFResponse response;
    private ReportRequest request;
    private RestTemplate rs;

    public PdfThread(ReportRequest request) {
        this.response = new PDFResponse();
        this.request = request;
        rs= new RestTemplate();
    }

    @Override
    public PDFResponse call(){
        JSONObject json = new JSONObject(this.request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
        try {
            response = rs.postForEntity("http://localhost:9999/pdf", request, PDFResponse.class).getBody();
        } catch (Exception e) {
            response.setReqId(this.request.getReqId());
            response.setFailed(true);
            log.error("Got error inside the generation - {}", e.getMessage());
        }

        return this.response;
    }
}
