package ru.vsu.csf.sqlportal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.sqlportal.model.DbInfo;

@Repository
public interface DbInfoRepository extends JpaRepository<DbInfo, Long> {
    Page<DbInfo> findAllByAuthorId(Long author_id, Pageable pageable);
}
