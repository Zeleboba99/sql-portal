package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.sqlportal.dto.request.AnswerRequest;
import ru.vsu.csf.sqlportal.dto.request.QuestionRequest;
import ru.vsu.csf.sqlportal.dto.request.TestRequest;
import ru.vsu.csf.sqlportal.dto.response.*;
import ru.vsu.csf.sqlportal.exception.AbuseRightsException;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.*;
import ru.vsu.csf.sqlportal.repository.*;
import ru.vsu.csf.sqlportal.service.TestService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.vsu.csf.sqlportal.service.ConverterService.*;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private DbInfoRepository dbInfoRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AttemptRepository attemptRepository;

    @Override
    public List<TestResponse> getAllTestForCourse(Long course_id) {
        List<Test> tests = testRepository.findByCourse_IdOrderByNumber(course_id);
        return tests.stream()
                .map(t -> {
                    DbInfo dbInfo = t.getDbInfo();
                    DbInfoResponse dbInfoResponse = convertToDbInfoResponse(dbInfo);
                    TestResponse testResponse = convertToTestResponse(t, dbInfoResponse, null, null);
                    return testResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TestResponse getTestById(Long test_id) {
        User currentUser = getCurrentUser();
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        DbInfo dbInfo = test.getDbInfo();
        DbInfoResponse dbInfoResponse = convertToDbInfoResponse(dbInfo);
        List<QuestionResponse> questionList = test.getQuestions().stream().map(
                question -> {
                    AnswerResponse answerResponse = new AnswerResponse(null, "", null);
                    if (isCurrentUserCourseAuthor(test.getCourse().getId())) {
                        //TODO return all answers instead of first
                        Optional<Answer> answerOptional = question.getAnswers().stream()
                                .filter(answer -> answer.getAuthor().getId().equals(currentUser.getId()))
                                .findFirst();
                        if (answerOptional.isPresent()) {
                            Answer answer = answerOptional.get();
                            answerResponse = new AnswerResponse(answer.getId(), answer.getText(), answer.getGrade());
                        }
                    }

                    QuestionResponse questionResponse = convertToQuestionResponse(question, answerResponse, null);
                    return questionResponse;
                })
                .collect(Collectors.toList());
        TestResponse testResponse = convertToTestResponse(test, dbInfoResponse, null, questionList);
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
        DbInfo dbInfo = dbInfoRepository.findById(testRequest.getDbInfoRequest().getId()).orElseThrow(
                () -> new ResourceNotFoundException("DbInfo", "id", testRequest.getDbInfoRequest().getId())
        );
        Test test;
        if (testRequest.getPreviousTestId() != 0) {
            Test previousTest = testRepository.findById(testRequest.getPreviousTestId()).orElseThrow(
                    () -> new ResourceNotFoundException("Test", "id", testRequest.getPreviousTestId())
            );
            if (!previousTest.getCourse().getId().equals(course_id)) {
                throw new AbuseRightsException(String.format("Test with id '%s' is not related for course '%s'", previousTest.getId(), course_id));
            }

            List<Test> testsForUpdate = testRepository.findByCourse_IdOrderByNumber(course_id).stream()
                    .filter(t -> t.getNumber() > previousTest.getNumber())
                    .collect(Collectors.toList());
            increaseTestNumbers(testsForUpdate);

            test = new Test(testRequest.getName(),
                    previousTest.getNumber() + 1,
                    testRequest.getMaxAttemptsCnt(),
                    dbInfo,
                    course,
                    null);
        } else {
            List<Test> testsForUpdate = testRepository.findByCourse_IdOrderByNumber(course_id);
            increaseTestNumbers(testsForUpdate);
            test = new Test(testRequest.getName(),
                    1,
                    testRequest.getMaxAttemptsCnt(),
                    dbInfo,
                    course,
                    null);
        }

        test = testRepository.save(test);
        return convertToTestResponse(test, null, null, null);
    }

    @Override
    public void deleteTestById(Long test_id) {
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        List<Test> testsForUpdate = testRepository.findByCourse_IdOrderByNumber(test.getCourse().getId()).stream()
                .filter(t -> t.getNumber() > test.getNumber())
                .collect(Collectors.toList());
        decreaseTestNumbers(testsForUpdate);
        testRepository.deleteById(test_id);
    }

    @Override
    public void addQuestionsForTest(Long test_id, List<QuestionRequest> questionRequests) {
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(userLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", userLogin)
        );
        Course course = courseRepository.findById(test.getCourse().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", test.getCourse().getId())
        );
        if (!isCurrentUserCourseAuthor(course.getId())) {
            throw new AbuseRightsException(String.format("User '%s' is not author for course '%s'", user.getLogin(), course.getId()));
        }

        for (QuestionRequest q : questionRequests) {
            Question question = new Question(
                    q.getText(),
                    test
            );
            Question savedQuestion = questionRepository.save(question);
            //TODO save all answers
            Answer answer = new Answer(q.getAnswer().getText(), null, user, savedQuestion, null);
            answerRepository.save(answer);
        }
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

    @Override
    @Transactional
    public void passTest(Long test_id, List<QuestionRequest> questionRequests) {
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        User user = getCurrentUser();
        int previousAttemptsCnt = getAttemptsForTest(test_id).size();

        if (test.getMaxAttemptsCnt() <= previousAttemptsCnt) {
            throw new AbuseRightsException(String.format("No available attempts for user '%s'", user.getLogin()));
        }

        Attempt attempt = new Attempt(new Date(), 0, test, user, null);
        Attempt finalAttempt = attemptRepository.save(attempt);
        questionRequests.forEach(q -> {
            Question question = questionRepository.findById(q.getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Question", "id", q.getId())
            );
            //TODO save all answers
            Answer answer = new Answer(q.getAnswer().getText(), null, user, question, finalAttempt);
            answerRepository.save(answer);
        });
    }

    @Override
    public List<AttemptResponse> getAttemptsForTest(Long test_id) {
        User user = getCurrentUser();
        return getAttemptsForTest(user.getId(), test_id).stream()
                .sorted(Comparator.comparing(AttemptResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<AttemptResponse> getAttemptsForTest(Long user_id, Long test_id) {
        List<Attempt> attempts = attemptRepository.getAllByTestIdAndAuthorId(test_id, user_id).stream()
                .sorted(Comparator.comparing(Attempt::getCreatedAt).reversed())
                .collect(Collectors.toList());
        return attempts.stream().map(attempt -> {
            List<QuestionResponse> questionResponses = attempt.getAnswers().stream().map(answer -> {
                AnswerResponse answerResponse = new AnswerResponse(answer.getId(), answer.getText(), answer.getGrade());
                Question question = answer.getQuestion();
                return convertToQuestionResponse(question, answerResponse, null);
            }).collect(Collectors.toList());
            UserResponse userResponse = convertToUserResponse(attempt.getAuthor());
            return convertToAttemptResponse(attempt, null, userResponse ,questionResponses);
        }).collect(Collectors.toList());
    }

    @Override
    public AttemptResponse getAttemptById(Long attempt_id) {
        User user = getCurrentUser();
        //TODO check that user is author or teacher or else throw abuse of rights
        Attempt attempt = attemptRepository.findById(attempt_id).orElseThrow(
                () -> new ResourceNotFoundException("Attempt", "id", attempt_id)
        );
        List<QuestionResponse> questionResponses = attempt.getAnswers().stream().map(answer -> {
            AnswerResponse answerResponse = new AnswerResponse(answer.getId(), answer.getText(), answer.getGrade());
            Question question = answer.getQuestion();
            return convertToQuestionResponse(question, answerResponse, null);
        }).collect(Collectors.toList());
        UserResponse userResponse = convertToUserResponse(attempt.getAuthor());
        return convertToAttemptResponse(attempt, null, userResponse, questionResponses);
    }

    @Override
    public TestResponse updateTest(Long test_id, TestRequest testRequest) {
        User user = getCurrentUser();
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        DbInfo dbInfo = dbInfoRepository.findById(testRequest.getDbInfoRequest().getId()).orElseThrow(
                () -> new ResourceNotFoundException("DbInfo", "id", testRequest.getDbInfoRequest().getId())
        );
        if (!isCurrentUserCourseAuthor(test.getCourse().getId())) {
            throw new AbuseRightsException(String.format("User '%s' is not author for course '%s'", user.getLogin(), test.getCourse().getId()));
        }
        test.setName(testRequest.getName());
        test.setMaxAttemptsCnt(testRequest.getMaxAttemptsCnt());
        test.setDbInfo(dbInfo);
        Test updatedTest = testRepository.save(test);
        return convertToTestResponse(updatedTest, convertToDbInfoResponse(updatedTest.getDbInfo()), null, null);
    }

    @Override
    public void updateQuestions(Long test_id, List<QuestionRequest> questionRequests) {
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        User user = getCurrentUser();
        if (!isCurrentUserCourseAuthor(test.getCourse().getId())) {
            throw new AbuseRightsException(String.format("User '%s' is not author for course '%s'", user.getLogin(), test.getCourse().getId()));
        }

        //очистили удаленные вопросы
        List<Long> questionRequestIds = questionRequests.stream()
                .filter(q -> Objects.nonNull(q.getId()))
                .map(QuestionRequest::getId).collect(Collectors.toList());
        test.getQuestions().stream()
                .map(Question::getId)
                .forEach(questionId -> {
                    if (!questionRequestIds.contains(questionId)) {
                        questionRepository.deleteById(questionId);
                    }
                });
        questionRequests.forEach(questionRequest -> {
            if (questionRequest.getId() != null) {
                Question question = questionRepository.findById(questionRequest.getId()).orElseThrow(
                        () -> new ResourceNotFoundException("Question", "id", questionRequest.getId())
                );
                question.setText(questionRequest.getText());
                Question updatedQuestion = questionRepository.save(question);
                List<Answer> authorAnswers = updatedQuestion.getAnswers().stream()
                        .filter(answer -> answer.getAuthor().getId().equals(user.getId())).collect(Collectors.toList());
                answerRepository.deleteAll(authorAnswers);
                Answer answer = new Answer(questionRequest.getAnswer().getText(), null, user, updatedQuestion, null);
                answerRepository.save(answer);
            } else {
                Question question = new Question(questionRequest.getText(), test);
                Question savedQuestion = questionRepository.save(question);
                Answer answer = new Answer(questionRequest.getAnswer().getText(), null, user, savedQuestion, null);
                answerRepository.save(answer);
            }
        });
    }

    @Override
    public void estimateAttempt(Long attempt_id, List<AnswerRequest> answerRequests) {
        answerRequests
                .forEach(answerRequest -> {
                    Answer answer = answerRepository.findById(answerRequest.getId()).orElseThrow(
                            () -> new ResourceNotFoundException("Answer", "id", answerRequest.getId())
                    );
                    if (!answer.getAttempt().getId().equals(attempt_id)) {
                        throw new RuntimeException("Answers assign to different attempts");
                    }
                    answer.setGrade(answerRequest.getGrade());
                    answerRepository.save(answer);
                });
    }

    private boolean isCurrentUserCourseAuthor(Long course_id) {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(userLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", userLogin)
        );

        Course course = courseRepository.findById(course_id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", course_id)
        );
        return course.getAuthor().getId().equals(user.getId());
    }

    private User getCurrentUser() {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(userLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", userLogin)
        );
    }

    private void increaseTestNumbers(List<Test> tests) {
        tests.forEach(test -> test.setNumber(test.getNumber() + 1));
    }

    private void decreaseTestNumbers(List<Test> tests) {
        tests.forEach(test -> test.setNumber(test.getNumber() - 1));
    }
}
