package se331.backend.entity;

public class CreateCommentRequest {
    private String user;
    private String text;
    private String image;
    private String vote;

    // Getters and Setters...
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getVote() { return vote; }
    public void setVote(String vote) { this.vote = vote; }
}