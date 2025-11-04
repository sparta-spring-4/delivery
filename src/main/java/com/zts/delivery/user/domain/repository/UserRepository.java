package com.zts.delivery.user.domain.repository;

import com.zts.delivery.user.domain.User;
import com.zts.delivery.user.domain.UserAddress;
import com.zts.delivery.user.domain.UserId;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UserId id);

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void updatePassword(UserId id, String password);

    void deleteById(UserId userId);

    User addAddress(UserId userId, UserAddress userAddress);

    void deleteAddress(UserId userId, UUID addressId);
}
