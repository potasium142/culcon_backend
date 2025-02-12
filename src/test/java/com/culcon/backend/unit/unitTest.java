package com.culcon.backend.unit;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountRepoTest {

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
    private List<OrderHistory> orderHistories;
    private PostComment postComment;

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
    }



    @Test
    void testFindByUsername() {
        when(accountRepo.findByUsername("testUser")).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountRepo.findByUsername("testUser");

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getUsername()).isEqualTo("testUser");

        verify(accountRepo, times(1)).findByUsername("testUser");
    }

    @Test
    void testFindAccountById() {
        when(accountRepo.findAccountById("94511231-59ce-45cb-9edc-196c378064a1")).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountRepo.findAccountById("94511231-59ce-45cb-9edc-196c378064a1");

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getId()).isEqualTo("94511231-59ce-45cb-9edc-196c378064a1");

        verify(accountRepo, times(1)).findAccountById("94511231-59ce-45cb-9edc-196c378064a1");
    }

    @Test
    void testFindAccountByEmail() {
        when(accountRepo.findAccountByEmail("test@example.com")).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountRepo.findAccountByEmail("test@example.com");

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getEmail()).isEqualTo("test@example.com");

        verify(accountRepo, times(1)).findAccountByEmail("test@example.com");
    }

    @Test
    void testFindByToken() {
        when(accountRepo.findByToken("Rgy8YTrwELqlh1")).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountRepo.findByToken("Rgy8YTrwELqlh1");

        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getToken()).isEqualTo("Rgy8YTrwELqlh1");

        verify(accountRepo, times(1)).findByToken("Rgy8YTrwELqlh1");
    }

    @Test
    void testExistsByUsername() {
        when(accountRepo.existsByUsername("testUser")).thenReturn(true);

        Boolean exists = accountRepo.existsByUsername("testUser");

        assertThat(exists).isTrue();

        verify(accountRepo, times(1)).existsByUsername("testUser");
    }
    @Test
    void findByOtpAndAccountId(){
        when(accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1","94511231-59ce-45cb-9edc-196c378064a1")).thenReturn(Optional.of(accountOTP));
        Optional<AccountOTP> foundAccountOTP = accountOTPRepo.findByOtpAndAccountId("Rgy8YTrwELqlh1","94511231-59ce-45cb-9edc-196c378064a1");

        assertThat(foundAccountOTP).isPresent();
        assertThat(foundAccountOTP.get().getOtp()).isEqualTo("Rgy8YTrwELqlh1");
        assertThat(foundAccountOTP.get().getAccountId()).isEqualTo("94511231-59ce-45cb-9edc-196c378064a1");

        verify(accountOTPRepo, times(1)).findByOtpAndAccountId("Rgy8YTrwELqlh1","94511231-59ce-45cb-9edc-196c378064a1");

    }
    @Test
    void findByAccount() {
        when(accountOTPRepo.findByAccount(account)).thenReturn(Optional.of(accountOTP));

        Optional<AccountOTP> foundOTP = accountOTPRepo.findByAccount(account);

        assertThat(foundOTP).isPresent();
        assertThat(foundOTP.get().getAccount()).isEqualTo(account);

        verify(accountOTPRepo, times(1)).findByAccount(account);
    }
    @Test
    void testFindByUser() {
        when(orderHistoryRepo.findByUser(account)).thenReturn(orderHistories);

        List<OrderHistory> foundOrders = orderHistoryRepo.findByUser(account);

        assertThat(foundOrders).isNotEmpty().hasSize(1);
        assertThat(foundOrders.get(0).getUser()).isEqualTo(account);

        verify(orderHistoryRepo, times(1)).findByUser(account);
    }
    @Test
    void testFindByUserAndOrderStatus() {
        when(orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING))
                .thenReturn(List.of(orderHistory));

        List<OrderHistory> foundOrders = orderHistoryRepo.findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);

        assertThat(foundOrders).isNotEmpty().hasSize(1);
        assertThat(foundOrders.get(0).getOrderStatus()).isEqualTo(OrderStatus.ON_PROCESSING);

        verify(orderHistoryRepo, times(1)).findByUserAndOrderStatus(account, OrderStatus.ON_PROCESSING);
    }
    @Test
    void testFindByIdAndUser() {
        when(orderHistoryRepo.findByIdAndUser(1L, account)).thenReturn(Optional.of(orderHistory));

        Optional<OrderHistory> foundOrder = orderHistoryRepo.findByIdAndUser(1L, account);

        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(1L);

        verify(orderHistoryRepo, times(1)).findByIdAndUser(1L, account);
    }
    @Test
    void testFindAllByProductStatus() {
        when(productRepo.findAllByProductStatus(ProductStatus.IN_STOCK)).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productRepo.findAllByProductStatus(ProductStatus.IN_STOCK);

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");
        assertThat(products.get(1).getProductName()).isEqualTo("Ketchup");

        verify(productRepo, times(1)).findAllByProductStatus(ProductStatus.IN_STOCK);
    }
    @Test
    void testFindAllByProductNameContainingIgnoreCaseAndProductTypes() {
        when(productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT))
                .thenReturn(List.of(product1));

        List<Product> products = productRepo.findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT);

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");

        verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCaseAndProductTypes("Pork Ribs", ProductType.MEAT);
    }
    @Test
    void testFindAllByProductNameContainingIgnoreCase() {
        when(productRepo.findAllByProductNameContainingIgnoreCase("Pork Ribs"))
                .thenReturn(List.of(product1));

        List<Product> products = productRepo.findAllByProductNameContainingIgnoreCase("Pork Ribs");

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getProductName()).isEqualTo("Pork Ribs");

        verify(productRepo, times(1)).findAllByProductNameContainingIgnoreCase("Pork Ribs");
    }
    @Test
    void testFindAllByProductTypes() {
        when(productRepo.findAllByProductTypes(ProductType.MEAT))
                .thenAnswer(invocation -> {
                    // Lọc danh sách sản phẩm với ProductType.MEAT
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
    void testfindFirstById_ProductIdOrderById_DateDesc() {
        when(productPriceRepo.findFirstById_ProductIdOrderById_DateDesc("P001"))
                .thenReturn(Optional.of(productPriceHistory));
        Optional<ProductPriceHistory> foundProductPriceHistory = productPriceRepo.findFirstById_ProductIdOrderById_DateDesc("P001");
        assertThat(foundProductPriceHistory).isPresent();
        assertThat(foundProductPriceHistory.get()).isEqualTo(productPriceHistory);
        verify(productPriceRepo, times(1)).findFirstById_ProductIdOrderById_DateDesc("P001");
    }
    @Test
    void testFindFirstById_ProductOrderById_DateDesc() {
        when(productPriceRepo.findFirstById_ProductOrderById_DateDesc(product)).thenReturn(Optional.of(productPriceHistory));
        Optional<ProductPriceHistory> foundProductPriceHistory = productPriceRepo.findFirstById_ProductOrderById_DateDesc(product);

        assertThat(foundProductPriceHistory).isPresent();
        assertThat(foundProductPriceHistory.get()).isEqualTo(productPriceHistory);

        verify(productPriceRepo, times(1)).findFirstById_ProductOrderById_DateDesc(product);
    }
    @Test
    void findAllByPostIdAndCommentType() {
        when(postCommentRepo.findAllByPostIdAndCommentType())
    }
}

