package ru.vsu.csf.sqlportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.sqlportal.dto.request.QuestionRequest;
import ru.vsu.csf.sqlportal.dto.request.TestRequest;
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

    @GetMapping("course/{course_id}/tests")
    public List<TestResponse> getAllTestForCourse(@PathVariable("course_id") Long course_id) {
        return testService.getAllTestForCourse(course_id);
    }

    @GetMapping("/tests/{test_id}")
    public TestResponse getTest(@PathVariable("test_id") Long test_id) {
        return testService.getTestById(test_id);
    }

    @PostMapping("course/{course_id}/tests")
    public ResponseEntity<?> createTest(@PathVariable("course_id") Long course_id, @Valid @RequestBody TestRequest testRequest) {
        TestResponse testResponse = testService.createTest(course_id, testRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(testResponse);
        //TODO add exhausted db id here
    }

    @DeleteMapping("/tests/{test_id}")
    public void deleteTest(@PathVariable("test_id") Long test_id) {
        testService.deleteTestById(test_id);
    }

    @PostMapping("/tests/{test_id}/questions")
    public void addQuestion(@PathVariable("test_id") Long test_id, @Valid @RequestBody QuestionRequest questionRequest) {
        testService.addQuestionForTest(test_id, questionRequest);
    }

    @PutMapping("/tests/{test_id}/questions/{question_id}")
    public void updateQuestion(@PathVariable("test_id") Long test_id,
                        @PathVariable("question_id") Long question_id) {

    }

    @DeleteMapping("/questions/{question_id}")
    public void deleteQuestion(@PathVariable("question_id") Long question_id) {
        testService.deleteQuestionById(question_id);
    }

}
