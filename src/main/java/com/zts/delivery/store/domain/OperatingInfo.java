package com.zts.delivery.store.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@ToString
@Getter
@Embeddable
@Access(AccessType.FIELD)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingInfo {
    private LocalTime startHour;
    private LocalTime endHour;
    private List<DayOfWeek> weekdays;
}
