package by.salary.authorizationserver.service;

import by.salary.authorizationserver.model.UserInfoDTO;
import by.salary.authorizationserver.model.dto.AuthenticationRequestDto;
import by.salary.authorizationserver.model.dto.AuthenticationResponseDto;
import by.salary.authorizationserver.model.entity.Authority;
import by.salary.authorizationserver.model.oauth2.AuthenticationRegistrationId;
import by.salary.authorizationserver.repository.AuthorizationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserService implements UserDetailsService {
    AuthorizationRepository authorizationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthenticationRequestDto request = AuthenticationRequestDto.builder()
                .authenticationRegistrationId(AuthenticationRegistrationId.local)
                .username(username)
                .build();

        return mapToUserInfoDto(authorizationRepository.find(request).orElseThrow(
                () -> new UsernameNotFoundException("User not found")));
    }

    private UserDetails mapToUserInfoDto(AuthenticationResponseDto authenticationResponseDto) {
        return UserInfoDTO.builder()
                .name(authenticationResponseDto.getUserName())
                .email(authenticationResponseDto.getUserEmail())
                .authorities(authenticationResponseDto.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toList()))
                .password(authenticationResponseDto.getPassword())
                .build();
    }
}
