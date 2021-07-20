package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.entity.ExcelFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcelDatabaseRepo extends JpaRepository<ExcelFileEntity, String> {
}
