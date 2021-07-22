package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.PDFRequest;
import com.antra.evaluation.reporting_system.entity.PDFFile;

import java.io.FileNotFoundException;

public interface PDFService {
    PDFFile createPDF(PDFRequest request);

    PDFFile deleteFile(String id) throws FileNotFoundException;
}
