package ru.vsu.csf.sqlportal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.sqlportal.model.ExhaustedDb;

import java.util.List;

@Repository
public interface ExhaustedDbRepository extends JpaRepository<ExhaustedDb, Long> {
    Page<ExhaustedDb> findAllByAuthorId(Long author_id, Pageable pageable);
}
