package com.eldercare.service;

import com.eldercare.dto.LoginRequest;
import com.eldercare.dto.LoginResponse;
import com.eldercare.dto.RegisterRequest;
import com.eldercare.entity.User;
import com.eldercare.repository.UserRepository;
import com.eldercare.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        User.Role role = request.getRole() == User.Role.ADMIN ? User.Role.FAMILY : request.getRole();
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .realName(request.getRealName() != null ? request.getRealName() : request.getUsername())
                .phone(request.getPhone())
                .role(role)
                .enabled(true)
                .build();
        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole().name())
                .linkedElderId(user.getLinkedElderId())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!user.getEnabled()) {
            throw new RuntimeException("账号已禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole().name())
                .linkedElderId(user.getLinkedElderId())
                .build();
    }

    @org.springframework.transaction.annotation.Transactional
    public void setLinkedElder(Long userId, Long linkedElderId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setLinkedElderId(linkedElderId);
        userRepository.save(user);
    }
}
