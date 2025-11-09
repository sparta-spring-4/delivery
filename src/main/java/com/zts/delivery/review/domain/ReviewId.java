package com.zts.delivery.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@ToString
@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ReviewId implements Serializable {
    @Column(length = 45, name = "review_id")
    private UUID id;

    public static ReviewId of() {
        return ReviewId.of(UUID.randomUUID());
    }

    public static ReviewId of(UUID id) {
        return new ReviewId(id);
    }
}