package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.csf.sqlportal.dto.response.DbInfoResponse;
import ru.vsu.csf.sqlportal.model.Database;
import ru.vsu.csf.sqlportal.service.ExcelHelperService;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/dbs")
public class DbController {
    @Autowired
    private ExcelHelperService excelHelperService;

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<Database> getDB(@PathVariable("id") Long id) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(excelHelperService.getDB(id));
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/db-info/{id}")
    public ResponseEntity<DbInfoResponse> getDBInfo(@PathVariable("id") Long id) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(excelHelperService.getDBInfoById(id));
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/pageable")
    public Page<DbInfoResponse> getDBsPage(@RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam(value = "sort", defaultValue = "true") boolean sort) throws IOException {
        return excelHelperService.getAllDBs(page, size, sort);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping
    public List<DbInfoResponse> getAllDBs() throws IOException {
        return excelHelperService.getAllDBs();
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/author/{author_id}")
    public Page<DbInfoResponse> getDBsByAuthorId(@PathVariable("author_id") Long author_id,
                                                 @RequestParam("page") int page,
                                                 @RequestParam("size") int size,
                                                 @RequestParam(value = "sort", defaultValue = "true") boolean sort) throws IOException {
        return excelHelperService.getAllDBsByAuthorId(author_id, page, size, sort);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/uploadDB/{dbName}")
    public ResponseEntity<?> uploadDB(@RequestParam("file") MultipartFile multipartFile,
                                      @RequestParam(value = "schemaImage", required = false) MultipartFile schemaImage,
                                           @PathVariable("dbName") String dbName){
        String message = "";
        try {
            DbInfoResponse response = excelHelperService.save(dbName, schemaImage, multipartFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (Exception e){
            message = "Could not upload the file: " + multipartFile.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/{db_id}")
    public void deleteDb(@PathVariable("db_id") Long db_id) {
        excelHelperService.deleteDB(db_id);
    }

}
