package ru.vsu.csf.sqlportal.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.csf.sqlportal.dto.response.DbLocationResponse;
import ru.vsu.csf.sqlportal.model.Database;

import java.io.IOException;
import java.util.List;


public interface ExcelHelperService {
    DbLocationResponse save(String dbName, MultipartFile file) throws IOException;
    Page<DbLocationResponse> getAllDBs(int page, int size, boolean sort);
    List<DbLocationResponse> getAllDBs();
    Page<DbLocationResponse> getAllDBsByAuthorId(Long authorId, int page, int size, boolean sort);
    InputStreamResource getExcelDB(Long id) throws IOException;
    Database getDB(Long db_id) throws IOException;
    void deleteDB(Long db_id);
}
