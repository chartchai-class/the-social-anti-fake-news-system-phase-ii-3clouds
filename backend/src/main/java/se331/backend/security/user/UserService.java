package se331.backend.security.user;

import jakarta.transaction.Transactional;
import java.util.List;

public interface UserService {
    User save(User user);

    @Transactional
    User findByUsername(String username);

    @Transactional
    List<User> findAllUsers();

    @Transactional
    User findById(Integer id);

    @Transactional
    User updateUserRole(Integer userId, Role newRole);
}
