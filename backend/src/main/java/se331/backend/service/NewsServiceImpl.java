package se331.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se331.backend.dao.NewsDao;
import se331.backend.entity.*;
import se331.backend.util.NewsMapper;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsDao newsDao; // Inject DAO

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public List<NewsDTO> getAllNews(String statusFilter) {
        // 1. ดึงข้อมูลจาก DAO
        List<News> allNews = newsDao.findAll();

        // 2. แปลง (Logic เหมือนเดิม)
        List<NewsDTO> allNewsDTOs = allNews.stream()
                .map(newsMapper::toNewsDTO)
                .collect(Collectors.toList());

        // 3. กรอง (Logic เหมือนเดิม)
        if (statusFilter == null || statusFilter.equalsIgnoreCase("all")) {
            return allNewsDTOs;
        }
        return allNewsDTOs.stream()
                .filter(news -> news.getStatus().equalsIgnoreCase(statusFilter))
                .collect(Collectors.toList());
    }

    @Override
    public NewsDTO getNewsById(Long id) {
        News news = newsDao.findById(id) // เรียกผ่าน DAO
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + id));

        return newsMapper.toNewsDTO(news);
    }

    @Override
    @Transactional
    public NewsDTO createNews(CreateNewsRequest request) {
        News news = new News();
        news.setTopic(request.getTopic());
        news.setShortDetail(request.getShortDetail());
        news.setFullDetail(request.getFullDetail());
        news.setImage(request.getImage());
        news.setReporter(request.getReporter());
        news.setDateTime(Instant.parse(request.getDateTime()));

        News savedNews = newsDao.save(news); // เรียกผ่าน DAO
        return newsMapper.toNewsDTO(savedNews);
    }

    @Override
    @Transactional
    public NewsDTO addCommentToNews(Long newsId, CreateCommentRequest request) {
        News news = newsDao.findById(newsId) // เรียกผ่าน DAO
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + newsId));

        Comment comment = new Comment();
        comment.setUsername(request.getUser());
        comment.setText(request.getText());
        comment.setImage(request.getImage());
        comment.setTime(Instant.now());
        comment.setVote(Vote.valueOf(request.getVote().toUpperCase()));
        comment.setNews(news);

        news.getComments().add(comment);

        News updatedNews = newsDao.save(news); // เรียกผ่าน DAO

        return newsMapper.toNewsDTO(updatedNews);
    }
}