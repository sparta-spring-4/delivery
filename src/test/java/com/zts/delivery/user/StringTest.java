package com.zts.delivery.user;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

public class StringTest {

    @Test
    void test() {
        if (StringUtils.hasText("      a         ")) {
            System.out.println("yes");
        } else {
            System.out.println("no");
        }
    }
}
