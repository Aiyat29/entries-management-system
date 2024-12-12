package kg.alatoo.entryapi.mappers;

import kg.alatoo.entryapi.dto.UserDTO;
import kg.alatoo.entryapi.dto.authorization.AuthRegistrationDTO;
import kg.alatoo.entryapi.entities.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User userDtoToUser(UserDTO dto);
    User authRegistrationDtoToUserEntity(AuthRegistrationDTO dto);
    UserDTO userToUserDto(User user);
}
