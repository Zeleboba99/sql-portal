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
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AttemptRepository attemptRepository;

    @Override
    public List<TestResponse> getAllTestForCourse(Long course_id) {
        List<Test> tests = testRepository.findAllByCourse_Id(course_id);
        return tests.stream()
                .map(t -> {
                    ExhaustedDb exhaustedDb = t.getExhaustedDB();
                    ExhaustedDBResponse exhaustedDBResponse = convertToExhaustedDBResponse(exhaustedDb);
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

        User currentUser = getCurrentUser();
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        ExhaustedDb exhaustedDb = test.getExhaustedDB();
        ExhaustedDBResponse exhaustedDBResponse = convertToExhaustedDBResponse(exhaustedDb);
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

                    QuestionResponse questionResponse = new QuestionResponse(
                            question.getId(),
                            question.getText(),
                            answerResponse,
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
        ExhaustedDb exhaustedDb = exhaustedDbRepository.findById(testRequest.getExhaustedDb().getId()).orElseThrow(
                () -> new ResourceNotFoundException("ExhaustedDB", "id", testRequest.getExhaustedDb().getId())
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

        Attempt attempt = new Attempt(new Date(), test, user, null);
        Attempt finalAttempt = attemptRepository.save(attempt);
        List<Answer> answers = questionRequests.stream()
                .map(q -> {
                    Question question = questionRepository.findById(q.getId()).orElseThrow(
                            () -> new ResourceNotFoundException("Question", "id", q.getId())
                    );
                    //TODO save all answers
                    Answer answer = new Answer(q.getAnswer().getText(), null, user, question, finalAttempt);
                    return answerRepository.save(answer);
                }).collect(Collectors.toList());
    }

    @Override
    public List<AttemptResponse> getAttemptsForTest(Long test_id) {
        User user = getCurrentUser();
        return getAttemptsForTest(user.getId(), test_id);
    }

    @Override
    public List<AttemptResponse> getAttemptsForTest(Long user_id, Long test_id) {
        List<Attempt> attempts = attemptRepository.getAllByTestIdAndAuthorId(test_id, user_id);
        return attempts.stream().map(attempt -> {
            List<QuestionResponse> questionResponses = attempt.getAnswers().stream().map(answer -> {
                AnswerResponse answerResponse = new AnswerResponse(answer.getId(), answer.getText(), answer.getGrade());
                Question question = answer.getQuestion();
                return new QuestionResponse(
                        question.getId(),
                        question.getText(),
                        answerResponse,
                        null);
            }).collect(Collectors.toList());
            return new AttemptResponse(attempt.getId(),
                    attempt.getCreatedAt(),
                    null,
                    new UserResponse(attempt.getAuthor().getId(), attempt.getAuthor().getLogin(), attempt.getAuthor().getFirstName(), attempt.getAuthor().getLastName(), null),
                    questionResponses);
        }).collect(Collectors.toList());
    }

    @Override
    public AttemptResponse getAttemptById(Long attempt_id) {
        User user = getCurrentUser();
        Attempt attempt = attemptRepository.findById(attempt_id).orElseThrow(
                () -> new ResourceNotFoundException("Attempt", "id", attempt_id)
        );
        List<QuestionResponse> questionResponses = attempt.getAnswers().stream().map(answer -> {
            AnswerResponse answerResponse = new AnswerResponse(answer.getId(), answer.getText(), answer.getGrade());
            Question question = answer.getQuestion();
            return new QuestionResponse(
                    question.getId(),
                    question.getText(),
                    answerResponse,
                    null);
        }).collect(Collectors.toList());
        return new AttemptResponse(attempt.getId(),
                attempt.getCreatedAt(),
                null,
                new UserResponse(attempt.getAuthor().getId(), attempt.getAuthor().getLogin(), attempt.getAuthor().getFirstName(), attempt.getAuthor().getLastName(), null),
                questionResponses);

    }

    @Override
    public TestResponse updateTest(Long test_id, TestRequest testRequest) {
        User user = getCurrentUser();
        Test test = testRepository.findById(test_id).orElseThrow(
                () -> new ResourceNotFoundException("Test", "id", test_id)
        );
        ExhaustedDb exhaustedDb = exhaustedDbRepository.findById(testRequest.getExhaustedDb().getId()).orElseThrow(
                () -> new ResourceNotFoundException("ExhaustedDb", "id", testRequest.getExhaustedDb().getId())
        );
        if (!isCurrentUserCourseAuthor(test.getCourse().getId())) {
            throw new AbuseRightsException(String.format("User '%s' is not author for course '%s'", user.getLogin(), test.getCourse().getId()));
        }
        test.setName(testRequest.getName());
        test.setMaxAttemptsCnt(testRequest.getMaxAttemptsCnt());
        test.setExhaustedDB(exhaustedDb);
        Test updatedTest = testRepository.save(test);
        return new TestResponse(
                updatedTest.getId(),
                updatedTest.getName(),
                updatedTest.getMaxAttemptsCnt(),
                convertToExhaustedDBResponse(updatedTest.getExhaustedDB()),
                null,
                null
        );
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
                    if (!answer.getAttempt().getId().equals(attempt_id)){
                        throw new RuntimeException("Answers assign to different attempts");
                    }
                    answer.setGrade(answerRequest.getGrade());
                    answerRepository.save(answer);
                });
    }

    private ExhaustedDBResponse convertToExhaustedDBResponse(ExhaustedDb exhaustedDb) {
        return new ExhaustedDBResponse(exhaustedDb.getId(),
                exhaustedDb.getName(),
                exhaustedDb.getAuthor().getId(),
                exhaustedDb.getAuthor().getFirstName() + " " + exhaustedDb.getAuthor().getLastName());
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

}
