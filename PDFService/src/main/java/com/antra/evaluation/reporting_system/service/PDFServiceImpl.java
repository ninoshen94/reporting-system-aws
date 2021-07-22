package com.antra.evaluation.reporting_system.service;

import com.amazonaws.services.s3.AmazonS3;
import com.antra.evaluation.reporting_system.pojo.api.PDFRequest;
import com.antra.evaluation.reporting_system.entity.PDFFile;
import com.antra.evaluation.reporting_system.repo.PDFDatabaseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PDFServiceImpl implements PDFService {

    private static final Logger log = LoggerFactory.getLogger(PDFServiceImpl.class);


    private final PDFDatabaseRepo repository;

    private final PDFGenerator generator;

    private final AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String s3Bucket;

    public PDFServiceImpl(PDFDatabaseRepo repository, PDFGenerator generator, AmazonS3 s3Client) {
        this.repository = repository;
        this.generator = generator;
        this.s3Client = s3Client;
    }

    @Override
    public PDFFile createPDF(final PDFRequest request) {
        PDFFile file = new PDFFile();
        file.setId("Pdf-" + UUID.randomUUID().toString());
        file.setSubmitter(request.getSubmitter());
        file.setDescription(request.getDescription());
        file.setGeneratedTime(LocalDateTime.now());

        PDFFile generatedFile= generator.generate(request);

        File temp = new File(generatedFile.getFileLocation());
        log.debug("Upload temp file to s3 {}", generatedFile.getFileLocation());
        s3Client.putObject(s3Bucket,file.getId(),temp);
        log.debug("Uploaded");

        file.setFileLocation(String.join("/",s3Bucket,file.getId()));
        file.setFileSize(generatedFile.getFileSize());
        file.setFileName(generatedFile.getFileName());
        repository.save(file);


        log.debug("clear tem file {}", file.getFileLocation());
        if(temp.delete()){
            log.debug("cleared");
        }

        return file;
    }

    @Override
    public PDFFile deleteFile(String id) throws FileNotFoundException {
        PDFFile pdf = repository.findById(id).orElseThrow(FileNotFoundException::new);
        repository.deleteById(id);

        s3Client.deleteObject(s3Bucket, id);
        log.info("File has been deleted, id: {}", id);
        return pdf;
    }
}
