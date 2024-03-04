package com.taskmanagementsystem.tms.contoroller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.taskmanagementsystem.tms.constant.Role;
import com.taskmanagementsystem.tms.models.UserInfo;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  private final Gson gson = new Gson();

  @Test
  void testRegisterUser() throws Exception {
    var userInfo = new UserInfo(null, "whatever@host.com", "123", Role.USER);

    mockMvc
        .perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userInfo)))
            .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username", Is.is("whatever@host.com")))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.role", Is.is("USER")));
  }

  @Test
  void testLogin() throws Exception {
    // Register
    var userInfo = new UserInfo(null, "uniqueusername@host.com", "123", Role.USER);

    mockMvc
            .perform(post("/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(gson.toJson(userInfo)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username", Is.is("uniqueusername@host.com")))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.role", Is.is("USER")));

    // Login
    var userInfoToLogin = new UserInfo(null, "loginuser@host.com", "123", null);

    mockMvc
            .perform(post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(gson.toJson(userInfoToLogin)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
  }
}
