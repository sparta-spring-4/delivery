package com.zts.delivery.user.application.service;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.infrastructure.execption.ErrorCode;
import com.zts.delivery.user.application.dto.*;
import com.zts.delivery.user.domain.User;
import com.zts.delivery.user.domain.UserAddress;
import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.domain.UserRole;
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
        User user = dto.toUser(List.of(UserRole.CUSTOMER), registeredAt);
        userRepository.save(user);
    }

    public UserProfile update(UserId userId, UserUpdate dto, LocalDateTime updatedAt) {
        UserProfile foundUser = getUserProfile(userId);
        if (dto.email() != null && !dto.email().equals(foundUser.email())) {
            checkDuplicatedEmail(dto.email());
        }

        User user = User.builder()
                .userId(userId)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phone(dto.phone())
                .updatedAt(updatedAt)
                .build();

        User updatedUSer = userRepository.save(user);
        return UserProfile.of(updatedUSer);
    }

    public void updatePassword(UserId userId, String password) {
        userRepository.updatePassword(userId, password);
    }

    public UserProfile getUserProfile(UserId userId) {
        return userRepository.findById(userId)
                .map(UserProfile::of)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public List<UserAddressInfo> addUserAddresses(UserId userId, RegisterUserAddress registerUserAddress) {
        UserAddress userAddress = registerUserAddress.toUserAddress(UUID.randomUUID());
        List<UserAddress> userAddresses = userRepository.addAddress(userId, userAddress);
        return userAddresses.stream()
                .map(UserAddressInfo::of)
                .toList();
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

    public void deleteUserAddress(UserId userId, UUID targetAddressId) {
        userRepository.deleteAddress(userId, targetAddressId);
    }

    public void changeUserRole(ChangeUserRole changeUserRole) {
        userRepository.changeUserRole(changeUserRole.userId(), changeUserRole.role());
    }
}
