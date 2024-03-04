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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

  @Autowired private MockMvc mockMvc;
  private final Gson gson = new Gson();

  @Test
  void testCreateTask() throws Exception {

    // Register
    var userInfo = new UserInfo(null, "loginuser@host.com", "123", Role.USER);

    mockMvc
        .perform(
            post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userInfo)));

    // Login
    var userInfoToLogin = new UserInfo(null, "loginuser@host.com", "123", null);

    var token =
        mockMvc
            .perform(
                post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(gson.toJson(userInfoToLogin)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andReturn()
            .getResponse()
            .getContentAsString();

    String taskToCreate =
        """
{
  "title": "test",
  "description": "test",
  "status": "TODO",
  "priority": 3,
  "dueDate": "2024-03-25"
}
""";

    mockMvc
        .perform(
            post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(taskToCreate))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.title", Is.is("test")))
        .andExpect(jsonPath("$.description", Is.is("test")))
        .andExpect(jsonPath("$.status", Is.is("TODO")))
        .andExpect(jsonPath("$.priority", Is.is(3)))
        .andExpect(jsonPath("$.dueDate", Is.is("2024-03-25")));
  }

}
