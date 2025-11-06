package com.zts.delivery.order;

import com.zts.delivery.order.application.CartService;
import com.zts.delivery.order.presentation.controller.CartController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
public class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    //    @MockitoBeans(
    //        CartService cartService;
    //    )
}
