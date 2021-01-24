package ru.vsu.csf.sqlportal.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.csf.sqlportal.dto.request.CreateDBRequest;
import ru.vsu.csf.sqlportal.dto.response.ExhaustedDBResponse;
import ru.vsu.csf.sqlportal.model.Database;

import java.io.IOException;


public interface ExcelHelperService {
    ExhaustedDBResponse save(CreateDBRequest createDbRequest, MultipartFile file) throws IOException;
    InputStreamResource getExcelDB(Long id) throws IOException;
    Database getDB(Long db_id) throws IOException;
}
