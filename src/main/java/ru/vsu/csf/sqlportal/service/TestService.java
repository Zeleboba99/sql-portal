package ru.vsu.csf.sqlportal.service;

import ru.vsu.csf.sqlportal.dto.request.QuestionRequest;
import ru.vsu.csf.sqlportal.dto.request.TestRequest;
import ru.vsu.csf.sqlportal.dto.response.QuestionResponse;
import ru.vsu.csf.sqlportal.dto.response.TestResponse;

import java.util.List;

public interface TestService {
    List<TestResponse> getAllTestForCourse(Long course_id);
    TestResponse getTestById(Long test_id);
    TestResponse createTest(Long course_id, TestRequest testRequest);
    void deleteTestById(Long test_id);
    void addQuestionForTest(Long test_id, QuestionRequest questionRequest);
    void deleteQuestionById(Long question_id);
}
