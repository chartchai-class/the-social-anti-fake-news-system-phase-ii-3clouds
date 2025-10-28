package se331.backend.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import se331.backend.entity.News;
import se331.backend.repository.NewsRepository;

import java.util.*;
import java.util.stream.Collectors;

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
    public Page<News> searchByKeyword(String keyword, Pageable pageable) {
        // ใช้ LinkedHashSet เพื่อเก็บลำดับ
        Set<News> results = new LinkedHashSet<>();

        // ค้นหาจาก topic
        List<News> topicResults = newsRepository.findByRemovedFalseAndTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults);

        // ค้นหาจาก shortDetail
        List<News> shortDetailResults = newsRepository.findByRemovedFalseAndShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        // ค้นหาจาก reporter
        List<News> reporterResults = newsRepository.findByRemovedFalseAndReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        // แปลง Set เป็น List
        List<News> combinedList = new ArrayList<>(results);

        // คำนวณ pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedList.size());

        List<News> pageContent = start >= combinedList.size() ?
                List.of() : combinedList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, combinedList.size());
    }

    @Override
    public Page<News> findAllVisible(Pageable pageable) {
        return newsRepository.findByRemovedFalse(pageable);
    }

    // สำหรับ admin
    @Override
    public Page<News> searchByKeywordIncludingRemoved(String keyword, Pageable pageable) {
        // ใช้ LinkedHashSet เพื่อเก็บลำดับ
        Set<News> results = new LinkedHashSet<>();

        // ค้นหาจาก topic
        List<News> topicResults = newsRepository.findByTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults);

        // ค้นหาจาก shortDetail
        List<News> shortDetailResults = newsRepository.findByShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        // ค้นหาจาก reporter
        List<News> reporterResults = newsRepository.findByReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        // แปลง Set เป็น List
        List<News> combinedList = new ArrayList<>(results);

        // คำนวณ pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedList.size());

        List<News> pageContent = start >= combinedList.size() ?
                List.of() : combinedList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, combinedList.size());
    }

    @Override
    public Page<News> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }
}