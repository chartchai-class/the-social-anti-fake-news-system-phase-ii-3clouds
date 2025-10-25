package se331.backend.entity;

public class CommentDTO {
    private Long id;
    private String user;
    private String text;
    private String image;
    private String time;
    private String vote;

    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getVote() { return vote; }
    public void setVote(String vote) { this.vote = vote; }
}