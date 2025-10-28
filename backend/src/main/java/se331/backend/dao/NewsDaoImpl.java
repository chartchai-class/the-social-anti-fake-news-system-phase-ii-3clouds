package se331.backend.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import se331.backend.entity.News;
import se331.backend.repository.NewsRepository;

import java.util.*;

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

    // สำหรับ normal users - ไม่ filter removed
    @Override
    public Page<News> searchByKeyword(String keyword, Pageable pageable) {
        Set<News> results = new LinkedHashSet<>();

        List<News> topicResults = newsRepository.findByRemovedFalseAndTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults);

        List<News> shortDetailResults = newsRepository.findByRemovedFalseAndShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        List<News> reporterResults = newsRepository.findByRemovedFalseAndReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        return createPageFromList(new ArrayList<>(results), pageable);
    }

    @Override
    public Page<News> findAllVisible(Pageable pageable) {
        return newsRepository.findByRemovedFalse(pageable);
    }

    // สำหรับ admin - รวม removed
    @Override
    public Page<News> searchByKeywordIncludingRemoved(String keyword, Pageable pageable) {
        Set<News> results = new LinkedHashSet<>();

        List<News> topicResults = newsRepository.findByTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults);

        List<News> shortDetailResults = newsRepository.findByShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        List<News> reporterResults = newsRepository.findByReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        return createPageFromList(new ArrayList<>(results), pageable);
    }

    @Override
    public Page<News> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    // Helper method เพื่อลด code duplication
    private Page<News> createPageFromList(List<News> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        List<News> pageContent = start >= list.size() ? List.of() : list.subList(start, end);

        return new PageImpl<>(pageContent, pageable, list.size());
    }
}