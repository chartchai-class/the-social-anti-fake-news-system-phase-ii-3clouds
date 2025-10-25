package se331.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    @Lob // สำหรับข้อความยาวๆ
    private String shortDetail;

    @Lob // สำหรับข้อความยาวๆ
    private String fullDetail;

    private String image; // URL to image
    private String reporter;
    private Instant dateTime; // ใช้ Instant สำหรับเวลาที่เป็นมาตรฐาน

    // ความสัมพันธ์: ข่าวหนึ่งข่าวมีได้หลายคอมเมนต์
    // CascadeType.ALL หมายถึงถ้าลบข่าว ให้ลบคอมเมนต์ด้วย
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    @Column(columnDefinition = "integer default 0")
    private int realVotes = 0;

    @Column(columnDefinition = "integer default 0")
    private int fakeVotes = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getShortDetail() { return shortDetail; }
    public void setShortDetail(String shortDetail) { this.shortDetail = shortDetail; }
    public String getFullDetail() { return fullDetail; }
    public void setFullDetail(String fullDetail) { this.fullDetail = fullDetail; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getReporter() { return reporter; }
    public void setReporter(String reporter) { this.reporter = reporter; }
    public Instant getDateTime() { return dateTime; }
    public void setDateTime(Instant dateTime) { this.dateTime = dateTime; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public int getRealVotes() { return realVotes; }
    public void setRealVotes(int realVotes) { this.realVotes = realVotes; }
    public int getFakeVotes() { return fakeVotes; }
    public void setFakeVotes(int fakeVotes) { this.fakeVotes = fakeVotes; }
}