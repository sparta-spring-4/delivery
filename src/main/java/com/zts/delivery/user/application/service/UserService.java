package com.zts.delivery.user.application.service;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.user.application.dto.RegisterUserAddress;
import com.zts.delivery.user.application.dto.UserProfile;
import com.zts.delivery.user.application.dto.UserRegister;
import com.zts.delivery.user.application.dto.UserUpdate;
import com.zts.delivery.user.domain.UserAddress;
import com.zts.delivery.user.domain.UserRole;
import com.zts.delivery.user.domain.User;
import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(UserRegister dto, LocalDateTime registeredAt) {
        checkDuplicatedUsername(dto.username());
        checkDuplicatedEmail(dto.email());
        User user = dto.toUser(List.of(UserRole.USER), registeredAt);
        userRepository.save(user);
    }

    public UserProfile update(UUID userId, UserUpdate dto, LocalDateTime updatedAt) {
        UserProfile foundUser = getUserProfile(userId);
        if (dto.email() != null && !dto.email().equals(foundUser.email())) {
            checkDuplicatedEmail(dto.email());
        }

        User user = User.builder()
                .userId(UserId.of(userId))
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phone(dto.phone())
                .updatedAt(updatedAt)
                .build();

        User updatedUSer = userRepository.save(user);
        return UserProfile.of(updatedUSer);
    }

    public void updatePassword(UUID userId, String password) {
        userRepository.updatePassword(UserId.of(userId), password);
    }

    public UserProfile getUserProfile(UUID userId) {
        return userRepository.findById(UserId.of(userId))
                .map(UserProfile::of)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public UserProfile addUserAddresses(UserId userId, List<RegisterUserAddress> registerUserAddresses) {
        List<UserAddress> userAddresses = registerUserAddresses.stream()
                .map(it -> it.toUserAddress(UUID.randomUUID()))
                .toList();
        User user = userRepository.addAddresses(userId, userAddresses);
        return UserProfile.of(user);
    }

    private void checkDuplicatedUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new ApplicationException(ErrorCode.DUPLICATED_USERNAME);
        }
    }

    private void checkDuplicatedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApplicationException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    public void withdraw(UserId userId) {
        userRepository.deleteById(userId);
    }
}
