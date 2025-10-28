package se331.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se331.backend.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // สำหรับ normal users - ไม่รวม removed
    Page<News> findByRemovedFalseAndTopicContainingIgnoreCase(String topic, Pageable pageable);
    Page<News> findByRemovedFalseAndShortDetailContainingIgnoreCase(String shortDetail, Pageable pageable);
    Page<News> findByRemovedFalseAndReporterContainingIgnoreCase(String reporter, Pageable pageable);
    Page<News> findByRemovedFalse(Pageable pageable);

    // สำหรับ admin - รวม removed
    Page<News> findByTopicContainingIgnoreCase(String topic, Pageable pageable);
    Page<News> findByShortDetailContainingIgnoreCase(String shortDetail, Pageable pageable);
    Page<News> findByReporterContainingIgnoreCase(String reporter, Pageable pageable);
}