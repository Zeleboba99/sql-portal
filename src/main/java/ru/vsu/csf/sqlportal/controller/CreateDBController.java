package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.csf.sqlportal.dto.request.CreateDBRequest;
import ru.vsu.csf.sqlportal.dto.response.ExhaustedDBResponse;
import ru.vsu.csf.sqlportal.model.Database;
import ru.vsu.csf.sqlportal.service.ExcelHelperService;
import ru.vsu.csf.sqlportal.service.ExhaustedDbService;

import javax.validation.Valid;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class CreateDBController {

    private final static String FILENAME = "Книга1.xlsx";
    @Autowired
    private ExcelHelperService excelHelperService;

    @Autowired
    private ExhaustedDbService exhaustedDbService;

    @GetMapping("/DB/{id}")
    public ResponseEntity<Database> getDB(@PathVariable("id") Long id) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(excelHelperService.getDB(id));
    }

    @Deprecated
    @GetMapping("/downloadDB/{id}")
    public ResponseEntity<?> downloadDB(@PathVariable("id") Long id) {
        String message = "";
        try {
            InputStreamResource file = excelHelperService.getExcelDB(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FILENAME)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(file);
        } catch (Exception e){
            message = "Could not download the file";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    @PostMapping("/uploadDB")
    public ResponseEntity<?> uploadDB(@RequestParam("file") MultipartFile multipartFile,
                                           @RequestBody @Valid CreateDBRequest createDbRequest){
        String message = "";
        try {
            ExhaustedDBResponse response = excelHelperService.save(createDbRequest, multipartFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (Exception e){
            message = "Could not upload the file: " + multipartFile.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }
}
