package com.culcon.backend.integrations;

import com.culcon.backend.JsonReader;
import com.culcon.backend.models.user.Account;
import com.culcon.backend.models.user.Role;
import com.culcon.backend.repositories.user.AccountRepo;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc()
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class AuthAPITest {
	@Value("${constant.json-data}")
	String pwd;

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	String jwtToken;
	JsonReader testJson;


	@Autowired
	private AccountRepo userRepository;

	@BeforeAll
	void setUp() throws Exception {

		var admin = Account.builder()
			.email("example@test")
			// ADMIN
			.password("$2a$10$n7NTAk2ymn6sYQEmwnqbI.mIqOBFSAWdXoZewi.PiPxQqnZiQq9zq")
			.role(Role.CUSTOMER)
			.phone("0969996669")
			.username("test_account")
			.build();

		userRepository.save(admin);

		this.testJson =
			new JsonReader(this.pwd + "AuthAPITest.json");
		var result = mockMvc.
			perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						  {
							"username" : "test_account",
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
	@BeforeAll
	@DisplayName("Smoke test Auth controller")
	void smokeTest() throws Exception {
		assertNotNull(mockMvc);
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_Success() throws Exception {
		var result = mockMvc.
			perform(
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
		var localToken = jsonResult
			.getString("accessToken");

		assertEquals(156, localToken.length());
	}

	@Test
	@Order(3)
	void AuthAPI_Login_Success() throws Exception {
		var result = mockMvc.
			perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
						testJson.getTestCase("login_success")
							.get("input")
							.toString()
					)
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
		var result = mockMvc.
			perform(
				get("/api/auth/account")
					.header("Authorization", jwtToken)
			).andExpect(
				status().isOk()
			).andReturn().getResponse().getContentAsString();

		var jsonResult = (ObjectNode) objectMapper.readTree(result);
		jsonResult.remove("id");

		assertEquals(
			testJson.getTestCase("get_account_info").get("output"),
			jsonResult);
	}

	@Test
	@Order(3)
	void AuthAPI_Login_Fail() throws Exception {
		var result = mockMvc.
			perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
						testJson.getTestCase("login_fail")
							.get("input")
							.toString()
					)
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("No account with such username", jsonResult.getString("messages"));
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_InvalidEmail() throws Exception {
		// Thực hiện API call
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_InvalidEmail").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));

		var errors = jsonResult.getJSONArray("errors");

		boolean emailErrorFound = false;
		for (int i = 0; i < errors.length(); i++) {
			var error = errors.getJSONObject(i);
			if (error.getString("fieldName").equals("email") &&
				error.getString("message").equals("must be a well-formed email address")) {
				emailErrorFound = true;
				break;
			}
		}

		assertTrue(emailErrorFound, "Expected error for field 'email' was not found.");
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_BlankUsername() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_blank_username").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));

		var errors = jsonResult.getJSONArray("errors");

		String actualErrorMessage = null;
		for (int i = 0; i < errors.length(); i++) {
			var error = errors.getJSONObject(i);
			if (error.getString("fieldName").equals("username")) {
				actualErrorMessage = error.getString("message");
				break;
			}
		}

		assertEquals("must not be blank", actualErrorMessage, "Expected error for field 'username' was not found.");

	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_BlankEmail() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_blank_email").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_BlankPhone() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_blank_phone").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_BlankPassword() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_blank_password").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_InvalidPhoneMissNumber() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_InvalidPhoneNumberMissNumber")
						.get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_InvalidPhoneMoreNumber() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_InvalidPhoneNumberMoreNumber").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(2)
	@Rollback(false)
	void AuthAPI_Register_InvalidPhoneHaveText() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_InvalidPhoneNumberHaveText").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(3)
	@Rollback(false)
	void AuthAPI_Login_BlankUserName() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("login_blankUserName").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(3)
	@Rollback(false)
	void AuthAPI_Login_BlankPassword() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/signin")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("login_blankPassword").get("input").toString())
			)
			.andExpect(
				status().isBadRequest()
			)
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}
}
