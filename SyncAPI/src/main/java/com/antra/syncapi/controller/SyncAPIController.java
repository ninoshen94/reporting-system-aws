package com.antra.syncapi.controller;

import com.antra.syncapi.pojo.CombinedResponse;
import com.antra.syncapi.pojo.ReportRequest;
import com.antra.syncapi.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SyncAPIController {
    private static final Logger log = LoggerFactory.getLogger(SyncAPIController.class);

    private final SyncService service;

    @Autowired
    public SyncAPIController(SyncService service) {
        this.service = service;
    }

    @PostMapping("/sync")
    public CombinedResponse handleSyncRequest(@RequestBody @Validated ReportRequest request) {
        log.info("Got Request to generate report - sync: {}", request);
        CombinedResponse e = service.generateSyncFiles(request);
        return e;
    }
}
