package se331.backend.service;

import se331.backend.entity.CreateCommentRequest;
import se331.backend.entity.CreateNewsRequest;
import se331.backend.entity.NewsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService {
    List<NewsDTO> getAllNews(String statusFilter);
    List<NewsDTO> getRemovedNews();
    NewsDTO getNewsById(Long id);
    NewsDTO createNews(CreateNewsRequest request);
    NewsDTO addCommentToNews(Long newsId, CreateCommentRequest request);
    void deleteNews(Long id);
    void deleteCommentFromNews(Long newsId, Long commentId);

    // เพิ่ม search method with pagination
    Page<NewsDTO> searchNews(String title, Pageable pageable);
}
