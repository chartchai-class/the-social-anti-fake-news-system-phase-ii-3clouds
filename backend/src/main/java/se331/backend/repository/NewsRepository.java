package se331.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se331.backend.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}