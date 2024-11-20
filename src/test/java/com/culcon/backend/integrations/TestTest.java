package com.culcon.backend.integrations;

import com.culcon.backend.JsonReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc()
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class TestTest {
	@Value("${constant.json-data}")
	String pwd;

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	String jwtToken;
	JsonReader testJson;

	@BeforeAll
	void setUp() throws Exception {

		this.testJson =
			new JsonReader(this.pwd + "AuthAPITest.json");
		var result = mockMvc.
			perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
							"username" : "admin",
							"password" : "admin"
						}
						""")
			).andExpect(
				status().isOk()
			).andReturn().getResponse().getContentAsString();

		var jsonResult = new JSONObject(result);
		this.jwtToken = "Bearer " + jsonResult
			.getString("accessToken");
	}


	@Test
	@Order(1)
	@Rollback(false)
	void AuthAPI_Register_Success() throws Exception {
		var result = mockMvc.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
						testJson.getTestCase("register_success")
							.get("input")
							.toString()
					)
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);

		Assertions.assertEquals(156, jsonResult.getString("accessToken").length());
	}


	@Test
	@Order(2)
	void AuthAPI_Login_Success() throws Exception {
		var result = mockMvc.
			perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{
							"username" : "user01",
							"password" : "user01"
						}
						""")

			).andExpect(
				status().isOk()
			).andReturn().getResponse().getContentAsString();

		var jsonResult = new JSONObject(result);
		var localToken = jsonResult
			.getString("accessToken");


		assertEquals(156, localToken.length());
	}


	@Test
	void AuthAPI_GetAccountInfo() throws Exception {
		var token = mockMvc.perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
						testJson.getTestCase("login_success")
							.get("input")
							.toString()
					)
			).andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var tokenJson = new JSONObject(token);
		var jwtToken = "Bearer " + tokenJson.getString("accessToken");

		var result = mockMvc.perform(
				get("/api/auth/account")
					.header("Authorization", jwtToken)
			).andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();


		var jsonResult = (ObjectNode) objectMapper.readTree(result);

		jsonResult.remove("id");

//		assertEquals(
//			testJson.getTestCase("get_account_info").get("output"),
//			jsonResult);
	}
}
