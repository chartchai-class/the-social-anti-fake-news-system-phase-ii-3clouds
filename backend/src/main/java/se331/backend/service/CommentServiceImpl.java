package se331.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se331.backend.entity.Comment;
import se331.backend.dao.CommentDAO;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Override
    @Transactional
    public Comment save(Comment comment) {
        return commentDAO.save(comment);
    }

    @Override
    public Page<Comment> getCommentsByNewsId(Long newsId, Pageable pageable) {
        return commentDAO.findByNewsId(newsId, pageable);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        commentDAO.deleteById(id);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentDAO.findById(id);
    }
}