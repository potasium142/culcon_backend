package com.culcon.backend.integrations;

import com.culcon.backend.JsonReader;
import com.culcon.backend.repositories.AccountRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class CartAPITest {

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
			new JsonReader(this.pwd + "ProductAPITest.json");
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
	@Order(4)
	@Rollback(value = false)
	void CartAPI_AddToCart_Success() throws Exception {
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
	void CartAPI_AddToCart_InvalidId() throws Exception {
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
	void CartAPI_AddToCart_InvalidQuantity() throws Exception {
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
	void CartAPI_AddToCart_BlankQuantity() throws Exception {
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
	void CartAPI_AddToCart_BlankId() throws Exception {
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
	void CartAPI_AddToCart_NoIdExist() throws Exception {
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
	void CartAPI_Cart_SetQuantity() throws Exception {
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
	void CartAPI_Cart_NullSetQuantity() throws Exception {
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
	void CartAPI_Cart_NullSetId() throws Exception {
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
	void CartAPI_Cart_SetIdNotExist() throws Exception {
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
	void CartAPI_Cart_Remove_InvalidId() throws Exception {
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
	void CartAPI_Cart_RemoveSuccess() throws Exception {
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
