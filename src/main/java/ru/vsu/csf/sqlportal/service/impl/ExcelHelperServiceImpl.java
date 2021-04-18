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
import ru.vsu.csf.sqlportal.dto.response.DbInfoResponse;
import ru.vsu.csf.sqlportal.exception.AbuseRightsException;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Database;
import ru.vsu.csf.sqlportal.model.DbInfo;
import ru.vsu.csf.sqlportal.model.Table;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.DbInfoRepository;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.service.ExcelHelperService;
import ru.vsu.csf.sqlportal.service.ConverterService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static ru.vsu.csf.sqlportal.service.ConverterService.convertToDbInfoResponse;

@Service
public class ExcelHelperServiceImpl implements ExcelHelperService {

    public static final String RELATIVE_EXCEL_RESOURCES_PATH = "src\\main\\resources\\excel\\";
    public static final String XLSX_EXTENTION = ".xlsx";

    @Autowired
    private DbInfoRepository dbInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public DbInfoResponse save(String dbName, MultipartFile schemaImage, MultipartFile file) throws IOException {

        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        byte[] byteSchemaImage = Objects.nonNull(schemaImage) ? schemaImage.getBytes() : new byte[0];
        DbInfo dbInfo = new DbInfo(
                dbName,
                byteSchemaImage,
                null,
                author);
        DbInfo savedDb = dbInfoRepository.save(dbInfo);

        String currentLocation = getCurrentLocation();
        StringBuilder absoluteFilePath = new StringBuilder(currentLocation);
        absoluteFilePath.append(RELATIVE_EXCEL_RESOURCES_PATH)
                .append(dbInfo.getId().toString())
                .append(XLSX_EXTENTION);

        File newFile = new File(absoluteFilePath.toString());
        file.transferTo(newFile);

        savedDb.setPath(RELATIVE_EXCEL_RESOURCES_PATH + dbInfo.getId().toString() + XLSX_EXTENTION);
        savedDb = dbInfoRepository.save(savedDb);
        return convertToDbInfoResponse(savedDb);
    }

    @Override
    public Page<DbInfoResponse> getAllDBs(int page, int size, boolean sort) {
        Sort sortOrder = sort ? Sort.by("name").ascending() : Sort.by("name").descending();
        Page<DbInfo> dbsInfo = dbInfoRepository.findAll(PageRequest.of(page, size, sortOrder));
        long totalElements = dbsInfo.getTotalElements();
        List<DbInfoResponse> dbInfoResponses = dbsInfo.stream()
                .map(ConverterService::convertToDbInfoResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(dbInfoResponses, PageRequest.of(page, size), totalElements);
    }

    @Override
    public List<DbInfoResponse> getAllDBs() {
        return dbInfoRepository.findAll().stream()
                .map(ConverterService::convertToDbInfoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DbInfoResponse> getAllDBsByAuthorId(Long authorId, int page, int size, boolean sort) {
        Sort sortOrder = sort ? Sort.by("name").ascending() : Sort.by("name").descending();
        Page<DbInfo> dbsInfo = dbInfoRepository.findAllByAuthorId(authorId, PageRequest.of(page, size, sortOrder));
        long totalElements = dbsInfo.getTotalElements();
        List<DbInfoResponse> dbInfoResponses = dbsInfo.stream()
                .map(ConverterService::convertToDbInfoResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(dbInfoResponses, PageRequest.of(page, size), totalElements);
    }

    @Override
    public Database getDB(Long db_id) throws IOException {
        DbInfo dbInfoRecord = dbInfoRepository.findById(db_id).orElseThrow(
                () -> new ResourceNotFoundException("DbInfo", "id", db_id)
        );
        String absoluteFilePath = getCurrentLocation() + dbInfoRecord.getPath();
        FileInputStream file = new FileInputStream(new File(absoluteFilePath));

        Database db = new Database(dbInfoRecord.getName());
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
                    if (j == 0) { // table name
                        table.setName(temp);
                    } else if (j == 1) { //columns
                        String[] field = temp.split("\\|");
                        table.getFields().add(Arrays.asList(field[0], field[1]));
                    } else { //row values
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
    public DbInfoResponse getDBInfoById(Long db_id) {
        DbInfo dbInfo = dbInfoRepository.findById(db_id).orElseThrow(
                () -> new ResourceNotFoundException("DbInfo", "id", db_id)
        );
        return convertToDbInfoResponse(dbInfo);
    }

    @Override
    public void deleteDB(Long db_id) {
        User user = getCurrentUser();
        DbInfo dbInfo = dbInfoRepository.findById(db_id).orElseThrow(
                () -> new ResourceNotFoundException("DbInfo", "id", db_id)
        );
        if (!user.getId().equals(dbInfo.getAuthor().getId())) {
            throw new AbuseRightsException(user.getLogin());
        }
        File file = new File(dbInfo.getPath());
        if (file.delete()) {
            dbInfoRepository.delete(dbInfo);
        } else {
            //todo throw some exception (mb just runtime ex)
        }
    }

    private User getCurrentUser() {
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
    }

    private String getCurrentLocation() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        StringBuilder fileLocation = new StringBuilder(path.substring(0, path.length() - 1));
        return fileLocation.toString();
    }
}
