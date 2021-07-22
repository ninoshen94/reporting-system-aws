package com.antra.evaluation.reporting_system.service;

import com.amazonaws.services.s3.AmazonS3;
import com.antra.evaluation.reporting_system.exception.FileGenerationException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.entity.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelDatabaseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private final ExcelDatabaseRepo excelRepository;

    private final ExcelGenerationServiceImpl excelGenerationService;
    private final AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String s3Bucket;

    public ExcelServiceImpl(AmazonS3 amazonS3, ExcelDatabaseRepo excelRepository, ExcelGenerationServiceImpl excelGenerationService) {
        this.excelRepository = excelRepository;
        this.excelGenerationService = excelGenerationService;
        this.s3Client = amazonS3;
    }

    @Override
    public InputStream getExcelBodyById(String id) throws FileNotFoundException {
        Optional<ExcelFile> fileInfo = excelRepository.findById(id);
        return new FileInputStream(fileInfo.orElseThrow(FileNotFoundException::new).getFileLocation());
    }

    @Override
    public ExcelFile generateFile(ExcelRequest request, boolean multisheet) {
        ExcelFile file = new ExcelFile();
        file.setFileId("Excel- " + UUID.randomUUID().toString());
        file.setGeneratedTime(LocalDateTime.now());
        ExcelData data = new ExcelData();
        data.setTitle(request.getDescription());
        data.setFileId(file.getFileId());
        data.setSubmitter(request.getSubmitter());
        if(multisheet){
            data.setSheets(generateMultiSheet(request));
        }else {
            data.setSheets(generateSheet(request));
        }
        try {
            ExcelFile generatedFile = excelGenerationService.generateExcelReport(data);
            File temp = new File(generatedFile.getFileLocation());
            log.debug("Upload temp file to s3 {}", generatedFile.getFileLocation());
            s3Client.putObject(s3Bucket,file.getFileId(),temp);
            log.debug("Uploaded");

            file.setFileLocation(String.join("/",s3Bucket,file.getFileId()));
            file.setFileSize(generatedFile.getFileSize());
            file.setFileName(generatedFile.getFileName());
            excelRepository.save(file);
            if(temp.delete()){
                log.debug("temp cleared");
            }
        } catch (IOException e) {
            log.error("Error in generateFile()", e);
            throw new FileGenerationException(e);
        }
        return file;
    }

    @Override
    public ExcelFile deleteFile(String id) throws FileNotFoundException {
        ExcelFile excelFile = excelRepository.findById(id).orElseThrow(FileNotFoundException::new);
        excelRepository.deleteById(id);
        s3Client.deleteObject(s3Bucket, id);
        log.info("File has been deleted, id: {}", id);
        return excelFile;
    }

    private List<ExcelDataSheet> generateSheet(ExcelRequest request) {
        List<ExcelDataSheet> sheets = new ArrayList<>();
        ExcelDataSheet sheet = new ExcelDataSheet();
        sheet.setHeaders(request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList()));
        sheet.setDataRows(request.getData().stream().map(listOfString -> (List<Object>) new ArrayList<Object>(listOfString)).collect(Collectors.toList()));
        sheet.setTitle("sheet-1");
        sheets.add(sheet);
        return sheets;
    }

    private List<ExcelDataSheet> generateMultiSheet(ExcelRequest request) {
        List<ExcelDataSheet> sheets = new ArrayList<>();
        int index = request.getHeaders().indexOf(((MultiSheetExcelRequest) request).getSplitBy());
        Map<String, List<List<String>>> splittedData = request.getData().stream().collect(Collectors.groupingBy(row -> (String)row.get(index)));
        List<ExcelDataHeader> headers = request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList());
        splittedData.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(
                entry ->{
                    ExcelDataSheet sheet = new ExcelDataSheet();
                    sheet.setHeaders(headers);
                    sheet.setDataRows(entry.getValue().stream().map(listOfString -> (List<Object>) new ArrayList<Object>(listOfString)).collect(Collectors.toList()));
                    sheet.setTitle(entry.getKey());
                    sheets.add(sheet);
                }
        );
        return sheets;
    }
}
