package kg.alatoo.entryapi.services.crud.iml;

import kg.alatoo.entryapi.dto.UserDTO;
import kg.alatoo.entryapi.entities.User;
import kg.alatoo.entryapi.exceptions.ApiException;
import kg.alatoo.entryapi.mappers.UserMapper;
import kg.alatoo.entryapi.repositories.UserRepository;
import kg.alatoo.entryapi.services.crud.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceJPA implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceJPA(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO findUserByID(Long id) {
        Optional<User> optionalUserEntity = userRepository.findById(id);
        User user = optionalUserEntity.orElseThrow(() -> new ApiException("User not found with id" + id , HttpStatusCode.valueOf(409)));
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User user = userMapper.userDtoToUser(userDTO);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        try {
            User savedUser = userRepository.save(user);
            return userMapper.userToUserDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException("User " + userDTO.getUsername() + " is already exists", HttpStatusCode.valueOf(409));
        } catch (Exception e) {
            throw new ApiException("Error while user creating", HttpStatusCode.valueOf(400));
        }
    }

    @Override
    public void deleteUser() {
        User user = getCurrentUser();
        userRepository.deleteById(user.getId());
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiException("Test not found with id: " + id, HttpStatusCode.valueOf(409));
        }
        userRepository.deleteById(id);
    }


    @Override
    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof User userDetails) {
            Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
            User user = optionalUser.orElseThrow(() -> new ApiException("User not found with id " + userDetails.getId(), HttpStatusCode.valueOf(409)));
            return user;
        } else {
            return null;
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

}
