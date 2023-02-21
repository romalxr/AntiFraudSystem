package antifraud.mapper;

import antifraud.dto.UserDTO;
import antifraud.entity.User;

public class UserMapper {
    public static User toEntity(UserDTO userDTO){
        return User.builder()
                .name(userDTO.getName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();
    }

    public static UserDTO toDTO(User userEntity){
        return UserDTO.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();
    }
}
