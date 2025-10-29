package se331.backend.dao;

import se331.backend.entity.News;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsDao {
    List<News> findAll();
    Optional<News> findById(Long id);
    News save(News news);
    void deleteById(Long id);

    // ค้นหาสำหรับ normal users (ไม่รวม removed)
    Page<News> searchByKeyword(String keyword, Pageable pageable);
    Page<News> findAllVisible(Pageable pageable);

    // ค้นหาสำหรับ admin (รวม removed)
    Page<News> searchByKeywordIncludingRemoved(String keyword, Pageable pageable);
    Page<News> findAll(Pageable pageable);

    // *** เพิ่มใหม่: ค้นหาตามสถานะ ***
    // สำหรับ normal users - ค้นหาตามสถานะ (ไม่รวม removed)
    Page<News> searchByKeywordAndStatus(String keyword, String status, Pageable pageable);
    Page<News> findAllVisibleByStatus(String status, Pageable pageable);

    // สำหรับ admin - ค้นหาตามสถานะ (รวม removed)
    Page<News> searchByKeywordAndStatusIncludingRemoved(String keyword, String status, Pageable pageable);
    Page<News> findAllByStatus(String status, Pageable pageable);
}