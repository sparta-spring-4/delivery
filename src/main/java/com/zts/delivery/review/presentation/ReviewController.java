package com.zts.delivery.review.presentation;

import com.zts.delivery.order.domain.OrderId;
import com.zts.delivery.review.application.service.ReviewService;
import com.zts.delivery.review.application.service.StoreReviewService;
import com.zts.delivery.review.application.service.dto.RegisterReview;
import com.zts.delivery.review.application.service.dto.ReviewInfo;
import com.zts.delivery.review.application.service.dto.StoreReviewInfo;
import com.zts.delivery.review.presentation.dto.RegisterReviewRequest;
import com.zts.delivery.review.presentation.dto.ReviewListResponse;
import com.zts.delivery.store.domain.StoreId;
import com.zts.delivery.user.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
@Tag(name = "리뷰 API", description = "리뷰 등록 / 조회 기능을 제공합니다.")
public class ReviewController {

    private final ReviewService reviewService;
    private final StoreReviewService storeReviewService;

    @Operation(summary = "리뷰 등록", description = "가게에 리뷰를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody RegisterReviewRequest req) {
        RegisterReview registerReview = RegisterReview.builder()
                .userId(userPrincipal.userId())
                .username(userPrincipal.username())
                .orderId(OrderId.of(req.orderId()))
                .storeId(StoreId.of(req.storeId()))
                .comment(req.comment())
                .score(req.score())
                .build();
        reviewService.register(registerReview);
    }

    @Operation(summary = "리뷰 조회", description = "가게의 리뷰를 조회합니다. 페이징 가능")
    @GetMapping("{storeId}")
    public ReviewListResponse findAllBy(@PathVariable UUID storeId, PageRequest pageRequest) {
        StoreId findStoreId = StoreId.of(storeId);
        StoreReviewInfo storeReviewInfo = storeReviewService.findBy(findStoreId);
        List<ReviewInfo> reviewInfos = reviewService.findAllBy(findStoreId, pageRequest);
        return ReviewListResponse.of(storeReviewInfo, reviewInfos);
    }
}
