package se331.backend.security.user;

public interface UserDao {
    User findByUsername(String username);

    User save(User user);
}