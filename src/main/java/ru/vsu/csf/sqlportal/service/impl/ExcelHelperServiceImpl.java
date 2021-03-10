package ru.vsu.csf.sqlportal.service.impl;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.vsu.csf.sqlportal.dto.response.DbLocationResponse;
import ru.vsu.csf.sqlportal.exception.AbuseRightsException;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Database;
import ru.vsu.csf.sqlportal.model.DbLocation;
import ru.vsu.csf.sqlportal.model.Table;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.DbLocationRepository;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.service.ExcelHelperService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelHelperServiceImpl implements ExcelHelperService {

    @Autowired
    private DbLocationRepository dbLocationRepository;

    @Autowired
    private UserRepository userRepository;

    private String fileLocation;

    @Transactional
    @Override
    public DbLocationResponse save(String dbName, MultipartFile file) throws IOException {

        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        DbLocation dbLocation = new DbLocation(
                dbName,
                null,
                author);
        DbLocation savedDb = dbLocationRepository.save(dbLocation);

//        InputStream in = file.getInputStream();

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        fileLocation = path.substring(0, path.length() - 1).concat("src\\main\\resources\\excel\\")
                .concat(dbLocation.getId().toString())
                .concat(".xlsx");

        File newFile = new File(fileLocation);
        file.transferTo(newFile);
//        fileLocation = fileLocation.concat(Objects.requireNonNull(file.getOriginalFilename()));
//        FileOutputStream f = new FileOutputStream(fileLocation);
//        int ch = 0;
//        while ((ch = in.read()) != -1) {
//            f.write(ch);
//        }
//        f.flush();
//        f.close();



//        fileLocation = fileLocation.concat(savedDb.getId().toString()).concat(".xlsx");
        savedDb.setPath(fileLocation);
        savedDb = dbLocationRepository.save(savedDb);
        return convertToDbLocationResponse(savedDb);
    }

    @Override
    public Page<DbLocationResponse> getAllDBs(int page, int size, boolean sort) {
        Sort sortOrder = sort ? Sort.by("name").ascending() : Sort.by("name").descending();
        Page<DbLocation> dbLocations = dbLocationRepository.findAll(PageRequest.of(page, size, sortOrder));
        long totalElements = dbLocations.getTotalElements();
        List<DbLocationResponse> dbLocationRespons = dbLocations.stream()
                .map(this::convertToDbLocationResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(dbLocationRespons, PageRequest.of(page, size), totalElements);
    }

    @Override
    public List<DbLocationResponse> getAllDBs() {
        return dbLocationRepository.findAll().stream()
                .map(this::convertToDbLocationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DbLocationResponse> getAllDBsByAuthorId(Long authorId, int page, int size, boolean sort) {
        Sort sortOrder = sort ? Sort.by("name").ascending() : Sort.by("name").descending();
        Page<DbLocation> dbLocations = dbLocationRepository.findAllByAuthorId(authorId, PageRequest.of(page, size, sortOrder));
        long totalElements = dbLocations.getTotalElements();
        List<DbLocationResponse> dbLocationRespons = dbLocations.stream()
                .map(this::convertToDbLocationResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(dbLocationRespons, PageRequest.of(page, size), totalElements);
    }

    @Deprecated
    @Override
    public InputStreamResource getExcelDB(Long id) throws IOException {
        byte[] result = Files.readAllBytes(Paths.get("C:\\Users\\kate2\\Desktop\\диплом\\sql-portal\\src\\main\\resources\\excel\\Книга1.xlsx"));
        InputStream is = new ByteArrayInputStream(result);
        return new InputStreamResource(is);
    }

    @Override
    public Database getDB(Long db_id) throws IOException {
        DbLocation dbLocationRecord = dbLocationRepository.findById(db_id).orElseThrow(
                () -> new ResourceNotFoundException("DbLocation", "id", db_id)
        );

        FileInputStream file = new FileInputStream(new File(dbLocationRecord.getPath()));
//        FileInputStream file = new FileInputStream(new File("C:\\Users\\kate2\\Desktop\\диплом\\sql-portal\\src\\main\\resources\\excel\\Книга1.xlsx"));

//        Database db = new Database("DB1");

        Database db = new Database(dbLocationRecord.getName());
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
                        table.getFields().add(Arrays.asList(field[0], field[1]));
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

    @Override
    public void deleteDB(Long db_id) {
        User user = getCurrentUser();
        DbLocation dbLocation = dbLocationRepository.findById(db_id).orElseThrow(
                () -> new ResourceNotFoundException("DbLocation", "id", db_id)
        );
        if (!user.getId().equals(dbLocation.getAuthor().getId())) {
            throw new AbuseRightsException(user.getLogin());
        }
        File file = new File(dbLocation.getPath());
        if (file.delete()) {
            dbLocationRepository.delete(dbLocation);
        } else {
            //todo throw some exception (mb just runtime ex)
        }
    }

    private DbLocationResponse convertToDbLocationResponse(DbLocation dbLocation) {
        return new DbLocationResponse(dbLocation.getId(),
                dbLocation.getName(),
                dbLocation.getAuthor().getId(),
                dbLocation.getAuthor().getFirstName() + " " + dbLocation.getAuthor().getLastName());
    }

    private User getCurrentUser() {
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
    }
}
