package se331.backend.dao;

import se331.backend.entity.News;

import java.util.List;
import java.util.Optional;

public interface NewsDao {
    List<News> findAll();
    Optional<News> findById(Long id);
    News save(News news);
}