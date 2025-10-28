package se331.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import se331.backend.dao.NewsDao;
import se331.backend.entity.*;
import se331.backend.util.NewsMapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public List<NewsDTO> getAllNews(String statusFilter) {
        List<News> allNews = newsDao.findAll();
        boolean isAdmin = isCurrentUserAdmin();

        List<News> visibleNews = allNews.stream()
                .filter(news -> !news.isRemoved() || isAdmin)
                .collect(Collectors.toList());

        List<NewsDTO> allNewsDTOs = visibleNews.stream()
                .map(newsMapper::toNewsDTO)
                .collect(Collectors.toList());

        if (statusFilter == null || statusFilter.equalsIgnoreCase("all")) {
            return allNewsDTOs;
        }
        return allNewsDTOs.stream()
                .filter(news -> news.getStatus() != null && news.getStatus().equalsIgnoreCase(statusFilter))
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsDTO> getRemovedNews() {
        if (!isCurrentUserAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can view removed news");
        }
        return newsDao.findAll().stream()
                .filter(News::isRemoved)
                .map(newsMapper::toNewsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NewsDTO getNewsById(Long id) {
        News news = newsDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + id));

        if (news.isRemoved() && !isCurrentUserAdmin()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found with id: " + id);
        }

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
        String rawDateTime = request.getDateTime();
        Instant createdAt;
        if (rawDateTime == null || rawDateTime.isBlank()) {
            createdAt = Instant.now();
        } else {
            try {
                createdAt = Instant.parse(rawDateTime);
            } catch (DateTimeParseException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid dateTime format. Expecting ISO-8601, e.g. 2024-05-01T12:34:56Z",
                        ex
                );
            }
        }
        news.setDateTime(createdAt);
        news.setRemoved(false);

        News savedNews = newsDao.save(news);
        return newsMapper.toNewsDTO(savedNews);
    }

    @Override
    @Transactional
    public NewsDTO addCommentToNews(Long newsId, CreateCommentRequest request) {
        News news = newsDao.findById(newsId)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + newsId));

        Comment comment = new Comment();
        comment.setUsername(request.getUsername());
        comment.setText(request.getText());
        comment.setImage(request.getImage());
        comment.setTime(Instant.now());
        comment.setVote(Vote.valueOf(request.getVote().toUpperCase()));
        news.addComment(comment);
        comment.setNews(news);

        news.addComment(comment);

        if (comment.getVote() == Vote.REAL) {
            news.setRealVotes(news.getRealVotes() + 1);
        } else {
            news.setFakeVotes(news.getFakeVotes() + 1);
        }

        News updatedNews = newsDao.save(news);
        return newsMapper.toNewsDTO(updatedNews);
    }

    @Override
    @Transactional
    public void deleteNews(Long id) {
        News news = newsDao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found with id: " + id));
        news.setRemoved(true);
        newsDao.save(news);
    }

    @Override
    @Transactional
    public void deleteCommentFromNews(Long newsId, Long commentId) {
        News news = newsDao.findById(newsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found with id: " + newsId));

        Comment targetComment = news.getComments().stream()
                .filter(comment -> comment.getId() != null && comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Comment not found with id: " + commentId + " for news id: " + newsId));

        news.removeComment(targetComment);
        newsDao.save(news);
    }

    @Override
    public Page<NewsDTO> searchNews(String title, Pageable pageable) {
        Page<News> newsPage;
        boolean isAdmin = isCurrentUserAdmin();

        if (title != null && !title.isBlank()) {
            // มี keyword
            if (isAdmin) {
                newsPage = newsDao.searchByKeywordIncludingRemoved(title, pageable);
            } else {
                newsPage = newsDao.searchByKeyword(title, pageable);
            }
        } else {
            // ไม่มี keyword
            if (isAdmin) {
                newsPage = newsDao.findAll(pageable);
            } else {
                newsPage = newsDao.findAllVisible(pageable);
            }
        }

        // แค่ map เป็น DTO
        return newsPage.map(newsMapper::toNewsDTO);
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }
}