package com.zts.delivery.infrastructure.exception.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestController.class)
public class ApplicationExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser("USER")
    @DisplayName("ApplicationException 은 code, message를 가진다.")
    void applicationException() throws Exception {
        mockMvc.perform(get("/test/app-exception"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    @WithMockUser("USER")
    @DisplayName("MethodArgumentNotValidException 는 code, message, details를 가진다.")
    void methodArgumentNotValidException() throws Exception {
        String request = """
                {
                    "name" : "",
                    "age" : -1,
                    "isAdult" : ""
                }
                """;
        mockMvc.perform(post("/test/field-exception")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .with(csrf()))
                .andExpect(status().isPreconditionFailed())
                .andExpect(jsonPath("$.code").value("REQUEST_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request value is not valid"))
                .andExpect(jsonPath("$.details[*].field", containsInAnyOrder("name", "age", "isAdult")))
                .andExpect(jsonPath("$.details[*].reason",
                        containsInAnyOrder("must not be empty", "must be greater than or equal to 1", "must not be null")));

    }

    @Test
    @WithMockUser("USER")
    @DisplayName("UncaughtException 은 code, message를 가진다.")
    void uncaughtException() throws Exception {
        mockMvc.perform(get("/test/uncaught-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Internal Server Error"))
                .andExpect(jsonPath("$.details").isEmpty());
    }
}
