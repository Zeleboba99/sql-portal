package ru.vsu.csf.sqlportal.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.csf.sqlportal.dto.response.DbInfoResponse;
import ru.vsu.csf.sqlportal.model.Database;

import java.io.IOException;
import java.util.List;


public interface ExcelHelperService {
    DbInfoResponse save(String dbName, MultipartFile schemaImage, MultipartFile file) throws IOException;
    Page<DbInfoResponse> getAllDBs(int page, int size, boolean sort);
    List<DbInfoResponse> getAllDBs();
    Page<DbInfoResponse> getAllDBsByAuthorId(Long authorId, int page, int size, boolean sort);
    Database getDB(Long db_id) throws IOException;
    DbInfoResponse getDBInfoById(Long db_id);
    void deleteDB(Long db_id);
}
