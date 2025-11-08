package com.zts.delivery.order.application;

import com.zts.delivery.infrastructure.execption.ApplicationException;
import com.zts.delivery.order.domain.OrderId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final OrderService orderService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Async("simulationExecutor")
    public void simulateDelivery(OrderId orderId) {

        try {
            Thread.sleep(3000);

            orderService.updateToDelivering(orderId);
            log.info("[Order {}] 🛵 배달 시작: {} (Status: DELIVERING)",
                orderId, LocalDateTime.now().format(formatter));

            Thread.sleep(3000);

            orderService.updateToDelivered(orderId);
            log.info("[Order {}] ✅ 배달 완료: {} (Status: DELIVERED)",
                orderId, LocalDateTime.now().format(formatter));

        } catch (InterruptedException e) {
            log.error("[Order {}] 시뮬레이션 스레드가 중단되었습니다.", orderId, e);
            Thread.currentThread().interrupt();
        } catch (ApplicationException e) {
            log.error("[[Order {}] 시뮬레이션 중 오류 발생: {}", orderId, e.getMessage());
        }
    }
}
