package se331.backend.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se331.backend.entity.CreateCommentRequest;
import se331.backend.entity.CreateNewsRequest;
import se331.backend.entity.NewsDTO;
import se331.backend.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {

    @Autowired
    private NewsService newsService; // Inject Interface

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getAllNews(
            @RequestParam(required = false, defaultValue = "all") String status) {

        List<NewsDTO> newsList = newsService.getAllNews(status);
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/removed")
    public ResponseEntity<List<NewsDTO>> getRemovedNews() {
        List<NewsDTO> removedNews = newsService.getRemovedNews();
        return ResponseEntity.ok(removedNews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable Long id) {
        NewsDTO news = newsService.getNewsById(id);
        return ResponseEntity.ok(news);
    }

    @PostMapping
    public ResponseEntity<NewsDTO> createNews(@RequestBody CreateNewsRequest request) {
        NewsDTO createdNews = newsService.createNews(request);
        return new ResponseEntity<>(createdNews, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<NewsDTO> addComment(
            @PathVariable("id") Long newsId,
            @RequestBody CreateCommentRequest request) {

        NewsDTO updatedNews = newsService.addCommentToNews(newsId, request);
        return ResponseEntity.ok(updatedNews);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{newsId}/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentFromNews(
            @PathVariable Long newsId,
            @PathVariable Long commentId) {

        newsService.deleteCommentFromNews(newsId, commentId);
        return ResponseEntity.noContent().build();
    }
}
