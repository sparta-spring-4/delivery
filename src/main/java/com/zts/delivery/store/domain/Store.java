package com.zts.delivery.store.domain;

import com.zts.delivery.global.persistence.common.BaseEntity;
import com.zts.delivery.store.domain.exception.StoreNotEditableException;
import com.zts.delivery.store.domain.exception.StoreNotFoundException;
import com.zts.delivery.store.domain.service.StoreAddressService;
import com.zts.delivery.user.domain.UserId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ToString
@Getter
@Entity
@Table(name = "P_STORE")
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @EmbeddedId
    private StoreId id;

    @Embedded
    private Owner owner;

    @Column(length = 100, nullable = false)
    private String storeName;

    @Column(length = 45, nullable = false)
    private String storeTel;

    @Embedded
    private StoreAddress address;

    @Embedded
    private OperatingInfo operatingInfo;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "P_STORE_CATEGORY", joinColumns = @JoinColumn(name = "store_id"))
    @OrderColumn(name = "category_idx")
    private List<StoreCategory> categories;

    private int reviewCount;

    private BigDecimal averageReviewScore;

    @Builder
    public Store(StoreId id, String storeName, String storeTel, LocalTime startHour, LocalTime endHour, List<DayOfWeek> weekdays,
                 UserId userId, String userName, String address, List<StoreCategory> categories, StoreAddressService addressService) {

        this.id = Objects.requireNonNullElse(id, StoreId.of());
        this.owner = new Owner(userId, userName);
        this.storeName = storeName;
        this.storeTel = storeTel;
        this.operatingInfo = new OperatingInfo(startHour, endHour, weekdays);
        setCategories(categories);

        // 주소 -> 좌표 변환
        changeAddress(address, addressService);

        averageReviewScore = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
    }

    private void setCategories(List<StoreCategory> categories) {
        if (categories == null || categories.isEmpty()) return;

        this.categories = categories.stream().distinct().collect(Collectors.toCollection(ArrayList::new)); // 변경 가능한 리스트로 변환
    }

    // 수정 및 삭제 권한
    public void isEditable(RoleCheck roleCheck) {
        if (!roleCheck.check(this)) {
            // 권한이 없는 경우
            throw new StoreNotEditableException();
        }
    }

    // 매장 일반 정보 수정
    public void changeInfo(String storeName, String storeTel) {
        this.storeName = storeName;
        this.storeTel = storeTel;
    }

    // 매장 주소 등록/변경 시 위도/경도 정보도 갱신
    public void changeAddress(String address, StoreAddressService service) {
        if (!StringUtils.hasText(address) || service == null) return;
        List<Double> coords = service.getCoordinate(address);
        this.address = new StoreAddress(address, coords.get(0), coords.get(1));
    }

    // 매장 운영시간, 운영 요일 정보 수정
    public void changeOperatingInfo(LocalTime startHour, LocalTime endHour, List<DayOfWeek> weekdays) {
        // 등록 가능 여부 확인
        if (startHour != null && endHour != null && endHour.isBefore(startHour)) {
            LocalTime tmp = endHour;
            endHour = startHour;
            startHour = tmp;
        }

        this.operatingInfo = new OperatingInfo(startHour, endHour, weekdays);
    }

    // 매장 존재 여부 확인
    public static void exists(StoreId id, StoreRepository repository) {
        if (!repository.existsById(id)) {
            throw new StoreNotFoundException();
        }
    }

    // 분류 추가
    public void addCategory(Category category, boolean active) {
        categories = toModifiableList(categories);
        categories.add(new StoreCategory(category, active));
    }

    // 분류 비우기
    public void truncateCategory() {
        categories = new ArrayList<>();
    }

    // 분류 삭제
    public void removeCategory(Category category) {
        removeCategory(List.of(category));
    }

    // 분류 복수 삭제
    public void removeCategory(List<Category> categories) {
        if (this.categories == null || categories == null || categories.isEmpty()) return;
        this.categories = toModifiableList(this.categories);
        this.categories = this.categories.stream()
                .filter(c -> !categories.contains(c.getCategory()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private <T> List<T> toModifiableList(List<T> items) {
        return items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    // 리뷰 갯수, 리뷰 평점 수정
    public void changeReview(int reviewCount, BigDecimal averageReviewScore) {
        this.reviewCount = reviewCount;
        this.averageReviewScore = averageReviewScore;
    }
}
