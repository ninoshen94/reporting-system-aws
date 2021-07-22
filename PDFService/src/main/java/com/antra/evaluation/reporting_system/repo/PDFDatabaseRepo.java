package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.entity.PDFFile;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface PDFDatabaseRepo extends CrudRepository<PDFFile, String> {
}
