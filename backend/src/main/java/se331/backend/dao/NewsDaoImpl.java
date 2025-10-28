package se331.backend.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import se331.backend.entity.News;
import se331.backend.repository.NewsRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class NewsDaoImpl implements NewsDao {

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public List<News> findAll() {
        return newsRepository.findAll();
    }

    @Override
    public Optional<News> findById(Long id) {
        return newsRepository.findById(id);
    }

    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }

    // สำหรับ normal users
    @Override
    public Page<News> searchByKeyword(String title, Pageable pageable) {
        if (title != null && !title.isBlank()) {
            // ถ้าค้นหาคำในหัวข้อ, รายละเอียดสั้น, หรือผู้รายงาน
            return newsRepository.findByRemovedFalseAndTopicContainingIgnoreCaseAndShortDetailContainingIgnoreCaseAndReporterContainingIgnoreCase(
                    title, title, title, pageable);
        }
        // ถ้าไม่ค้นหาอะไร ค้นหาข่าวที่ไม่ได้ถูกลบ
        return newsRepository.findByRemovedFalse(pageable);
    }

    @Override
    public Page<News> findAllVisible(Pageable pageable) {
        return newsRepository.findByRemovedFalse(pageable);
    }

    // สำหรับ admin
    @Override
    public Page<News> searchByKeywordIncludingRemoved(String title, Pageable pageable) {
        if (title != null && !title.isBlank()) {
            // ถ้าค้นหาคำในหัวข้อ, รายละเอียดสั้น, หรือผู้รายงาน
            return newsRepository.findByTopicContainingIgnoreCaseOrShortDetailContainingIgnoreCaseOrReporterContainingIgnoreCase(
                    title, title, title, pageable);
        }
        // ถ้าไม่ค้นหาอะไร ให้ค้นหาทุกข่าว
        return newsRepository.findAll(pageable);
    }

    @Override
    public Page<News> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

}
