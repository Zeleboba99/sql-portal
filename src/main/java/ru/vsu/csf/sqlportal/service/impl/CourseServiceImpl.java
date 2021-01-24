package ru.vsu.csf.sqlportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.vsu.csf.sqlportal.dto.request.CourseRequest;
import ru.vsu.csf.sqlportal.dto.response.CourseResponse;
import ru.vsu.csf.sqlportal.exception.AbuseRightsException;
import ru.vsu.csf.sqlportal.exception.ResourceNotFoundException;
import ru.vsu.csf.sqlportal.model.Course;
import ru.vsu.csf.sqlportal.model.Role;
import ru.vsu.csf.sqlportal.model.User;
import ru.vsu.csf.sqlportal.repository.CourseRepository;
import ru.vsu.csf.sqlportal.repository.UserRepository;
import ru.vsu.csf.sqlportal.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CourseResponse> getCourses(Long author_id) {
        return courseRepository.findAllByAuthorId(author_id).stream().map(
                course -> {
                    return new CourseResponse(course.getId(),
                            course.getName(),
                            course.getDescription(),
                            course.getAuthor().getFirstName() + " " + course.getAuthor().getLastName());
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(
                course -> {
                    return new CourseResponse(course.getId(),
                            course.getName(),
                            course.getDescription(),
                            course.getAuthor().getFirstName() + " " + course.getAuthor().getLastName());
                }
        ).collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseById(Long course_id) {
        Course course = courseRepository.findById(course_id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", course_id)
        );
        return new CourseResponse(course.getId(),
                course.getName(),
                course.getDescription(),
                course.getAuthor().getFirstName() + " " + course.getAuthor().getLastName());
    }

    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        Course course = new Course(courseRequest.getName(),
                courseRequest.getDescription(),
                author);

        Course newCourse = courseRepository.save(course);
        return new CourseResponse(newCourse.getId(),
                newCourse.getName(),
                newCourse.getDescription(),
                newCourse.getAuthor().getFirstName() + " " + newCourse.getAuthor().getLastName());
    }

    @Override
    public void deleteCourse(Long course_id) {
        String authorLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByLogin(authorLogin).orElseThrow(
                () -> new ResourceNotFoundException("User", "login", authorLogin)
        );
        Course course = courseRepository.findById(course_id).orElseThrow(
                () -> new ResourceNotFoundException("Course", "id", course_id)
        );
        if (!author.getId().equals(course.getAuthor().getId())) {
            throw new AbuseRightsException(authorLogin);
        }
        courseRepository.deleteById(course_id);
    }
}
