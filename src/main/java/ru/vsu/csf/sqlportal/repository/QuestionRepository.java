package ru.vsu.csf.sqlportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.sqlportal.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
