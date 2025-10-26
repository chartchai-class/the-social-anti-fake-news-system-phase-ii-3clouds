package se331.backend.security.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se331.backend.entity.UserAuthDTO; // ⭐️ Import DTO
import se331.backend.util.NewsMapper;   // ⭐️ Import Mapper
import java.util.List;
import java.util.stream.Collectors; // ⭐️ Import Stream

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    final UserDao userDao;
    final NewsMapper newsMapper;

    @Override
    @Transactional
    public User save(User user) {
        return userDao.save(user);
    }

    @Override
    @Transactional
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional
    public User findById(Integer id) {
        return userDao.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Admin Controller
    @Override
    @Transactional
    public List<UserAuthDTO> getAllUsers() {
        return userDao.findAll().stream()
                .map(newsMapper::toUserAuthDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserAuthDTO promoteUserToMember(Integer userId) {
        User user = findById(userId);
        if (!user.getRoles().contains(Role.ROLE_MEMBER)) {
            user.getRoles().add(Role.ROLE_MEMBER);
            user.getRoles().remove(Role.ROLE_READER); // (ลบ Role เก่าออก)
        }
        User savedUser = userDao.save(user);
        return newsMapper.toUserAuthDTO(savedUser);
    }
}