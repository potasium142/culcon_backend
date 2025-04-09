package com.culcon.backend.unit;

import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepoTest {

	@Mock
	private AccountRepo accountRepo;
	@Mock
	private AccountOTPRepo accountOTPRepo;
	@Mock
	private OrderHistoryRepo orderHistoryRepo;
	@Mock
	private ProductPriceRepo productPriceRepo;
	@Mock
	private ProductPriceHistory productPriceHistory;
	@Mock
	private ProductPriceHistoryId productPriceHistoryId;
	@Mock
	private ProductRepo productRepo;
	@Mock
	PostCommentRepo postCommentRepo;
	private Product product;
	private Product product1;
	private Product product2;
	private Product product3;
	private Account account;
	private AccountOTP accountOTP;
	private OrderHistory orderHistory;
	private OrderHistory orderHistoryOnConfirm;
	private OrderHistory orderHistoryShip;
	private OrderHistory orderHistoryReceived;
	private List<OrderHistory> orderHistories;
	private PostComment postComment1;
	private PostComment postComment2;
	private PostComment replyComment;
	private PostComment replyComment1;
	private PaymentTransaction paymentTransaction;
	private PaymentTransactionRepo paymentTransactionRepo;

	@BeforeEach
	void setUp() {
		account = Account.builder()
			.id("94511231-59ce-45cb-9edc-196c378064a1")
			.username("testUser")
			.email("test@example.com")
			.token("Rgy8YTrwELqlh1")
			.password("123456")
			.address("testAddress")
			.phone("0123456789")
			.build();

		accountOTP = AccountOTP.builder()
			.otp("Rgy8YTrwELqlh1")
			.account(account)
			.accountId(account.getId())
			.build();
		orderHistory = OrderHistory.builder()
			.id("1L")
			.user(account)
			.orderStatus(OrderStatus.ON_PROCESSING)
			.build();
		orderHistoryOnConfirm = OrderHistory.builder()
			.id("1L")
			.user(account)
			.orderStatus(OrderStatus.ON_CONFIRM)
			.build();
		orderHistoryShip = OrderHistory.builder()
			.id("1L")
			.user(account)
			.orderStatus(OrderStatus.ON_SHIPPING)
			.build();


		orderHistories = List.of(orderHistory);
		product1 = Product.builder()
			.id("P001")
			.productName("Pork Ribs")
			.productStatus(ProductStatus.IN_STOCK)
			.productTypes(ProductType.MEAT)
			.build();

		product2 = Product.builder()
			.id("P002")
			.productName("Ketchup")
			.productStatus(ProductStatus.IN_STOCK)
			.productTypes(ProductType.SEASON)
			.build();
		product3 = Product.builder()
			.id("P003")
			.productName("Pangasius Fish")
			.productStatus(ProductStatus.IN_STOCK)
			.productTypes(ProductType.MEAT)
			.build();
		productPriceHistory = ProductPriceHistory.builder()
			.price(100.0F)
			.salePercent(10.0F)
			.build();
		postComment1 = PostComment.builder()
			.id("testBlog1")
			.commentType(CommentType.POST)
			.postId("001")
			.comment("This is the first comment")
			.accountId("testUser")
			.build();
		postComment2 = PostComment.builder()
			.id("testBlog2")
			.commentType(CommentType.POST)
			.postId("002")
			.comment("This is the second comment")
			.accountId("testaccount2")
			.build();
		replyComment = PostComment.builder()
			.id("testBlog1reply")
			.commentType(CommentType.REPLY)
			.postId("001")
			.comment("This is the first reply comment")
			.accountId("testaccount2")
			.parentComment(postComment1)
			.build();
		replyComment1 = PostComment.builder()
			.id("testBlog1reply1")
			.commentType(CommentType.REPLY)
			.postId("001")
			.comment("This is the second reply comment")
			.accountId("testaccount3")
			.parentComment(postComment1)
			.build();
		paymentTransaction = PaymentTransaction.builder()
			.id(orderHistory.getId())
			.order(orderHistory)
			.status(PaymentStatus.PENDING)
			.paymentId("pay_123")
			.refundId("ref_123")
			.transactionId("txn_123")
			.amount(100.0F)
			.url("http://example.com/payment")
			.createTime(Timestamp.valueOf(LocalDateTime.now()))
			.build();


	}

	@Test
	void testPaymentTransactionInitialization() {
		Assertions.assertNotNull(paymentTransaction, "PaymentTransaction không được null");
		Assertions.assertEquals(orderHistory.getId(), paymentTransaction.getId(), "ID không khớp");
		Assertions.assertEquals(PaymentStatus.PENDING, paymentTransaction.getStatus(), "Trạng thái thanh toán không đúng");
		Assertions.assertEquals(100.0F, paymentTransaction.getAmount(), "Số tiền không đúng");
		Assertions.assertTrue(paymentTransaction.getUrl().startsWith("http://"), "URL phải bắt đầu bằng 'http://'");
	}


	@Test
	void AccountRepo_FindByUsername_Success() {
		when(accountRepo.findByUsername("testUser")).thenReturn(Optional.of(account));

		Optional<Account> foundAccount = accountRepo.findByUsername("testUser");

		assertThat(foundAccount).isPresent();
		assertThat(foundAccount.get().getUsername()).isEqualTo("testUser");

		verify(accountRepo, times(1)).findByUsername("testUser");
	}

	@Test
	void AccountRepo_FindByUsername_NotFound() {
		when(accountRepo.findByUsername("unknownUser")).thenReturn(Optional.empty());

		Optional<Account> foundAccount = accountRepo.findByUsername("unknownUser");

		assertThat(foundAccount).isEmpty();
		verify(accountRepo, times(1)).findByUsername("unknownUser");
	}

	@Test
	void AccountRepo_FindByUsername_DatabaseError() {
		when(accountRepo.findByUsername("testUser"))
			.thenThrow(new RuntimeException("Database error"));

		assertThatThrownBy(() -> accountRepo.findByUsername("testUser"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Database error");

		verify(accountRepo, times(1)).findByUsername("testUser");
	}

	@Test
	void AccountRepo_FindByUsername_NullInput() {
		when(accountRepo.findByUsername(null)).thenThrow(new IllegalArgumentException("Username must not be null"));

		assertThatThrownBy(() -> accountRepo.findByUsername(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Username must not be null");

		verify(accountRepo, times(1)).findByUsername(null);
	}

	@Test
	void AccountRepo_FindByUsername_DuplicateUsernames() {
		Account account1 = new Account();
		account1.setUsername("duplicateUser");

		Account account2 = new Account();
		account2.setUsername("duplicateUser");

		when(accountRepo.findByUsername("duplicateUser"))
			.thenReturn(Optional.of(account1));

		Optional<Account> foundAccount = accountRepo.findByUsername("duplicateUser");

		assertThat(foundAccount).isPresent();
		assertThat(foundAccount.get().getUsername()).isEqualTo("duplicateUser");

		verify(accountRepo, times(1)).findByUsername("duplicateUser");
	}

//	@Test
//	void AccountRepo_FindAccountById() {
//		when(accountRepo.findAccountById("94511231-59ce-45cb-9edc-196c378064a1")).thenReturn(Optional.of(account));
//
//		Optional<Account> foundAccount = accountRepo.findAccountById("94511231-59ce-45cb-9edc-196c378064a1");
//
//		assertThat(foundAccount).isPresent();
//		assertThat(foundAccount.get().getId()).isEqualTo("94511231-59ce-45cb-9edc-196c378064a1");
//
//		verify(accountRepo, times(1)).findAccountById("94511231-59ce-45cb-9edc-196c378064a1");
//	}

	@Test
	void AccountRepo_FindAccountByEmail_Success() {
		when(accountRepo.findAccountByEmail("test@example.com")).thenReturn(Optional.of(account));

		Optional<Account> foundAccount = accountRepo.findAccountByEmail("test@example.com");

		assertThat(foundAccount).isPresent();
		assertThat(foundAccount.get().getEmail()).isEqualTo("test@example.com");

		verify(accountRepo, times(1)).findAccountByEmail("test@example.com");
	}

	@Test
	void AccountRepo_FindAccountByEmail_NotFound() {
		when(accountRepo.findAccountByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

		Optional<Account> foundAccount = accountRepo.findAccountByEmail("nonexistent@example.com");

		assertThat(foundAccount).isEmpty();

		verify(accountRepo, times(1)).findAccountByEmail("nonexistent@example.com");
	}

	@Test
	void AccountRepo_FindAccountByEmail_DatabaseError() {
		when(accountRepo.findAccountByEmail("test@example.com"))
			.thenThrow(new RuntimeException("Database connection error"));

		assertThatThrownBy(() -> accountRepo.findAccountByEmail("test@example.com"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Database connection error");

		verify(accountRepo, times(1)).findAccountByEmail("test@example.com");
	}

	@Test
	void AccountRepo_FindAccountByEmail_NullInput() {
		when(accountRepo.findAccountByEmail(null)).thenThrow(new IllegalArgumentException("Email must not be null"));

		assertThatThrownBy(() -> accountRepo.findAccountByEmail(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Email must not be null");

		verify(accountRepo, times(1)).findAccountByEmail(null);
	}

	@Test
	void AccountRepo_FindAccountByEmail_DuplicateEmails() {
		Account account1 = new Account();
		account1.setEmail("duplicate@example.com");

		Account account2 = new Account();
		account2.setEmail("duplicate@example.com");

		when(accountRepo.findAccountByEmail("duplicate@example.com"))
			.thenReturn(Optional.of(account1));

		Optional<Account> foundAccount = accountRepo.findAccountByEmail("duplicate@example.com");

		assertThat(foundAccount).isPresent();
		assertThat(foundAccount.get().getEmail()).isEqualTo("duplicate@example.com");

		verify(accountRepo, times(1)).findAccountByEmail("duplicate@example.com");
	}


	@Test
	void AccountRepo_FindByToken() {
		when(accountRepo.findByToken("Rgy8YTrwELqlh1")).thenReturn(Optional.of(account));

		Optional<Account> foundAccount = accountRepo.findByToken("Rgy8YTrwELqlh1");

		assertThat(foundAccount).isPresent();
		assertThat(foundAccount.get().getToken()).isEqualTo("Rgy8YTrwELqlh1");

		verify(accountRepo, times(1)).findByToken("Rgy8YTrwELqlh1");
	}

	@Test
	void AccountRepo_FindByToken_NotFound() {
		when(accountRepo.findByToken("InvalidToken")).thenReturn(Optional.empty());

		Optional<Account> foundAccount = accountRepo.findByToken("InvalidToken");

		assertThat(foundAccount).isEmpty();
		verify(accountRepo, times(1)).findByToken("InvalidToken");
	}

	@Test
	void AccountRepo_FindByToken_DatabaseError() {
		when(accountRepo.findByToken("Rgy8YTrwELqlh1"))
			.thenThrow(new RuntimeException("Database connection error"));

		assertThatThrownBy(() -> accountRepo.findByToken("Rgy8YTrwELqlh1"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Database connection error");

		verify(accountRepo, times(1)).findByToken("Rgy8YTrwELqlh1");
	}

	@Test
	void AccountRepo_FindByToken_NullInput() {
		when(accountRepo.findByToken(null)).thenThrow(new IllegalArgumentException("Token must not be null"));

		assertThatThrownBy(() -> accountRepo.findByToken(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Token must not be null");

		verify(accountRepo, times(1)).findByToken(null);
	}

//	@Test
//	void AccountRepo_ExistsByUsername() {
//		when(accountRepo.existsByUsername("testUser")).thenReturn(true);
//
//		Boolean exists = accountRepo.existsByUsername("testUser");
//
//		assertThat(exists).isTrue();
//
//		verify(accountRepo, times(1)).existsByUsername("testUser");
//	}
//
//	@Test
//	void AccountRepo_ExistsByUsername_NotFound() {
//		when(accountRepo.existsByUsername("nonExistingUser")).thenReturn(false);
//
//		boolean exists = accountRepo.existsByUsername("nonExistingUser");
//
//		assertThat(exists).isFalse();
//		verify(accountRepo, times(1)).existsByUsername("nonExistingUser");
//	}
//
//	@Test
//	void AccountRepo_ExistsByUsername_DatabaseError() {
//		when(accountRepo.existsByUsername("testUser"))
//			.thenThrow(new RuntimeException("Database connection error"));
//
//		assertThatThrownBy(() -> accountRepo.existsByUsername("testUser"))
//			.isInstanceOf(RuntimeException.class)
//			.hasMessageContaining("Database connection error");
//
//		verify(accountRepo, times(1)).existsByUsername("testUser");
//	}
//
//	@Test
//	void AccountRepo_ExistsByUsername_NullInput() {
//		when(accountRepo.existsByUsername(null)).thenThrow(new IllegalArgumentException("Username must not be null"));
//
//		assertThatThrownBy(() -> accountRepo.existsByUsername(null))
//			.isInstanceOf(IllegalArgumentException.class)
//			.hasMessage("Username must not be null");
//
//		verify(accountRepo, times(1)).existsByUsername(null);
//	}

	@Test
	void AccountOTPRepo_findByOtpAndAccountId_Success() {
		when(accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1", "94511231-59ce-45cb-9edc-196c378064a1")).thenReturn(Optional.of(accountOTP));
		Optional<AccountOTP> foundAccountOTP = accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1", "94511231-59ce-45cb-9edc-196c378064a1");

		assertThat(foundAccountOTP).isPresent();
		assertThat(foundAccountOTP.get().getOtp()).isEqualTo("Rgy8YTrwELqlh1");
		assertThat(foundAccountOTP.get().getAccountId()).isEqualTo("94511231-59ce-45cb-9edc-196c378064a1");

		verify(accountOTPRepo, times(1)).findByOtpAndAccountId("Rgy8YTrwELqlh1", "94511231-59ce-45cb-9edc-196c378064a1");

	}

	@Test
	void AccountOTPRepo_findByOtpAndAccountId_NotFound() {
		when(accountOTPRepo.findByOtpAndAccountId("InvalidOTP", "94511231-59ce-45cb-9edc-196c378064a1"))
			.thenReturn(Optional.empty());

		Optional<AccountOTP> foundAccountOTP = accountOTPRepo.findByOtpAndAccountId("InvalidOTP", "94511231-59ce-45cb-9edc-196c378064a1");

		assertThat(foundAccountOTP).isEmpty();

		verify(accountOTPRepo, times(1)).findByOtpAndAccountId("InvalidOTP", "94511231-59ce-45cb-9edc-196c378064a1");
	}

	@Test
	void AccountOTPRepo_findByOtpAndAccountId_AccountNotFound() {
		when(accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1", "InvalidAccountId"))
			.thenReturn(Optional.empty());

		Optional<AccountOTP> foundAccountOTP = accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1", "InvalidAccountId");

		assertThat(foundAccountOTP).isEmpty();

		verify(accountOTPRepo, times(1)).findByOtpAndAccountId("Rgy8YTrwELqlh1", "InvalidAccountId");
	}

	@Test
	void AccountOTPRepo_findByOtpAndAccountId_DatabaseError() {
		when(accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1", "94511231-59ce-45cb-9edc-196c378064a1"))
			.thenThrow(new RuntimeException("Database connection error"));

		assertThatThrownBy(() -> accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1", "94511231-59ce-45cb-9edc-196c378064a1"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Database connection error");

		verify(accountOTPRepo, times(1)).findByOtpAndAccountId("Rgy8YTrwELqlh1", "94511231-59ce-45cb-9edc-196c378064a1");
	}

	@Test
	void AccountOTPRepo_findByOtpAndAccountId_NullInput() {
		when(accountOTPRepo.findByOtpAndAccountId(null, null))
			.thenThrow(new IllegalArgumentException("OTP and Account ID must not be null"));

		assertThatThrownBy(() -> accountOTPRepo.findByOtpAndAccountId(null, null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("OTP and Account ID must not be null");

		verify(accountOTPRepo, times(1)).findByOtpAndAccountId(null, null);
	}


	@Test
	void AccountOTPRepo_findByAccount_Success() {
		when(accountOTPRepo.findByAccount(account)).thenReturn(Optional.of(accountOTP));

		Optional<AccountOTP> foundOTP = accountOTPRepo.findByAccount(account);

		assertThat(foundOTP).isPresent();
		assertThat(foundOTP.get().getAccount()).isEqualTo(account);

		verify(accountOTPRepo, times(1)).findByAccount(account);
	}

	@Test
	void AccountOTPRepo_findByAccount_NotFound() {
		when(accountOTPRepo.findByAccount(account)).thenReturn(Optional.empty());

		Optional<AccountOTP> foundOTP = accountOTPRepo.findByAccount(account);

		assertThat(foundOTP).isEmpty();

		verify(accountOTPRepo, times(1)).findByAccount(account);
	}

	@Test
	void AccountOTPRepo_findByAccount_AccountNotExists() {
		Account unknownAccount = new Account(); // Tạo một account chưa có trong database

		when(accountOTPRepo.findByAccount(unknownAccount)).thenReturn(Optional.empty());

		Optional<AccountOTP> foundOTP = accountOTPRepo.findByAccount(unknownAccount);

		assertThat(foundOTP).isEmpty();

		verify(accountOTPRepo, times(1)).findByAccount(unknownAccount);
	}

	@Test
	void AccountOTPRepo_findByAccount_DatabaseError() {
		when(accountOTPRepo.findByAccount(account))
			.thenThrow(new RuntimeException("Database connection error"));

		assertThatThrownBy(() -> accountOTPRepo.findByAccount(account))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Database connection error");

		verify(accountOTPRepo, times(1)).findByAccount(account);
	}

	@Test
	void AccountOTPRepo_findByAccount_NullInput() {
		when(accountOTPRepo.findByAccount(null))
			.thenThrow(new IllegalArgumentException("Account must not be null"));

		assertThatThrownBy(() -> accountOTPRepo.findByAccount(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Account must not be null");

		verify(accountOTPRepo, times(1)).findByAccount(null);
	}

	@Test
	void orderHistoryRepo_FindByUser_Success() {
		when(orderHistoryRepo.findByUser(account)).thenReturn(orderHistories);

		List<OrderHistory> foundOrders = orderHistoryRepo.findByUser(account);

		assertThat(foundOrders).isNotEmpty().hasSize(1);
		assertThat(foundOrders.get(0).getUser()).isEqualTo(account);

		verify(orderHistoryRepo, times(1)).findByUser(account);
	}

	@Test
	void orderHistoryRepo_FindByUser_NotFound() {
		when(orderHistoryRepo.findByUser(account)).thenReturn(Collections.emptyList());

		List<OrderHistory> foundOrders = orderHistoryRepo.findByUser(account);

		assertThat(foundOrders).isEmpty();

		verify(orderHistoryRepo, times(1)).findByUser(account);
	}

	@Test
	void orderHistoryRepo_FindByUser_AccountNotExists() {
		Account unknownAccount = new Account();

		when(orderHistoryRepo.findByUser(unknownAccount)).thenReturn(Collections.emptyList());

		List<OrderHistory> foundOrders = orderHistoryRepo.findByUser(unknownAccount);

		assertThat(foundOrders).isEmpty();

		verify(orderHistoryRepo, times(1)).findByUser(unknownAccount);
	}

	@Test
	void orderHistoryRepo_FindByUser_DatabaseError() {
		when(orderHistoryRepo.findByUser(account))
			.thenThrow(new RuntimeException("Database connection error"));

		assertThatThrownBy(() -> orderHistoryRepo.findByUser(account))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Database connection error");

		verify(orderHistoryRepo, times(1)).findByUser(account);
	}

	@Test
	void orderHistoryRepo_FindByUser_NullInput() {
		when(orderHistoryRepo.findByUser(null))
			.thenThrow(new IllegalArgumentException("User must not be null"));

		assertThatThrownBy(() -> orderHistoryRepo.findByUser(null))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("User must not be null");

		verify(orderHistoryRepo, times(1)).findByUser(null);
	}


	@Test
	void orderHistoryRepo_testFindByUserAndOrderStatus_Success() {
		when(orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING))
			.thenReturn(List.of(orderHistory));

		List<OrderHistory> foundOrders = orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);

		assertThat(foundOrders).isNotEmpty().hasSize(1);
		assertThat(foundOrders.get(0).getOrderStatus()).isEqualTo(OrderStatus.ON_PROCESSING);

		verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);
	}

	@Test
	void orderHistoryRepo_FindByUserAndOrderStatus_NotFound() {
		when(orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING))
			.thenReturn(Collections.emptyList());

		List<OrderHistory> foundOrders = orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);

		assertThat(foundOrders).isEmpty();

		verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);
	}

	@Test
	void orderHistoryRepo_FindByUserAndOrderStatus_ON_CONFIRM() {
		when(orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_CONFIRM))
			.thenReturn(List.of(orderHistoryOnConfirm));

		List<OrderHistory> foundOrders = orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_CONFIRM);

		assertThat(foundOrders).isNotEmpty().hasSize(1);
		assertThat(foundOrders.get(0).getOrderStatus()).isEqualTo(OrderStatus.ON_CONFIRM);

		verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(account, OrderStatus.ON_CONFIRM);
	}

	@Test
	void orderHistoryRepo_FindByUserAndOrderStatus_ON_SHIPPING() {
		when(orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_SHIPPING))
			.thenReturn(List.of(orderHistoryShip));

		List<OrderHistory> foundOrders = orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_SHIPPING);

		assertThat(foundOrders).isNotEmpty().hasSize(1);
		assertThat(foundOrders.get(0).getOrderStatus()).isEqualTo(OrderStatus.ON_SHIPPING);

		verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(account, OrderStatus.ON_SHIPPING);
	}

	@Test
	void orderHistoryRepo_FindByUserAndOrderStatus_DatabaseError() {
		when(orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING))
			.thenThrow(new RuntimeException("Database connection error"));

		assertThatThrownBy(() -> orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Database connection error");

		verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);
	}

	@Test
	void orderHistoryRepo_FindByUserAndOrderStatus_NullInput() {
		when(orderHistoryRepo.findByUserAndOrderStatus(null, OrderStatus.ON_PROCESSING))
			.thenThrow(new IllegalArgumentException("User must not be null"));

		assertThatThrownBy(() -> orderHistoryRepo.findByUserAndOrderStatus(null, OrderStatus.ON_PROCESSING))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("User must not be null");

		verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(null, OrderStatus.ON_PROCESSING);
	}

	@Test
	void orderHistoryRepo_FindByIdAndUser() {
		when(orderHistoryRepo.findByIdAndUser("1L", account)).thenReturn(Optional.of(orderHistory));

		Optional<OrderHistory> foundOrder = orderHistoryRepo.findByIdAndUser("1L", account);

		assertThat(foundOrder).isPresent();
		assertThat(foundOrder.get().getId()).isEqualTo("1L");

		verify(orderHistoryRepo, times(1)).findByIdAndUser("1L", account);
	}

	@Test
	void orderHistoryRepo_FindByIdAndUser_NotFound() {
		when(orderHistoryRepo.findByIdAndUser("1L", account)).thenReturn(Optional.empty());

		Optional<OrderHistory> foundOrder = orderHistoryRepo.findByIdAndUser("1L", account);

		assertThat(foundOrder).isEmpty();

		verify(orderHistoryRepo, times(1)).findByIdAndUser("1L", account);
	}


//	@Test
//	void productRepo_FindAllByProductStatus() {
//		when(productRepo.findAllByProductStatus(ProductStatus.IN_STOCK)).thenReturn(Arrays.asList(product1, product2));
//
//		List<Product> products = productRepo.findAllByProductStatus(ProductStatus.IN_STOCK);
//
//		assertThat(products).hasSize(2);
//		assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");
//		assertThat(products.get(1).getProductName()).isEqualTo("Ketchup");
//
//		verify(productRepo, times(1)).findAllByProductStatus(ProductStatus.IN_STOCK);
//	}

	@Test
	void orderHistoryRepo_FindByIdAndUser_DifferentId() {
		when(orderHistoryRepo.findByIdAndUser("2L", account)).thenReturn(Optional.empty());

		Optional<OrderHistory> foundOrder = orderHistoryRepo.findByIdAndUser("2L", account);

		assertThat(foundOrder).isEmpty();

		verify(orderHistoryRepo, times(1)).findByIdAndUser("2L", account);
	}


	@Test
	void productRepo_testFindAllByProductNameContainingIgnoreCaseAndProductTypes_Success() {
		when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT))
			.thenReturn(List.of(product1));

		List<Product> products = productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT);

		assertThat(products).hasSize(1);
		assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");

		verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT);
	}

	@Test
	void productRepo_FindAllByProductNameContainingIgnoreCaseAndProductTypes_NotFound() {
		when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Vegetable", ProductType.VEGETABLE))
			.thenReturn(Collections.emptyList());

		List<Product> products = productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Vegetable", ProductType.VEGETABLE);

		assertThat(products).isEmpty();

		verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCaseAndProductTypes("Vegetable", ProductType.VEGETABLE);
	}

	@Test
	void productRepo_FindAllByProductNameContainingIgnoreCaseAndProductTypes_PartialMatch() {
		when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Ribs", ProductType.MEAT))
			.thenReturn(List.of(product1));

		List<Product> products = productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Ribs", ProductType.MEAT);

		assertThat(products).hasSize(1);
		assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");

		verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCaseAndProductTypes("Ribs", ProductType.MEAT);
	}

	@Test
	void productRepo_FindAllByProductNameContainingIgnoreCaseAndProductTypes_CaseInsensitive() {
		when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("pork ribs", ProductType.MEAT))
			.thenReturn(List.of(product1));

		List<Product> products = productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("pork ribs", ProductType.MEAT);

		assertThat(products).hasSize(1);
		assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");

		verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCaseAndProductTypes("pork ribs", ProductType.MEAT);
	}

	@Test
	void productRepo_FindAllByProductNameContainingIgnoreCaseAndProductTypes_NullKeyword() {
		when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes(null, ProductType.MEAT))
			.thenThrow(new IllegalArgumentException("Keyword must not be null"));

		assertThatThrownBy(() -> productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes(null, ProductType.MEAT))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Keyword must not be null");

		verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCaseAndProductTypes(null, ProductType.MEAT);
	}


	@Test
	void productRepo_testFindAllByProductNameContainingIgnoreCase() {
		when(productRepo.findAllByProductNameContainingIgnoreCase("Pork Ribs"))
			.thenReturn(List.of(product1));

		List<Product> products = productRepo.findAllByProductNameContainingIgnoreCase("Pork Ribs");

		assertThat(products).hasSize(1);
		assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");

		verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCase("Pork Ribs");
	}

	@Test
	void productRepo_testFindAllByProductTypes() {
		Pageable pageable = PageRequest.of(0, 10);

		List<Product> productList = Arrays.asList(product1, product3);
		Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

		when(productRepo.findAllByProductTypes(ProductType.MEAT, pageable))
			.thenReturn(productPage);

		Page<Product> products = productRepo.findAllByProductTypes(ProductType.MEAT, pageable);

		assertThat(products.getTotalElements()).isEqualTo(2);
		assertThat(products.getContent()).extracting(Product::getProductTypes).containsOnly(ProductType.MEAT);
		assertThat(products.getContent()).extracting(Product::getId).containsExactlyInAnyOrder("P001", "P003");
		assertThat(products.getContent()).extracting(Product::getProductName).containsExactlyInAnyOrder("Pork Ribs", "Pangasius Fish");

		verify(productRepo, times(1)).findAllByProductTypes(ProductType.MEAT, pageable);
	}

	@Test
	void productPriceRepo_findFirstById_ProductIdOrderById_DateDesc() {
		when(productPriceRepo.findFirstById_ProductIdOrderById_DateDesc("P001"))
			.thenReturn(Optional.of(productPriceHistory));
		Optional<ProductPriceHistory> foundProductPriceHistory = productPriceRepo.findFirstById_ProductIdOrderById_DateDesc("P001");
		assertThat(foundProductPriceHistory).isPresent();
		assertThat(foundProductPriceHistory.get()).isEqualTo(productPriceHistory);
		verify(productPriceRepo, times(1)).findFirstById_ProductIdOrderById_DateDesc("P001");
	}


//	@Test
//	void PostCommentRepo_findAllByPostIdAndCommentType() {
//		when(postCommentRepo.findAllByPostIdAndCommentType("001", CommentType.POST))
//			.thenReturn(List.of(postComment1, postComment2));
//		List<PostComment> result = postCommentRepo.findAllByPostIdAndCommentType("001", CommentType.POST);
//		Assertions.assertEquals(2, result.size());
//		Assertions.assertEquals("This is the first comment", result.get(0).getComment());
//		Assertions.assertEquals(CommentType.POST, result.get(0).getCommentType());
//		Assertions.assertEquals("001", result.get(0).getPostId());
//
//		Assertions.assertEquals("This is the second comment", result.get(1).getComment());
//		Assertions.assertEquals(CommentType.POST, result.get(1).getCommentType());
//		Assertions.assertEquals("002", result.get(1).getPostId());
//		verify(postCommentRepo, times(1)).findAllByPostIdAndCommentType("001", CommentType.POST);
//	}
//
//	@Test
//	void PostCommentRepo_findAllByPostIdAndCommentType_NoResults() {
//		when(postCommentRepo.findAllByPostIdAndCommentType("999", CommentType.POST))
//			.thenReturn(Collections.emptyList());
//
//		List<PostComment> result = postCommentRepo.findAllByPostIdAndCommentType("999", CommentType.POST);
//
//		Assertions.assertTrue(result.isEmpty());
//
//		verify(postCommentRepo, times(1)).findAllByPostIdAndCommentType("999", CommentType.POST);
//	}
//
//	@Test
//	void PostCommentRepo_findAllByPostIdAndCommentType_NullInput() {
//		when(postCommentRepo.findAllByPostIdAndCommentType(null, CommentType.POST))
//			.thenThrow(new IllegalArgumentException("Post ID must not be null"));
//
//		Assertions.assertThrows(IllegalArgumentException.class, () -> {
//			postCommentRepo.findAllByPostIdAndCommentType(null, CommentType.POST);
//		});
//
//		verify(postCommentRepo, times(1)).findAllByPostIdAndCommentType(null, CommentType.POST);
//	}
//
//
//	@Test
//	void PostCommentRepo_findAllByPostIdAndParentComment_Id() {
//		when(postCommentRepo.findAllByPostIdAndParentComment_Id("001", "testBlog1"))
//			.thenReturn(List.of(replyComment, replyComment1));
//
//		List<PostComment> result = postCommentRepo.findAllByPostIdAndParentComment_Id("001", "testBlog1");
//
//		Assertions.assertEquals(2, result.size());
//
//		Assertions.assertEquals("This is the first reply comment", result.get(0).getComment());
//		Assertions.assertEquals("001", result.get(0).getPostId());
//		Assertions.assertEquals("testBlog1", result.get(0).getParentComment().getId());
//
//		Assertions.assertEquals("This is the second reply comment", result.get(1).getComment());
//		Assertions.assertEquals("001", result.get(1).getPostId());
//		Assertions.assertEquals("testBlog1", result.get(1).getParentComment().getId());
//
//		verify(postCommentRepo, times(1)).findAllByPostIdAndParentComment_Id("001", "testBlog1");
//	}
//
//	@Test
//	void PostCommentRepo_findAllByPostIdAndParentComment_Id_NoResults() {
//		when(postCommentRepo.findAllByPostIdAndParentComment_Id("999", "nonExistentParent"))
//			.thenReturn(Collections.emptyList());
//
//		List<PostComment> result = postCommentRepo.findAllByPostIdAndParentComment_Id("999", "nonExistentParent");
//
//		Assertions.assertTrue(result.isEmpty());
//
//		verify(postCommentRepo, times(1))
//			.findAllByPostIdAndParentComment_Id("999", "nonExistentParent");
//	}
//
//	@Test
//	void PostCommentRepo_findAllByPostIdAndParentComment_Id_ValidPostButNoReplies() {
//		when(postCommentRepo.findAllByPostIdAndParentComment_Id("002", "parentWithoutReplies"))
//			.thenReturn(Collections.emptyList());
//
//		List<PostComment> result = postCommentRepo.findAllByPostIdAndParentComment_Id("002", "parentWithoutReplies");
//
//		Assertions.assertTrue(result.isEmpty());
//
//		verify(postCommentRepo, times(1))
//			.findAllByPostIdAndParentComment_Id("002", "parentWithoutReplies");
//	}
//
//	@Test
//	void PostCommentRepo_findAllByPostIdAndParentComment_Id_NullInput() {
//		when(postCommentRepo.findAllByPostIdAndParentComment_Id(null, "testBlog1"))
//			.thenThrow(new IllegalArgumentException("Post ID must not be null"));
//
//		Assertions.assertThrows(IllegalArgumentException.class, () -> {
//			postCommentRepo.findAllByPostIdAndParentComment_Id(null, "testBlog1");
//		});
//
//		verify(postCommentRepo, times(1))
//			.findAllByPostIdAndParentComment_Id(null, "testBlog1");
//	}

	@Test
	void PostCommentRepo_findAllByAccount() {
		when(postCommentRepo.findAllByAccount(account))
			.thenReturn(List.of(postComment1));

		List<PostComment> result = postCommentRepo.findAllByAccount(account);

		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals("This is the first comment", result.get(0).getComment());
		Assertions.assertEquals("testUser", result.get(0).getAccountId());

		verify(postCommentRepo, times(1)).findAllByAccount(account);
	}

	@Test
	void PostCommentRepo_findAllByAccount_NoResults() {
		when(postCommentRepo.findAllByAccount(account)).thenReturn(Collections.emptyList());

		List<PostComment> result = postCommentRepo.findAllByAccount(account);

		Assertions.assertTrue(result.isEmpty());

		verify(postCommentRepo, times(1)).findAllByAccount(account);
	}

	@Test
	void PostCommentRepo_findAllByAccount_MultipleComments() {
		when(postCommentRepo.findAllByAccount(account))
			.thenReturn(List.of(postComment1, postComment2));

		List<PostComment> result = postCommentRepo.findAllByAccount(account);

		Assertions.assertEquals(2, result.size());
		Assertions.assertEquals(postComment1.getComment(), result.get(0).getComment());
		Assertions.assertEquals(postComment2.getComment(), result.get(1).getComment());

		verify(postCommentRepo, times(1)).findAllByAccount(account);
	}

	@Test
	void PostCommentRepo_findAllByAccount_NullAccount() {
		when(postCommentRepo.findAllByAccount(null))
			.thenThrow(new IllegalArgumentException("Account must not be null"));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			postCommentRepo.findAllByAccount(null);
		});

		verify(postCommentRepo, times(1)).findAllByAccount(null);
	}

	@Test
	void PostCommentRepo_findByIdAndAccount_Success() {
		when(postCommentRepo.findByIdAndAccount("testBlog1", account))
			.thenReturn(Optional.of(postComment1));

		Optional<PostComment> result = postCommentRepo.findByIdAndAccount("testBlog1", account);

		Assertions.assertTrue(result.isPresent());
		Assertions.assertEquals("This is the first comment", result.get().getComment());

		verify(postCommentRepo, times(1)).findByIdAndAccount("testBlog1", account);
	}

	@Test
	void PostCommentRepo_findByIdAndAccount_Fail() {
		when(postCommentRepo.findByIdAndAccount("invalid_id", account))
			.thenReturn(Optional.empty());
		Optional<PostComment> result = postCommentRepo.findByIdAndAccount("invalid_id", account);

		Assertions.assertTrue(result.isEmpty());

		verify(postCommentRepo, times(1)).findByIdAndAccount("invalid_id", account);
	}

	//@Test
	void PaymentTransactionRepo_findByTransactionId_Success() {
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		paymentTransaction.setTransactionId("txn_123");

		when(paymentTransactionRepo.findByTransactionId("txn_123"))
			.thenReturn(Optional.of(paymentTransaction));

		Optional<PaymentTransaction> result = paymentTransactionRepo.findByTransactionId("txn_123");

		Assertions.assertTrue(result.isPresent());
		Assertions.assertEquals("txn_123", result.get().getTransactionId());

		verify(paymentTransactionRepo, times(1)).findByTransactionId("txn_123");
	}

}

