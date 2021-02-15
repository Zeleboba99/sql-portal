package ru.vsu.csf.sqlportal.service;

import ru.vsu.csf.sqlportal.dto.request.AnswerRequest;
import ru.vsu.csf.sqlportal.dto.request.QuestionRequest;
import ru.vsu.csf.sqlportal.dto.request.TestRequest;
import ru.vsu.csf.sqlportal.dto.response.AttemptResponse;
import ru.vsu.csf.sqlportal.dto.response.QuestionResponse;
import ru.vsu.csf.sqlportal.dto.response.TestResponse;

import java.util.List;

public interface TestService {
    List<TestResponse> getAllTestForCourse(Long course_id);
    TestResponse getTestById(Long test_id);
    TestResponse createTest(Long course_id, TestRequest testRequest);
    void deleteTestById(Long test_id);
    void addQuestionsForTest(Long test_id, List<QuestionRequest> questionRequest);
    void deleteQuestionById(Long question_id);
    void passTest(Long test_id, List<QuestionRequest> questionRequests);
    List<AttemptResponse> getAttemptsForTest(Long test_id);
    List<AttemptResponse> getAttemptsForTest(Long user_id, Long test_id);
    AttemptResponse getAttemptById(Long attempt_id);
    TestResponse updateTest(Long test_id, TestRequest testRequest);
    void updateQuestions(Long test_id, List<QuestionRequest> questionRequests);
    void estimateAttempt(Long attempt_id, List<AnswerRequest> answerRequests);
}
