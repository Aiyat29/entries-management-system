package kg.alatoo.entryapi.services.crud;

import kg.alatoo.entryapi.dto.UserDTO;
import kg.alatoo.entryapi.entities.User;

import java.util.List;

public interface UserService {
    UserDTO saveUser(UserDTO userDTO);
    User getCurrentUser();
    void deleteUser();
    void deleteUserById(Long id);
    UserDTO findUserByID(Long id);
    List<UserDTO> getAllUsers();
}
