package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.dto.request.QuestionRequest;
import ru.vsu.csf.sqlportal.dto.request.TestRequest;
import ru.vsu.csf.sqlportal.dto.response.ExhaustedDBResponse;
import ru.vsu.csf.sqlportal.dto.response.QuestionResponse;
import ru.vsu.csf.sqlportal.dto.response.TestResponse;
import ru.vsu.csf.sqlportal.exception.AbuseRightsException;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.*;
import ru.vsu.csf.sqlportal.repository.*;
import ru.vsu.csf.sqlportal.service.TestService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private ExhaustedDbRepository exhaustedDbRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public List<TestResponse> getAllTestForCourse(Long course_id) {
        List<Test> tests = testRepository.findAllByCourse_Id(course_id);
        return tests.stream()
                .map(t -> {
                    ExhaustedDb exhaustedDb = t.getExhaustedDB();
                    ExhaustedDBResponse exhaustedDBResponse = new ExhaustedDBResponse(
                            exhaustedDb.getId(),
                            exhaustedDb.getName(),
                            exhaustedDb.getPath()
                    );
                    TestResponse testResponse = new TestResponse(
                            t.getId(),
                            t.getName(),
                            t.getMaxAttemptsCnt(),
                            exhaustedDBResponse,
                            null,
                            null
                    );
                    return testResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TestResponse getTestById(Long test_id) {
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        ExhaustedDb exhaustedDb = test.getExhaustedDB();
        ExhaustedDBResponse exhaustedDBResponse = new ExhaustedDBResponse(
                exhaustedDb.getId(),
                exhaustedDb.getName(),
                exhaustedDb.getPath()
        );
        List<QuestionResponse> questionList = test.getQuestions().stream().map(
                question -> {
                    QuestionResponse questionResponse = new QuestionResponse(
                            question.getId(),
                            question.getText(),
                            null
                    );
                    return questionResponse;
                })
                .collect(Collectors.toList());
        TestResponse testResponse = new TestResponse(
                test.getId(),
                test.getName(),
                test.getMaxAttemptsCnt(),
                exhaustedDBResponse,
                null,
                questionList
        );
        return testResponse;
    }

    @Override
    public TestResponse createTest(Long course_id, TestRequest testRequest) {
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        Course course = courseRepository.findById(course_id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", course_id)
        );
        if (!course.getAuthor().getId().equals(author.getId())) {
            throw new AbuseRightsException(String.format("User '%s' is not author for course '%s'", author.getLogin(), course.getId()));
        }
        ExhaustedDb exhaustedDb = exhaustedDbRepository.findById(testRequest.getExhaustedDB_id()).orElseThrow(
                () -> new ResourceNotFoundException("ExhaustedDB", "id", testRequest.getExhaustedDB_id())
        );
        Test test = new Test(testRequest.getName(),
                testRequest.getMaxAttemptsCnt(),
                exhaustedDb,
                course,
                null);
        test = testRepository.save(test);
        return new TestResponse(
                test.getId(),
                test.getName(),
                test.getMaxAttemptsCnt(),
                null,
                null,
                null
        );
    }

    @Override
    public void deleteTestById(Long test_id) {
        testRepository.deleteById(test_id);
    }

    @Override
    public void addQuestionForTest(Long test_id, QuestionRequest questionRequest) {
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        Course course = courseRepository.findById(test.getCourse().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", test.getCourse().getId())
        );
        if (!course.getAuthor().getId().equals(author.getId())) {
            throw new AbuseRightsException(String.format("User '%s' is not author for course '%s'", author.getLogin(), course.getId()));
        }
        Question question = new Question(
                questionRequest.getText(),
                questionRequest.getRightAnswer(),
                test
        );
        questionRepository.save(question);
    }

    @Override
    public void deleteQuestionById(Long question_id) {
        Question question = questionRepository.findById(question_id).orElseThrow(
                () -> new ResourceNotFoundException("Question", "id", question_id)
        );
        Test test = testRepository.findById(question.getTest().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", question.getTest().getId())
        );
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        Course course = courseRepository.findById(test.getCourse().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", test.getCourse().getId())
        );
        if (!course.getAuthor().getId().equals(author.getId())) {
            throw new AbuseRightsException(String.format("User '%s' is not author for course '%s'", author.getLogin(), course.getId()));
        }
        questionRepository.deleteById(question_id);
    }
}
