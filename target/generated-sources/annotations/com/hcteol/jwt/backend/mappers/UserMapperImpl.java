package com.hcteol.jwt.backend.mappers;

import com.hcteol.jwt.backend.dtos.SignUpDto;
import com.hcteol.jwt.backend.dtos.UserDto;
import com.hcteol.jwt.backend.entities.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T00:10:29+0900",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.companyId( user.getCompanyId() );
        userDto.level( user.getLevel() );
        userDto.id( user.getId() );
        userDto.firstName( user.getFirstName() );
        userDto.lastName( user.getLastName() );
        userDto.login( user.getLogin() );
        userDto.mobileNumber( user.getMobileNumber() );
        userDto.lastPasswordChanged( user.getLastPasswordChanged() );
        userDto.active( user.getActive() );

        return userDto.build();
    }

    @Override
    public User signUpToUser(SignUpDto signUpDto) {
        if ( signUpDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.firstName( signUpDto.getFirstName() );
        user.lastName( signUpDto.getLastName() );
        user.login( signUpDto.getLogin() );
        user.mobileNumber( signUpDto.getMobileNumber() );

        return user.build();
    }
}
