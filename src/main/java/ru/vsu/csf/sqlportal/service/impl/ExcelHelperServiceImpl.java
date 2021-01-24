package ru.vsu.csf.sqlportal.service.impl;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.csf.sqlportal.dto.request.CreateDBRequest;
import ru.vsu.csf.sqlportal.dto.response.ExhaustedDBResponse;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Database;
import ru.vsu.csf.sqlportal.model.ExhaustedDb;
import ru.vsu.csf.sqlportal.model.Table;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.ExhaustedDbRepository;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.service.ExcelHelperService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ExcelHelperServiceImpl implements ExcelHelperService {

    @Autowired
    private ExhaustedDbRepository exhaustedDbRepository;

    @Autowired
    private UserRepository userRepository;

    private String fileLocation;

    @Override
    public ExhaustedDBResponse save(CreateDBRequest createDbRequest, MultipartFile file) throws IOException {

        InputStream in = file.getInputStream();
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        fileLocation = path.substring(0, path.length() - 1).concat("src\\main\\resources\\excel\\");
        fileLocation = fileLocation.concat(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream f = new FileOutputStream(fileLocation);
        int ch = 0;
        while ((ch = in.read()) != -1) {
            f.write(ch);
        }
        f.flush();
        f.close();

        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        ExhaustedDb exhaustedDb = new ExhaustedDb(
                createDbRequest.getName(),
                fileLocation,
                author);
        ExhaustedDb savedDb = exhaustedDbRepository.save(exhaustedDb);
        return new ExhaustedDBResponse(exhaustedDb.getId(),
                exhaustedDb.getName(),
                exhaustedDb.getPath());
    }

    @Override
    public InputStreamResource getExcelDB(Long id) throws IOException {
        byte[] result = Files.readAllBytes(Paths.get("C:\\Users\\kate2\\Desktop\\диплом\\sql-portal\\src\\main\\resources\\excel\\Книга1.xlsx"));
        InputStream is = new ByteArrayInputStream(result);
        return new InputStreamResource(is);
    }

    @Override
    public Database getDB(Long db_id) throws IOException{
        ExhaustedDb exhaustedDbRecord = exhaustedDbRepository.findById(db_id).orElseThrow(
                () -> new ResourceNotFoundException("ExhaustedDB", "id", db_id)
        );

        FileInputStream file = new FileInputStream(new File(exhaustedDbRecord.getPath()));
//        FileInputStream file = new FileInputStream(new File("C:\\Users\\kate2\\Desktop\\диплом\\sql-portal\\src\\main\\resources\\excel\\Книга1.xlsx"));

//        Database db = new Database("DB1");

        Database db = new Database(exhaustedDbRecord.getName());
        Workbook workbook = new XSSFWorkbook(file);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

            Table table = new Table();
            Sheet sheet = workbook.getSheetAt(i);

            Map<Integer, List<String>> data = new HashMap<>();
            int j = 0;
            for (Row row : sheet) {
                List<String> insertData = new ArrayList<>();
                for (Cell cell : row) {
                    String temp = "";
                    switch (cell.getCellTypeEnum()) {
                        case STRING: {
                            temp = cell.getRichStringCellValue().getString();
                        }
                        break;
                        case NUMERIC: {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                temp = cell.getDateCellValue() + "";
                            } else {
                                temp = cell.getNumericCellValue() + "";
                            }
                        }
                        break;
                        case BOOLEAN: {
                            temp = cell.getBooleanCellValue() + "";
                        }
                        break;
                        case FORMULA: {
                            temp = cell.getCellFormula() + "";
                        }
                        break;
                        default:
                            temp = " ";
                    }
                    if (j == 0) {
                        table.setName(temp);
                    } else if (j == 1) {
                        String[] field = temp.split("\\|");
                        table.getFields().put(field[0], field[1]);
                    } else {
                        insertData.add(temp);
                    }
                }
                if (!insertData.isEmpty()) {
                    table.getData().add(insertData);
                }
                j++;
            }
            db.getTables().add(table);
        }
        return db;
    }
}
