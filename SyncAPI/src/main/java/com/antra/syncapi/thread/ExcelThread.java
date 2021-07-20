package com.antra.syncapi.thread;


import com.antra.syncapi.pojo.ExcelResponse;
import com.antra.syncapi.pojo.ReportRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

public class ExcelThread implements Callable<ExcelResponse> {
    @Autowired
    private RestTemplate rs;
    private ExcelResponse response;
    private ReportRequest request;

    public ExcelThread(ReportRequest request) {
        this.response = new ExcelResponse();
        this.request = request;
        this.rs = new RestTemplate();
    }

    @Override
    public ExcelResponse call(){
        try {
            JSONObject json = new JSONObject(this.request);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
            this.response = this.rs.postForEntity("http://localhost:8888/excel", request, ExcelResponse.class).getBody();
        } catch (Exception e) {
            response.setReqId(request.getReqId());
            response.setFailed(true);
        }

        return this.response;
    }
}
