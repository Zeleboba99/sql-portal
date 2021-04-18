package ru.vsu.csf.sqlportal.service;

import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.dto.response.*;
import ru.vsu.csf.sqlportal.model.*;

import java.util.List;

@Service
public class ConverterService {
    public static CourseResponse convertToCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getAuthor().getId(),
                course.getAuthor().getFirstName() + " " + course.getAuthor().getLastName()
        );
    }

    public static DbInfoResponse convertToDbInfoResponse(DbInfo dbInfo) {
        return new DbInfoResponse(dbInfo.getId(),
                dbInfo.getName(),
                dbInfo.getSchemaImage(),
                dbInfo.getAuthor().getId(),
                dbInfo.getAuthor().getFirstName() + " " + dbInfo.getAuthor().getLastName());
    }

    public static TestResponse convertToTestResponse(Test test, DbInfoResponse dbResponse, CourseResponse courseResponse, List<QuestionResponse> questionResponses) {
        return new TestResponse(
                test.getId(),
                test.getName(),
                test.getNumber(),
                test.getMaxAttemptsCnt(),
                dbResponse,
                courseResponse,
                questionResponses
        );
    }

    public static AttemptResponse convertToAttemptResponse(Attempt attempt, TestResponse testResponse, UserResponse userResponse, List<QuestionResponse> questionResponses) {
        return new AttemptResponse(attempt.getId(),
                attempt.getCreatedAt(),
                attempt.getMark(),
                testResponse,
                userResponse,
                questionResponses);
    }

    public static QuestionResponse convertToQuestionResponse(Question question, AnswerResponse answerResponse, TestResponse testResponse) {
        return new QuestionResponse(
                question.getId(),
                question.getText(),
                answerResponse,
                testResponse
        );
    }

    public static UserResponse convertToUserResponse(User user) {
        return new UserResponse(user.getId(), user.getLogin(), user.getFirstName(), user.getLastName(), user.getRole().name());
    }

}
