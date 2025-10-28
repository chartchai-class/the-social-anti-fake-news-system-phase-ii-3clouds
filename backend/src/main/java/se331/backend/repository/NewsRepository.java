package se331.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se331.backend.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findByRemovedFalse(Pageable pageable);

    // สำหรับ normal users (กรอง removed)
    Page<News> findByRemovedFalseAndTopicContainingIgnoreCaseAndShortDetailContainingIgnoreCaseAndReporterContainingIgnoreCase(
            String topic, String shortDetail, String reporter, Pageable pageable);

    // สำหรับ admin (แสดงทุกข่าว ไม่กรอง removed)
    Page<News> findByTopicContainingIgnoreCaseOrShortDetailContainingIgnoreCaseOrReporterContainingIgnoreCase(
            String topic, String shortDetail, String reporter, Pageable pageable);

}