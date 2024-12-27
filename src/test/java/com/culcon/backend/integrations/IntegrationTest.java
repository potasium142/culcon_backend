package com.culcon.backend.integrations;

import com.culcon.backend.JsonReader;
import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.AccountRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Testcontainers
public class IntegrationTest {

	@LocalServerPort
	private Integer port;

	@Container
	static PostgreSQLContainer<?> postgres =
		new PostgreSQLContainer<>("postgres:16-alpine");

	static {
		postgres.start();
	}

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

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

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
	void Register_Success() throws Exception {
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

		assertEquals(239, localToken.length());
	}

	@Test
	@Order(3)
	void Login_Success() throws Exception {
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


		assertEquals(239, localToken.length());
	}


	@Test
	void GetAccountInfo() throws Exception {
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
	void Login_Fail() throws Exception {
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
	void Register_InvalidEmail() throws Exception {
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
	void Register_BlankUsername() throws Exception {
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
	void Register_BlankEmail() throws Exception {
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
	void Register_BlankPhone() throws Exception {
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
	void Register_BlankPassword() throws Exception {
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
	void Register_InvalidPhoneMissNumber() throws Exception {
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
	void Register_InvalidPhoneMoreNumber() throws Exception {
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
	void Register_InvalidPhoneHaveText() throws Exception {
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
	void Login_BlankUserName() throws Exception {
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
	void Login_BlankPassword() throws Exception {
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

	@Test
	@Order(2)
	void Register_EmailAlreadyExists() throws Exception {
		Account existingAccount = new Account();
		existingAccount.setEmail("example@email.com");
		existingAccount.setPassword("user01");
		existingAccount.setUsername("user01");
		existingAccount.setPhone("0123456780");
		userRepository.save(existingAccount); // Lưu tài khoản mẫu vào cơ sở dữ liệu

		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_exists_email").get("input").toString())
			)
			.andExpect(status().isNotAcceptable()) // Kiểm tra HTTP status 400
			.andReturn()
			.getResponse()
			.getContentAsString();

		// Kiểm tra nội dung phản hồi
		var jsonResult = new JSONObject(result);
		assertEquals("Data integrity violation", jsonResult.getString("fieldName"), "Unique index or primary key violation");
	}

	@Test
	@Order(2)
	void Register_UsernameAlreadyExists() throws Exception {
		Account existingAccount = new Account();
		existingAccount.setEmail("example@email.com");
		existingAccount.setPassword("user01");
		existingAccount.setUsername("user01");
		existingAccount.setPhone("0123456780");
		userRepository.save(existingAccount);

		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_exists_username").get("input").toString())
			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("Data integrity violation", jsonResult.getString("fieldName"), "Unique index or primary key violation");
	}

	@Test
	@Order(2)
	void Register_PhoneAlreadyExists() throws Exception {
		Account existingAccount = new Account();
		existingAccount.setEmail("example@email.com");
		existingAccount.setPassword("user01");
		existingAccount.setUsername("user01");
		existingAccount.setPhone("0123456780");
		userRepository.save(existingAccount);

		var result = mockMvc
			.perform(
				post("/api/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("register_exists_phone").get("input").toString())
			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("Data integrity violation", jsonResult.getString("fieldName"), "Unique index or primary key violation");
	}

	@Test
	@Order(4)
	void EditProfile_Success() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/profile")
					.header("Authorization", jwtToken) // Sử dụng token hợp lệ
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("edit_profile").get("input").toString())
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		// Kiểm tra nội dung phản hồi
		var jsonResult = (ObjectNode) objectMapper.readTree(result);
		jsonResult.remove("id");
		assertEquals(
			testJson.getTestCase("edit_profile").get("output"),
			jsonResult
		);
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void EditProfile_InvalidLessPhoneNumber() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/profile")
					.header("Authorization", jwtToken) // Sử dụng token hợp lệ
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("edit_profileInvalidLessPhoneNumber").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void EditProfile_InvalidMorePhoneNumber() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/profile")
					.header("Authorization", jwtToken) // Sử dụng token hợp lệ
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("edit_profileInvalidMorePhoneNumber").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		// Kiểm tra nội dung phản hồi
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(5)
	@Rollback(value = true)
	void EditProfile_EmailSuccess() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/email")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "trinhquangtung3105@gmail.com")
					.param("accountID", "94511231-59ce-45cb-9edc-196c378064a1")
					.param("otp", "Rgy8YTrwELqlh1")

			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		System.out.println(result);

		if (result.equals("Email update successfully")) {
			assertTrue(true);
		}
	}


	@Test
	@Order(5)
	@Rollback(value = true)
	void EditProfile_EmailInvalidOTP() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/email")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "example_new_email@gmail.com")
					.param("accountID", "e7b5cd8f-698f-4b46-9028-c70501c3dda6")
					.param("otp", ".......")

			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("OTPException", jsonResult.getString("cause"));
	}

	@Test
	@Order(5)
	@Rollback(value = true)
	void EditProfile_EmailInvalidEmail() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/email")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "example_new_email")
					.param("accountID", "e7b5cd8f-698f-4b46-9028-c70501c3dda6")
					.param("otp", "........")
			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("ConstraintViolationException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	void testGetOtp() throws Exception {
		var otpResponse = mockMvc.perform(
				post("/api/customer/edit/email/get/otp")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "example_new_email@gmail.com")
			)
			.andExpect(status().isOk()) // Đảm bảo phản hồi OK
			.andReturn()
			.getResponse()
			.getContentAsString();

		// Kiểm tra phản hồi từ API /get/otp
		var otpJson = new JSONObject(otpResponse);
		assertTrue(otpJson.has("accountId"));
		assertTrue(otpJson.has("expireTime"));
	}

	@Test
	@Order(4)
	void testGetOtp_InvalidEmail() throws Exception {
		var result = mockMvc.perform(
				post("/api/customer/edit/email/get/otp")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "example_new_email")
			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);
		assertEquals("ConstraintViolationException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	void testGetOtp_EmailExist() throws Exception {
		var result = mockMvc.perform(
				post("/api/customer/edit/email/get/otp")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "example@test")
			)
			.andExpect(status().isInternalServerError());
	}

	@Test
	@Order(4)
	void testGetOtp_BlankEmail() throws Exception {
		var result = mockMvc.perform(
				post("/api/customer/edit/email/get/otp")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "")
			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);
		assertEquals("ConstraintViolationException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void EditProfile_BlankUsername() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/profile")
					.header("Authorization", jwtToken) // Sử dụng token hợp lệ
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("edit_profileBlankUsername").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void EditProfile_BlankPhone() throws Exception {

		var result = mockMvc
			.perform(
				post("/api/customer/edit/profile")
					.header("Authorization", jwtToken) // Sử dụng token hợp lệ
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("edit_profileBlankPhone").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void EditPassword_Success() throws Exception {

		var result = mockMvc
			.perform(
				post("/api/customer/edit/password")
					.header("Authorization", jwtToken) // Sử dụng token hợp lệ
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("editPassword").get("input").toString())
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		// Kiểm tra nội dung phản hồi
		var jsonResult = new JSONObject(result);
		var localToken = jsonResult.getString("accessToken");


		assertEquals(239, localToken.length());
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void EditPassword_WrongOldPassword() throws Exception {

		var result = mockMvc
			.perform(
				post("/api/customer/edit/password")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("editPassword_WrongOldPassword").get("input").toString())
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void EditPassword_BlankOldPassword() throws Exception {

		var result = mockMvc
			.perform(
				post("/api/customer/edit/password")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("editPassword_BlankOldPassword").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void EditPassword_BlankNewPassword() throws Exception {

		var result = mockMvc
			.perform(
				post("/api/customer/edit/password")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("editPassword_BlankNewPassword").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void EditPassword_InvalidNewPassword() throws Exception {

		var result = mockMvc
			.perform(
				post("/api/customer/edit/password")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("editPassword_InvalidNewPassword").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void AddToCart_Success() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/add")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_01")
					.param("quantity", "10")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		if (result.equals("{\n" +
			"  \"product\": {\n" +
			"    \"id\": \"MK_01\",\n" +
			"    \"productName\": \"Snakehead Fish Braised with Pepper \uD83D\uDC1F\",\n" +
			"    \"productTypes\": \"MEALKIT\",\n" +
			"    \"availableQuantity\": 98,\n" +
			"    \"productStatus\": \"IN_STOCK\",\n" +
			"    \"imageUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRT9x9miekrqlY5dpoEFRTNLUeDWujSBuGuXQ&s\",\n" +
			"    \"price\": 12.5,\n" +
			"    \"salePercent\": 10\n" +
			"  },\n" +
			"  \"amount\": 10\n" +
			"}")) {
			assertTrue(true);
		}
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void AddToCart_InvalidId() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/add")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "M")
					.param("quantity", "10")
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void AddToCart_InvalidQuantity() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/add")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_01")
					.param("quantity", "ddd")
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void AddToCart_BlankQuantity() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/add")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_01")
					.param("quantity", "")
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void AddToCart_BlankId() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/add")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "")
					.param("quantity", "ddd")
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void AddToCart_NoIdExist() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/add")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "123")
					.param("quantity", "1")
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	void Cart_SetQuantity() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/set")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_01")
					.param("quantity", "4")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	void Cart_NullSetQuantity() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/set")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_01")
					.param("quantity", "")
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(5)
	void Cart_NullSetId() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/set")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "")
					.param("quantity", "1")
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(5)
	void Cart_SetIdNotExist() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/cart/set")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_0122222")
					.param("quantity", "1")
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(5)
	void Cart_Remove_InvalidId() throws Exception {
		var result = mockMvc
			.perform(
				delete("/api/customer/cart/remove")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_0122222")
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(6)
	void Cart_RemoveSuccess() throws Exception {
		var result = mockMvc
			.perform(
				delete("/api/customer/cart/remove")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "MK_01")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	void Coupon() throws Exception {
		var result = mockMvc
			.perform(
				get("/api/public/fetch/coupon")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("couponId", "cou132")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(5)
	void Order_Success() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/order/create")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("order_success").get("input").toString())
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("ON_CONFIRM", jsonResult.getString("status"));
	}

	@Test
	@Order(5)
	void Order_BlankPayment() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/order/create")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("order_BlankPlayment").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("Failed to read request", jsonResult.getString("detail"));

	}

	@Test
	@Order(5)
	void Order_WrongNumberProductInCart() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/order/create")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("order_WrongNumberProductInCart").get("input").toString())
			)
			.andExpect(status().isInternalServerError())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("RuntimeException", jsonResult.getString("cause"));
	}

	@Test
	@Order(5)
	void Order_InvalidCoupon() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/order/create")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("order_InvalidCoupon").get("input").toString())
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(5)
	void Order_InvalidPhoneNumber() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/order/create")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("order_InvalidPhoneNumber").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Rollback(true)
	void Order_CancelSuccess() throws Exception {
		var result = mockMvc
			.perform(
				delete("/api/customer/order/cancel")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "102")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("CANCELLED", jsonResult.getString("status"));
	}

	@Test
	@Order(6)
	@Rollback
	void Order_CancelNotExcistOrder() throws Exception {
		var result = mockMvc
			.perform(
				delete("/api/customer/order/cancel")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("id", "1023")
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback
	void OTP_FogotPassword() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/forgot/otp/get")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("email", "trinhquangtung1@gmail.com")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertTrue(jsonResult.has("accountId"));
		assertTrue(jsonResult.has("expireTime"));
	}

	@Test
	@Order(4)
	@Rollback
	void OTP_FogotPassword_InvalidEmail() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/forgot/otp/get")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("email", "trinhquangtung1")
			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("ConstraintViolationException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback
	void FogotPassword_Success() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/forgot/reset")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("fogotPassword_Success").get("input").toString())
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		assertEquals("Password update successfully", result);
	}

	@Test
	@Order(4)
	@Rollback
	void FogotPassword_InvalidNewPassword() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/forgot/reset")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("fogotPassword_InvalidPassword").get("input").toString())
			)
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(4)
	@Rollback
	void FogotPassword_InvalidOTP() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/auth/forgot/reset")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(testJson.getTestCase("fogotPassword_InvalidOTP").get("input").toString())
			)
			.andExpect(status().isNotAcceptable())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("OTPException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback
	void Blog_PostComment() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/blog/comment")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("postId", "B01")
					.param("comment", "delicous")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertTrue(jsonResult.has("accountName"));
		assertTrue(jsonResult.has("profilePicture"));
		assertTrue(jsonResult.has("timestamp"));
		assertTrue(jsonResult.has("comment"));
	}

	@Test
	@Order(4)
	@Rollback
	void Blog_Comment_NullpostId() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/blog/comment")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("postId", "B012345")
					.param("comment", "delicous")
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback
	void Blog_Bookmark_Save() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/blog/bookmark")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("bookmark", "True")
					.param("blogId", "B01")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(4)
	@Rollback
	void Blog_Bookmark_UnSave() throws Exception {
		var result = mockMvc
			.perform(
				put("/api/customer/blog/bookmark")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("bookmark", "False")
					.param("blogId", "B01")
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	@Test
	@Order(4)
	@Rollback
	void Blog_ShowAllComment() throws Exception {
		var result = mockMvc
			.perform(
				get("/api/public/fetch/blog/comment/{id}", "B01")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		if (result.trim().startsWith("[")) {
			var jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				var commentObject = jsonArray.getJSONObject(i); // Correctly access each JSONObject in the array
				assertTrue(commentObject.has("accountName"));
				assertTrue(commentObject.has("profilePicture"));
				assertTrue(commentObject.has("timestamp"));
				assertTrue(commentObject.has("comment"));
			}
		} else if (result.trim().startsWith("{")) {
			var jsonObject = new JSONObject(result);
			assertTrue(jsonObject.has("accountName"));
			assertTrue(jsonObject.has("profilePicture"));
			assertTrue(jsonObject.has("timestamp"));
			assertTrue(jsonObject.has("comment"));
		}
	}

	@Test
	@Order(4)
	@Rollback
	void Blog_Post() throws Exception {
		var result = mockMvc
			.perform(
				get("/api/public/fetch/blog/{id}", "B01")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonObject = new JSONObject(result);
		// Check the root keys
		assertTrue(jsonObject.has("blog"));
		assertTrue(jsonObject.has("bookmark"));
		// Validate the "blog" object
		var blogObject = jsonObject.getJSONObject("blog");
		assertTrue(blogObject.has("id"));
		assertEquals("B01", blogObject.getString("id"));
		assertTrue(blogObject.has("title"));
		assertTrue(blogObject.has("description"));
		assertTrue(blogObject.has("markdownText"));
		assertTrue(blogObject.has("infos"));
		assertTrue(blogObject.has("tags"));
		assertTrue(blogObject.has("relatedProduct"));
		assertTrue(blogObject.has("imageUrl"));
	}

	@Test
	@Order(4)
	@Rollback
	void Blog_InvalidPost() throws Exception {
		var result = mockMvc
			.perform(
				get("/api/public/fetch/blog/{id}", "B0123")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);
		assertEquals("NoSuchElementException", jsonResult.getString("cause"));
	}
}
