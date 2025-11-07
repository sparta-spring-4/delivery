package com.zts.delivery.store.application.service;

import com.zts.delivery.store.domain.*;
import com.zts.delivery.store.domain.service.StoreAddressService;
import com.zts.delivery.store.presentation.dto.StoreRequest;
import com.zts.delivery.user.domain.UserId;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreCreateService {

    private final StoreRepository repository;
    private final StoreAddressService addressService;

    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StoreId create(StoreRequest req) {
        // 회원정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UUID userId = principal.userId().getId();
        String name = principal.name();

        // 등록 가능 여부 확인
        LocalTime startHour = req.startHour();
        LocalTime endHour = req.endHour();
        if (startHour != null && endHour != null && endHour.isBefore(startHour)) {
            LocalTime tmp = endHour;
            endHour = startHour;
            startHour = tmp;
        }

        List<Double> coords = addressService.getCoordinate(req.storeAddress());
        StoreAddress address = new StoreAddress(req.storeAddress(), coords.get(0), coords.get(1));

        Store store = Store.builder()
                .storeName(req.storeName())
                .storeTel(req.storeTel())
                .address(address)
                .categories(req.category() == null ? null : req.category().stream()
                        .map(c -> new StoreCategory(c.category(), c.active())).toList())
                .startHour(startHour)
                .endHour(endHour)
                .weekdays(req.weekdays())
                .userId(UserId.of(userId))
                .userName(name)
                .build();


        repository.save(store);

        return store.getId();
    }
}
