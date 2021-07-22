package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.entity.ExcelFileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExcelDatabaseRepo extends MongoRepository<ExcelFileEntity, String> {
}
