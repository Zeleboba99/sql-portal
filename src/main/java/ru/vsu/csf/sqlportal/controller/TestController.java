package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.sqlportal.dto.request.AnswerRequest;
import ru.vsu.csf.sqlportal.dto.request.QuestionRequest;
import ru.vsu.csf.sqlportal.dto.request.TestRequest;
import ru.vsu.csf.sqlportal.dto.response.AttemptResponse;
import ru.vsu.csf.sqlportal.dto.response.TestResponse;
import ru.vsu.csf.sqlportal.service.TestService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private TestService testService;

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("courses/{course_id}/tests")
    public List<TestResponse> getAllTestForCourse(@PathVariable("course_id") Long course_id) {
        return testService.getAllTestForCourse(course_id);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/tests/{test_id}")
    public TestResponse getTest(@PathVariable("test_id") Long test_id) {
        return testService.getTestById(test_id);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("courses/{course_id}/tests")
    public TestResponse createTest(@PathVariable("course_id") Long course_id, @Valid @RequestBody TestRequest testRequest) {
        return testService.createTest(course_id, testRequest);

        //TODO add exhausted db id here
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/tests/{test_id}")
    public void deleteTest(@PathVariable("test_id") Long test_id) {
        testService.deleteTestById(test_id);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PostMapping("/tests/{test_id}/questions")
    public void addQuestions(@PathVariable("test_id") Long test_id, @Valid @RequestBody List<QuestionRequest> questionRequests) {
        testService.addQuestionsForTest(test_id, questionRequests);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/tests/{test_id}/questions")
    public void updateQuestions(@PathVariable("test_id") Long test_id, @Valid @RequestBody List<QuestionRequest> questionRequests) {
        testService.updateQuestions(test_id, questionRequests);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/questions/{question_id}")
    public void deleteQuestion(@PathVariable("question_id") Long question_id) {
        testService.deleteQuestionById(question_id);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/tests/{test_id}/pass")
    public void passTest(@PathVariable("test_id") Long test_id, @Valid @RequestBody List<QuestionRequest> questionRequests) {
        testService.passTest(test_id, questionRequests);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/tests/{test_id}/attempts")
    public List<AttemptResponse> getAttemptsForTest(@PathVariable("test_id") Long test_id) {
        return testService.getAttemptsForTest(test_id);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT', 'TEACHER')")
    @GetMapping("/tests/{test_id}/attempts/{attempt_id}")
    public AttemptResponse getAttemptById(@PathVariable("test_id") Long test_id, @PathVariable("attempt_id") Long attempt_id) {
        return testService.getAttemptById(attempt_id);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PutMapping("/tests/{test_id}")
    public TestResponse updateTest(@PathVariable("test_id") Long test_id, @Valid @RequestBody TestRequest testRequest) {
        return testService.updateTest(test_id, testRequest);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @GetMapping("/tests/{test_id}/users/{user_id}/attempts")
    public List<AttemptResponse> getUsersAttemptsForTest(@PathVariable("test_id") Long test_id, @PathVariable("user_id") Long user_id) {
        return testService.getAttemptsForTest(user_id, test_id);
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @PatchMapping("/attempts/{attempt_id}")
    public void estimateAttempt(@PathVariable("attempt_id") Long attempt_id, @Valid @RequestBody List<AnswerRequest> answerRequests) {
        testService.estimateAttempt(attempt_id, answerRequests);
    }
}
