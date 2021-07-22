package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.exception.FileGenerationException;
import com.antra.evaluation.reporting_system.pojo.api.ErrorResponse;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.entity.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ExcelGenerationController {

    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);
    ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping(value = "/excel", consumes = "application/json", produces = "application/json")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) {
        log.debug("Got Request to Create Single Sheet Excel:{}", request);
        ExcelFile fileInfo = excelService.generateFile(request, false);
        ExcelResponse response = new ExcelResponse();
        BeanUtils.copyProperties(fileInfo, response);
        response.setFileDownloadLink(fileInfo.getFileLocation());
        response.setFileLocation(fileInfo.getFileLocation());
        response.setReqId(request.getReqId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) {
        log.debug("Got Request to Create Multi-Sheet Excel:{}", request);
        //Double check if the header has splitBy field.
        if(!request.getHeaders().contains(request.getSplitBy())){
            throw new InvalidParameterException("No such header for splitting the sheets");
        }
        ExcelFile fileInfo = excelService.generateFile(request, true);
        ExcelResponse response = new ExcelResponse();
        BeanUtils.copyProperties(fileInfo, response);
        response.setFileLocation(fileInfo.getFileLocation());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) throws FileNotFoundException {
        log.debug("Got Request to Delete File:{}", id);
        var response = new ExcelResponse();
        ExcelFile fileDeleted = excelService.deleteFile(id);
        BeanUtils.copyProperties(fileDeleted, response);
        response.setFileLocation(fileDeleted.getFileLocation());
        log.debug("File Deleted:{}", fileDeleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFound(Exception e) {
        log.error("The file doesn't exist", e);
        return new ResponseEntity<>(new ErrorResponse("The file doesn't exist", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileGenerationException.class)
    public ResponseEntity<ErrorResponse> handleFileGenerationExceptions(Exception e) {
        log.error("Cannot Generate Excel File", e);
        return new ResponseEntity<>(new ErrorResponse("Cannot Generate Excel File", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownExceptions(Exception e) {
        log.error("Something is wrong", e);
        return new ResponseEntity<>(new ErrorResponse("Something is wrong", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
