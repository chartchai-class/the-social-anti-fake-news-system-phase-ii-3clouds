package se331.backend.service;

import se331.backend.entity.CreateCommentRequest;
import se331.backend.entity.CreateNewsRequest;
import se331.backend.entity.NewsDTO;

import java.util.List;

public interface NewsService {
    List<NewsDTO> getAllNews(String statusFilter);
    NewsDTO getNewsById(Long id);
    NewsDTO createNews(CreateNewsRequest request);
    NewsDTO addCommentToNews(Long newsId, CreateCommentRequest request);
}