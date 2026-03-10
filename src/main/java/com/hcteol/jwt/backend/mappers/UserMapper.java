package com.hcteol.jwt.backend.mappers;

import com.hcteol.jwt.backend.dtos.SignUpDto;
import com.hcteol.jwt.backend.dtos.UserDto;
import com.hcteol.jwt.backend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "token", ignore = true)
    @Mapping(target = "companyId", source = "companyId")
    @Mapping(target = "level", source = "level")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "lastPasswordChanged", source = "lastPasswordChanged")
    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "lastPasswordChanged", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}
