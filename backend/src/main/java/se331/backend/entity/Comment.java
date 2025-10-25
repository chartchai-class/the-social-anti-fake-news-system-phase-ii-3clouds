package se331.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 'user' อาจเป็นคำสงวน
    private String text;
    private String image; // URL to image
    private Instant time;

    @Enumerated(EnumType.STRING) // เก็บเป็น "REAL" หรือ "FAKE" ใน DB
    private Vote vote;

    // ความสัมพันธ์: คอมเมนต์หลายอันอยู่ในข่าวเดียว
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    @JsonIgnore // ป้องกันการวนลูปไม่รู้จบเวลาแปลงเป็น JSON
    private News news;

    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
    public Vote getVote() { return vote; }
    public void setVote(Vote vote) { this.vote = vote; }
    public News getNews() { return news; }
    public void setNews(News news) { this.news = news; }
}