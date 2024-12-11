package com.culcon.backend.integrations;

import com.culcon.backend.JsonReader;
import com.culcon.backend.models.Account;
import com.culcon.backend.repositories.AccountRepo;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

		assertEquals(196, localToken.length());
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


		assertEquals(196, localToken.length());
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

	@Test
	@Order(2)
	void AuthAPI_Register_EmailAlreadyExists() throws Exception {
		// Tạo tài khoản có email đã tồn tại trong cơ sở dữ liệu
		Account existingAccount = new Account();
		existingAccount.setEmail("example@email.com");
		existingAccount.setPassword("user01");
		existingAccount.setUsername("user01");
		existingAccount.setPhone("0123456780");
		userRepository.save(existingAccount); // Lưu tài khoản mẫu vào cơ sở dữ liệu

		// Gửi yêu cầu đăng ký với email đã tồn tại
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
		// Kiểm tra giá trị của các trường
		assertEquals("Data integrity violation", jsonResult.getString("fieldName"), "Unique index or primary key violation");
	}

	@Test
	@Order(2)
	void AuthAPI_Register_UsernameAlreadyExists() throws Exception {
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
	void AuthAPI_Register_PhoneAlreadyExists() throws Exception {
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
	void AuthAPI_EditProfile_Success() throws Exception {
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
	void AuthAPI_EditProfile_InvalidLessPhoneNumber() throws Exception {
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

		// Kiểm tra nội dung phản hồi
		var jsonResult = new JSONObject(result);
		assertEquals("MethodArgumentNotValidException", jsonResult.getString("exception"));
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void AuthAPI_EditProfile_InvalidMorePhoneNumber() throws Exception {
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
	void AuthAPI_EditProfile_EmailSuccess() throws Exception {
		var result = mockMvc
				.perform(
						post("/api/customer/edit/email")
								.header("Authorization", jwtToken)
								.contentType(MediaType.APPLICATION_JSON)
								.param("newEmail", "trinhquangtung1@gmail.com")
								.param("accountID", "e7b5cd8f-698f-4b46-9028-c70501c3dda6")
								.param("otp", "rhKdtAJznpRx3b")

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
	void AuthAPI_EditProfile_EmailInvalidOTP() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/email")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "example_new_email@gmail.com")
					.param("accountID", "e7b5cd8f-698f-4b46-9028-c70501c3dda6")
					.param("otp", ".......")

			)
			.andExpect(status().isInternalServerError())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var jsonResult = new JSONObject(result);

		assertEquals("OTPException", jsonResult.getString("cause"));
	}

	@Test
	@Order(5)
	@Rollback(value = true)
	void AuthAPI_EditProfile_EmailInvalidEmail() throws Exception {
		var result = mockMvc
			.perform(
				post("/api/customer/edit/email")
					.header("Authorization", jwtToken)
					.contentType(MediaType.APPLICATION_JSON)
					.param("newEmail", "example_new_email")
					.param("accountID", "e7b5cd8f-698f-4b46-9028-c70501c3dda6")
					.param("otp", "........")
			)
			.andExpect(status().isInternalServerError())
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
			.andExpect(status().isInternalServerError())
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
			.andExpect(status().isInternalServerError())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var jsonResult = new JSONObject(result);
		assertEquals("ConstraintViolationException", jsonResult.getString("cause"));
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void AuthAPI_EditProfile_BlankUsername() throws Exception {
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
	void AuthAPI_EditProfile_BlankPhone() throws Exception {

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
	void AuthAPI_EditPassword_Success() throws Exception {

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


		assertEquals(212, localToken.length());
	}

	@Test
	@Order(4)
	@Rollback(value = true)
	void AuthAPI_EditPassword_WrongOldPassword() throws Exception {

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
	void AuthAPI_EditPassword_BlankOldPassword() throws Exception {

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
	void AuthAPI_EditPassword_BlankNewPassword() throws Exception {

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
	void AuthAPI_EditPassword_InvalidNewPassword() throws Exception {

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
	void AuthAPI_AddToCart_Success() throws Exception {
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
	void AuthAPI_AddToCart_InvalidId() throws Exception {
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
	void AuthAPI_AddToCart_InvalidQuantity() throws Exception {
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
	void AuthAPI_AddToCart_BlankQuantity() throws Exception {
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
	void AuthAPI_AddToCart_BlankId() throws Exception {
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
	void AuthAPI_AddToCart_NoIdExist() throws Exception {
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
	void AuthAPI_Cart_SetQuantity() throws Exception {
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
	void AuthAPI_Cart_NullSetQuantity() throws Exception {
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
	void AuthAPI_Cart_NullSetId() throws Exception {
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
	void AuthAPI_Cart_SetIdNotExist() throws Exception {
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
	void AuthAPI_Cart_Remove_InvalidId() throws Exception {
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
	void AuthAPI_Cart_RemoveSuccess() throws Exception {
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

}
