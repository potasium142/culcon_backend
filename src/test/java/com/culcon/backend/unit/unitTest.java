package com.culcon.backend.unit;
import com.culcon.backend.dtos.blog.BlogComment;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Mock PostCommentRepo postCommentRepo;
    private Product product;
    private Product product1;
    private Product product2;
    private Product product3;
    private Account account;
    private AccountOTP accountOTP;
    private OrderHistory orderHistory;
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
                .id(1L)
                .user(account)
                .orderStatus(OrderStatus.ON_PROCESSING)
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
                .status(PaymentStatus.CREATED)
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
        Assertions.assertEquals(PaymentStatus.CREATED, paymentTransaction.getStatus(), "Trạng thái thanh toán không đúng");
        Assertions.assertEquals(100.0F, paymentTransaction.getAmount(), "Số tiền không đúng");
        Assertions.assertTrue(paymentTransaction.getUrl().startsWith("http://"), "URL phải bắt đầu bằng 'http://'");
    }


    @Test
    void AccountRepo_FindByUsername() {
        when(accountRepo.findByUsername("testUser")).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountRepo.findByUsername("testUser");

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getUsername()).isEqualTo("testUser");

        verify(accountRepo, times(1)).findByUsername("testUser");
    }

    @Test
    void AccountRepo_FindAccountById() {
        when(accountRepo.findAccountById("94511231-59ce-45cb-9edc-196c378064a1")).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountRepo.findAccountById("94511231-59ce-45cb-9edc-196c378064a1");

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getId()).isEqualTo("94511231-59ce-45cb-9edc-196c378064a1");

        verify(accountRepo, times(1)).findAccountById("94511231-59ce-45cb-9edc-196c378064a1");
    }

    @Test
    void AccountRepo_FindAccountByEmail() {
        when(accountRepo.findAccountByEmail("test@example.com")).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountRepo.findAccountByEmail("test@example.com");

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getEmail()).isEqualTo("test@example.com");

        verify(accountRepo, times(1)).findAccountByEmail("test@example.com");
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
    void AccountRepo_ExistsByUsername() {
        when(accountRepo.existsByUsername("testUser")).thenReturn(true);

        Boolean exists = accountRepo.existsByUsername("testUser");

        assertThat(exists).isTrue();

        verify(accountRepo, times(1)).existsByUsername("testUser");
    }
    @Test
    void AccountOTPRepo_findByOtpAndAccountId(){
        when(accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1","94511231-59ce-45cb-9edc-196c378064a1")).thenReturn(Optional.of(accountOTP));
        Optional<AccountOTP> foundAccountOTP = accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1","94511231-59ce-45cb-9edc-196c378064a1");

        assertThat(foundAccountOTP).isPresent();
        assertThat(foundAccountOTP.get().getOtp()).isEqualTo("Rgy8YTrwELqlh1");
        assertThat(foundAccountOTP.get().getAccountId()).isEqualTo("94511231-59ce-45cb-9edc-196c378064a1");

        verify(accountOTPRepo, times(1)).findByOtpAndAccountId("Rgy8YTrwELqlh1","94511231-59ce-45cb-9edc-196c378064a1");

    }
    @Test
    void AccountOTPRepo_findByAccount() {
        when(accountOTPRepo.findByAccount(account)).thenReturn(Optional.of(accountOTP));

        Optional<AccountOTP> foundOTP = accountOTPRepo.findByAccount(account);

        assertThat(foundOTP).isPresent();
        assertThat(foundOTP.get().getAccount()).isEqualTo(account);

        verify(accountOTPRepo, times(1)).findByAccount(account);
    }
    @Test
    void orderHistoryRepo_FindByUser() {
        when(orderHistoryRepo.findByUser(account)).thenReturn(orderHistories);

        List<OrderHistory> foundOrders = orderHistoryRepo.findByUser(account);

        assertThat(foundOrders).isNotEmpty().hasSize(1);
        assertThat(foundOrders.get(0).getUser()).isEqualTo(account);

        verify(orderHistoryRepo, times(1)).findByUser(account);
    }
    @Test
    void orderHistoryRepo_testFindByUserAndOrderStatus() {
        when(orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING))
                .thenReturn(List.of(orderHistory)); 

        List<OrderHistory> foundOrders = orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);

        assertThat(foundOrders).isNotEmpty().hasSize(1);
        assertThat(foundOrders.get(0).getOrderStatus()).isEqualTo(OrderStatus.ON_PROCESSING);

        verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);
    }
    @Test
    void orderHistoryRepo_FindByIdAndUser() {
        when(orderHistoryRepo.findByIdAndUser(1L, account)).thenReturn(Optional.of(orderHistory));

        Optional<OrderHistory> foundOrder = orderHistoryRepo.findByIdAndUser(1L, account);

        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(1L);

        verify(orderHistoryRepo, times(1)).findByIdAndUser(1L, account);
    }
    @Test
    void productRepo_FindAllByProductStatus() {
        when(productRepo.findAllByProductStatus(ProductStatus.IN_STOCK)).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productRepo.findAllByProductStatus(ProductStatus.IN_STOCK);

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");
        assertThat(products.get(1).getProductName()).isEqualTo("Ketchup");

        verify(productRepo, times(1)).findAllByProductStatus(ProductStatus.IN_STOCK);
    }
    @Test
    void productRepo_testFindAllByProductNameContainingIgnoreCaseAndProductTypes() {
        when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT))
                .thenReturn(List.of(product1));

        List<Product> products = productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT);

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");

        verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT);
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
        when(productRepo.findAllByProductTypes(ProductType.MEAT))
                .thenAnswer(invocation -> {
                    return Arrays.asList(product1, product2, product3)
                            .stream()
                            .filter(product -> product.getProductTypes() == ProductType.MEAT)
                            .collect(Collectors.toList());
                });

        List<Product> products = productRepo.findAllByProductTypes(ProductType.MEAT);

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getProductTypes()).isEqualTo(ProductType.MEAT);
        assertThat(products.get(1).getProductTypes()).isEqualTo(ProductType.MEAT);

        verify(productRepo, times(1)).findAllByProductTypes(ProductType.MEAT);
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
    @Test
    void productPriceRepo_FindFirstById_ProductOrderById_DateDesc() {
        when(productPriceRepo.findFirstById_ProductOrderById_DateDesc(product)).thenReturn(Optional.of(productPriceHistory));
        Optional<ProductPriceHistory> foundProductPriceHistory = productPriceRepo.findFirstById_ProductOrderById_DateDesc(product);
        assertThat(foundProductPriceHistory).isPresent();
        assertThat(foundProductPriceHistory.get()).isEqualTo(productPriceHistory);

        verify(productPriceRepo, times(1)).findFirstById_ProductOrderById_DateDesc(product);
    }
    @Test
    void PostCommentRepo_findAllByPostIdAndCommentType() {
    when(postCommentRepo.findAllByPostIdAndCommentType("001", CommentType.POST))
            .thenReturn(List.of(postComment1, postComment2));
    List<PostComment> result = postCommentRepo.findAllByPostIdAndCommentType("001", CommentType.POST);
        Assertions.assertEquals(2,result.size());
        Assertions.assertEquals("This is the first comment", result.get(0).getComment());
        Assertions.assertEquals(CommentType.POST, result.get(0).getCommentType());
        Assertions.assertEquals("001", result.get(0).getPostId());

        Assertions.assertEquals("This is the second comment", result.get(1).getComment());
        Assertions.assertEquals(CommentType.POST, result.get(1).getCommentType());
        Assertions.assertEquals("002", result.get(1).getPostId());
        verify(postCommentRepo, times(1)).findAllByPostIdAndCommentType("001", CommentType.POST);
    }
    @Test
    void PostCommentRepo_findAllByPostIdAndParentComment_Id() {
        when(postCommentRepo.findAllByPostIdAndParentComment_Id("001", "testBlog1"))
                .thenReturn(List.of(replyComment, replyComment1));
        List<PostComment> result = postCommentRepo.findAllByPostIdAndParentComment_Id("001", "testBlog1");

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("This is the first reply comment", result.get(0).getComment());
        Assertions.assertEquals("This is the second reply comment", result.get(1).getComment());

        verify(postCommentRepo, times(1))
                .findAllByPostIdAndParentComment_Id("001", "testBlog1");
    }
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
    @Test
    void findByTransactionId_Success() {
        when(paymentTransactionRepo.findByTransactionId("txn_123"))
                .thenReturn(Optional.ofNullable(paymentTransaction));

        Optional<PaymentTransaction> result = paymentTransactionRepo.findByTransactionId("txn_123");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("txn_123", result.get().getTransactionId());
        verify(paymentTransactionRepo, times(1)).findByTransactionId("txn_123");
    }

}

