package se331.backend.util;

import se331.backend.entity.*;

import org.springframework.stereotype.Component;
import se331.backend.security.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewsMapper {

    public UserAuthDTO toUserAuthDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserAuthDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

    public NewsDTO toNewsDTO(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setId(news.getId());
        dto.setTopic(news.getTopic());

        dto.setShortDetail(news.getShortDetail());
        dto.setFullDetail(news.getFullDetail());
        dto.setImage(news.getImage());
        dto.setReporter(news.getReporter());
        dto.setDateTime(news.getDateTime().toString());

        List<CommentDTO> commentDTOs = news.getComments().stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());
        dto.setComments(commentDTOs);

        VoteSummaryDTO summary = calculateVoteSummary(news.getComments());
        dto.setVoteSummary(summary);
        dto.setTotalVotes((int) (summary.getReal() + summary.getFake()));
        dto.setStatus(calculateStatus(summary));

        return dto;
    }

    public CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setUser(comment.getUsername());
        dto.setText(comment.getText());
        dto.setImage(comment.getImage());
        dto.setTime(comment.getTime().toString());
        dto.setVote(comment.getVote().name().toLowerCase());
        return dto;
    }

    private VoteSummaryDTO calculateVoteSummary(List<Comment> comments) {
        long realVotes = comments.stream()
                .filter(c -> c.getVote() == Vote.REAL)
                .count();
        long fakeVotes = comments.stream()
                .filter(c -> c.getVote() == Vote.FAKE)
                .count();
        return new VoteSummaryDTO(realVotes, fakeVotes);
    }

    private String calculateStatus(VoteSummaryDTO summary) {
        if (summary.getReal() > summary.getFake()) {
            return "not fake";
        } else if (summary.getReal() < summary.getFake()) {
            return "fake";
        } else {
            return "equal";
        }
    }
}