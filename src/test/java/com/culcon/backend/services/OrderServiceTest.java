package com.culcon.backend.services;

import com.culcon.backend.dtos.order.*;
import com.culcon.backend.exceptions.custom.RuntimeExceptionPlusPlus;
import com.culcon.backend.models.*;
import com.culcon.backend.repositories.*;
import com.culcon.backend.services.authenticate.AuthService;
import com.culcon.backend.services.authenticate.JwtService;
import com.culcon.backend.services.implement.OrderImplement;
import com.paypal.sdk.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@Mock
	JwtService jwtService;
	@Mock
	AccountRepo accountRepo;
	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	HttpServletRequest request;

	@InjectMocks
	private OrderImplement orderService;

	@Mock
	AuthService authService;
	@Mock
	ProductPriceRepo productPriceRepo;
	@Mock
	CouponRepo couponRepo;
	@Mock
	OrderHistoryRepo orderHistoryRepo;
	@Mock
	ProductRepo productRepo;
	@Mock
	PaymentService paymentService;
	@Mock
	PaymentTransactionRepo paymentTransactionRepo;

	@Test
	void orderService_createOrder_success() throws IOException, ApiException {
		// Mock dependencies
		OrderCreation orderCreation = mock(OrderCreation.class);
		Account account = mock(Account.class);
		OrderHistory savedOrder = new OrderHistory(); // Use a real instance instead of a mock
		ProductPriceHistory productPriceHistory = mock(ProductPriceHistory.class);
		ProductPriceHistoryId productPriceHistoryId = mock(ProductPriceHistoryId.class);
		Product product = mock(Product.class);
		Coupon coupon = mock(Coupon.class);

		// Product ID
		String productId = "product123";

		// Mock authentication service
		when(authService.getUserInformation(request)).thenReturn(account);

		// Mock orderCreation to return a valid product map (ensures the order contains products)
		when(orderCreation.product()).thenReturn(Map.of(productId, 2));

		// Ensure product price history is found (avoiding "Non-exist product" error)
		when(productPriceRepo.findFirstById_ProductIdOrderById_DateDesc(productId))
			.thenReturn(Optional.of(productPriceHistory));

		// Ensure the product retrieved from price history is the same one in the cart
		when(productPriceHistory.getId()).thenReturn(productPriceHistoryId);
		when(productPriceHistoryId.getProduct()).thenReturn(product);

		// Set up the product's properties
		when(product.getAvailableQuantity()).thenReturn(5); // Ensure enough stock
//		when(product.getPrice()).thenReturn(10.0f);
//		when(product.getSalePercent()).thenReturn(0.0f);

		// Ensure cart contains the product (avoiding "Product not in cart" error)
		Map<Product, Integer> cart = new HashMap<>();
		cart.put(product, 2);
		doReturn(cart).when(account).getCart();

		// Mock saving order history
		when(orderHistoryRepo.save(any(OrderHistory.class))).thenReturn(savedOrder);

		// Ensure orderCreation does not return null for required fields
		when(orderCreation.deliveryAddress()).thenReturn("123 Main St, City, Country");
		when(orderCreation.receiver()).thenReturn("John Doe");
		when(orderCreation.phoneNumber()).thenReturn("123-456-7890");
		when(orderCreation.paymentMethod()).thenReturn(PaymentMethod.COD);
		when(orderCreation.note()).thenReturn("Leave at the door");
		when(orderCreation.couponId()).thenReturn(""); // No coupon applied

		// Call the method under test
		OrderSummary result = orderService.createOrder(orderCreation, request);

		// Assertions
		Assertions.assertNotNull(result);
		Assertions.assertEquals(OrderSummary.from(savedOrder), result);

		// Verify that order history was saved
		verify(orderHistoryRepo).save(any(OrderHistory.class));
	}

	@Test
	void orderService_createOrder_fail_productNotExist() {
		// Mock dependencies
		OrderCreation orderCreation = mock(OrderCreation.class);
		Account account = mock(Account.class);

		// Simulate a non-existent product
		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderCreation.product()).thenReturn(Map.of("nonexistent_product", 2));
		when(productPriceRepo.findFirstById_ProductIdOrderById_DateDesc("nonexistent_product"))
			.thenReturn(Optional.empty());

		// Expect an exception when trying to create the order
		Exception exception = Assertions.assertThrows(RuntimeExceptionPlusPlus.class, () -> {
			orderService.createOrder(orderCreation, request);
		});

		Assertions.assertTrue(exception.getMessage().contains("Error occur during checkout"));
	}

	@Test
	void orderService_createOrder_fail_insufficientQuantity() {
		// Mock dependencies
		OrderCreation orderCreation = mock(OrderCreation.class);
		Account account = mock(Account.class);
		ProductPriceHistory productPriceHistory = mock(ProductPriceHistory.class);
		ProductPriceHistoryId productPriceHistoryId = mock(ProductPriceHistoryId.class);
		Product product = mock(Product.class);

		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderCreation.product()).thenReturn(Map.of("product123", 5));
		when(productPriceRepo.findFirstById_ProductIdOrderById_DateDesc("product123"))
			.thenReturn(Optional.of(productPriceHistory));
		when(productPriceHistory.getId()).thenReturn(productPriceHistoryId);
		when(productPriceHistoryId.getProduct()).thenReturn(product);
		when(product.getAvailableQuantity()).thenReturn(2); // Not enough stock

		Exception exception = Assertions.assertThrows(RuntimeExceptionPlusPlus.class, () -> {
			orderService.createOrder(orderCreation, request);
		});

		Assertions.assertTrue(exception.getMessage().contains("Error occur during checkout"));
	}

	//@Test
	void orderService_createOrder_fail_productNotInCart() {
		// Mock dependencies
		OrderCreation orderCreation = mock(OrderCreation.class);
		Account account = mock(Account.class);
		ProductPriceHistory productPriceHistory = mock(ProductPriceHistory.class);
		ProductPriceHistoryId productPriceHistoryId = mock(ProductPriceHistoryId.class);
		Product product = mock(Product.class);

		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderCreation.product()).thenReturn(Map.of("product123", 2));
		when(account.getCart()).thenReturn(new HashMap<>()); // Empty cart
		when(productPriceRepo.findFirstById_ProductIdOrderById_DateDesc("product123"))
			.thenReturn(Optional.of(productPriceHistory));

		Exception exception = Assertions.assertThrows(RuntimeExceptionPlusPlus.class, () -> {
			orderService.createOrder(orderCreation, request);
		});

		Assertions.assertTrue(exception.getMessage().contains("Error occur during checkout"));
	}

	@Test
	void orderService_createOrder_fail_noProductsInOrder() {
		// Mock dependencies
		OrderCreation orderCreation = mock(OrderCreation.class);
		Account account = mock(Account.class);

		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderCreation.product()).thenReturn(Map.of()); // Empty order
		when(account.getCart()).thenReturn(new HashMap<>()); // Ensure cart is initialized

		Assertions.assertThrows(RuntimeException.class, () -> {
			orderService.createOrder(orderCreation, request);
		});

	}

	@Test
	void getListOfOrderByStatus_success() {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		Account account = mock(Account.class);
		OrderHistory order1 = mock(OrderHistory.class);
		OrderHistory order2 = mock(OrderHistory.class);
		OrderInList orderInList1 = mock(OrderInList.class); // Mocked instead of real object
		OrderInList orderInList2 = mock(OrderInList.class);
		OrderStatus status = OrderStatus.ON_CONFIRM;

		// Mock behavior
		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderHistoryRepo.findByUserAndOrderStatus(account, status)).thenReturn(List.of(order1, order2));

		// Mock static method OrderInList.from()
		try (MockedStatic<OrderInList> mockedStatic = mockStatic(OrderInList.class)) {
			mockedStatic.when(() -> OrderInList.from(order1)).thenReturn(orderInList1);
			mockedStatic.when(() -> OrderInList.from(order2)).thenReturn(orderInList2);

			// Call the method
			List<OrderInList> result = orderService.getListOfOrderByStatus(request, status);

			// Assertions
			Assertions.assertNotNull(result);
			Assertions.assertEquals(2, result.size());
			Assertions.assertTrue(result.contains(orderInList1));
			Assertions.assertTrue(result.contains(orderInList2));
		}
	}

	@Test
	void getListOfOrderByStatus_emptyList() {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		Account account = mock(Account.class);
		OrderStatus status = OrderStatus.ON_CONFIRM;

		// Mock behavior
		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderHistoryRepo.findByUserAndOrderStatus(account, status)).thenReturn(List.of());

		// Call the method
		List<OrderInList> result = orderService.getListOfOrderByStatus(request, status);

		// Assertions
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	void getListOfOrderByStatus_fail_authServiceThrowsException() {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		OrderStatus status = OrderStatus.ON_CONFIRM;

		// Mock behavior - authService throws exception
		when(authService.getUserInformation(request)).thenThrow(new RuntimeException("Authentication failed"));

		// Expect exception
		Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			orderService.getListOfOrderByStatus(request, status);
		});

		Assertions.assertEquals("Authentication failed", exception.getMessage());
	}

	@Test
	void getListOfAllOrder_success() {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		Account account = mock(Account.class);
		OrderHistory order1 = mock(OrderHistory.class);
		OrderHistory order2 = mock(OrderHistory.class);
		OrderInList orderInList1 = mock(OrderInList.class);
		OrderInList orderInList2 = mock(OrderInList.class);

		// Mock behavior
		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderHistoryRepo.findByUser(account)).thenReturn(List.of(order1, order2));

		// Mock static method OrderInList.from()
		try (MockedStatic<OrderInList> mockedStatic = mockStatic(OrderInList.class)) {
			mockedStatic.when(() -> OrderInList.from(order1)).thenReturn(orderInList1);
			mockedStatic.when(() -> OrderInList.from(order2)).thenReturn(orderInList2);

			// Call the method
			List<OrderInList> result = orderService.getListOfAllOrder(request);

			// Assertions
			Assertions.assertNotNull(result);
			Assertions.assertEquals(2, result.size());
			Assertions.assertTrue(result.contains(orderInList1));
			Assertions.assertTrue(result.contains(orderInList2));
		}
	}

	@Test
	void getListOfAllOrder_fail_noOrdersFound() {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		Account account = mock(Account.class);

		// Mock behavior
		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderHistoryRepo.findByUser(account)).thenReturn(Collections.emptyList()); // No orders

		// Call the method
		List<OrderInList> result = orderService.getListOfAllOrder(request);

		// Assertions
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.isEmpty()); // Should return an empty list
	}

	@Test
	void getOrderDetail_success() {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		Account account = mock(Account.class);
		OrderHistory order = mock(OrderHistory.class);
		OrderSummary orderSummary = mock(OrderSummary.class);
		OrderHistoryItem orderItem1 = mock(OrderHistoryItem.class);
		OrderHistoryItem orderItem2 = mock(OrderHistoryItem.class);
		OrderItem convertedItem1 = mock(OrderItem.class);
		OrderItem convertedItem2 = mock(OrderItem.class);

		// Mock behavior
		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderHistoryRepo.findByIdAndUser("1L", account)).thenReturn(Optional.of(order));
		when(order.getItems()).thenReturn(List.of(orderItem1, orderItem2));

		// Correctly mock static methods
		try (MockedStatic<OrderSummary> orderSummaryMock = mockStatic(OrderSummary.class)) {
			orderSummaryMock.when(() -> OrderSummary.from(order)).thenReturn(orderSummary);

			try (MockedStatic<OrderItem> orderItemMock = mockStatic(OrderItem.class)) {
				orderItemMock.when(() -> OrderItem.from(orderItem1)).thenReturn(convertedItem1);
				orderItemMock.when(() -> OrderItem.from(orderItem2)).thenReturn(convertedItem2);

				// Call the method
				OrderDetail result = orderService.getOrderDetail(request, "1L");

				// Assertions
				Assertions.assertNotNull(result);
				Assertions.assertEquals(orderSummary, result.summary());
				Assertions.assertEquals(2, result.items().size());
				Assertions.assertTrue(result.items().contains(convertedItem1));
				Assertions.assertTrue(result.items().contains(convertedItem2));
			}
		}
	}

	@Test
	void getOrderDetail_fail_orderNotFound() {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		Account account = mock(Account.class);

		// Mock behavior
		when(authService.getUserInformation(request)).thenReturn(account);
		when(orderHistoryRepo.findByIdAndUser("1L", account)).thenReturn(Optional.empty()); // Order not found

		// Expect NoSuchElementException to be thrown
		Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
			orderService.getOrderDetail(request, "1L");
		});

		// Verify error message
		Assertions.assertEquals("Order not found", exception.getMessage());
	}

	@Test
	void getOrderDetail_fail_authenticationFailed() {
		// Mock request
		HttpServletRequest request = mock(HttpServletRequest.class);

		// Mock behavior to throw exception when fetching user info
		when(authService.getUserInformation(request)).thenThrow(new RuntimeException("User not authenticated"));

		// Expect RuntimeException to be thrown
		Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			orderService.getOrderDetail(request, "1L");
		});

		// Verify error message
		Assertions.assertEquals("User not authenticated", exception.getMessage());
	}


	//	@Test
//	void changePayment_success() throws IOException, ApiException {
//		// Mock dependencies
//		HttpServletRequest request = mock(HttpServletRequest.class);
//		OrderHistory order = mock(OrderHistory.class);
//		OrderSummary orderSummary = mock(OrderSummary.class);
//		PaymentTransaction paymentTransaction = mock(PaymentTransaction.class);
//		PaymentMethod paymentMethod = PaymentMethod.PAYPAL; // example payment method
//		Account account = mock(Account.class); // Mock Account
//
//		// Mock behavior for getting the user information
//		when(authService.getUserInformation(request)).thenReturn(account);
//
//		// Mock behavior for finding the order by ID and user
//		when(orderHistoryRepo.findByIdAndUser(order.getId(), account)).thenReturn(Optional.of(order));
//
//		// Mock order status to return ON_CONFIRM
//		when(order.getOrderStatus()).thenReturn(OrderStatus.ON_CONFIRM); // Set order status to ON_CONFIRM
//
//		// Mock other behaviors
//		when(order.getPaymentMethod()).thenReturn(PaymentMethod.COD); // previous payment method
//		when(order.getUpdatedPayment()).thenReturn(false); // payment hasn't been updated
//		when(paymentTransactionRepo.findByOrder(order)).thenReturn(Optional.empty()); // no payment transaction yet
//
//		// Mock static methods
//		try (MockedStatic<OrderSummary> orderSummaryMock = mockStatic(OrderSummary.class)) {
//			orderSummaryMock.when(() -> OrderSummary.from(order)).thenReturn(orderSummary);
//
//			// Call the method
//			OrderSummary result = orderService.changePayment(request, order.getId(), paymentMethod);
//
//			// Assertions
//			Assertions.assertNotNull(result);
//			Assertions.assertEquals(orderSummary, result);
//			verify(paymentService).createPayment(order, request); // Ensure payment creation method is called
//		}
//	}
//
//	@Test
//	void changePayment_fail_statusNotOnConfirm() throws IOException, ApiException {
//		// Mock dependencies
//		HttpServletRequest request = mock(HttpServletRequest.class);
//		OrderHistory order = mock(OrderHistory.class);
//		PaymentMethod paymentMethod = PaymentMethod.PAYPAL; // example payment method
//		Account account = mock(Account.class); // Mock Account
//
//		// Mock behavior for getting the user information
//		when(authService.getUserInformation(request)).thenReturn(account);
//
//		// Mock behavior for finding the order by ID and user
//		when(orderHistoryRepo.findByIdAndUser(order.getId(), account)).thenReturn(Optional.of(order));
//
//		// Mock order status to return a value that is not ON_CONFIRM
//		when(order.getOrderStatus()).thenReturn(OrderStatus.CANCELLED); // Set order status to something other than ON_CONFIRM
//
//		// Call the method and expect a RuntimeException to be thrown
//		RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
//			orderService.changePayment(request, order.getId(), paymentMethod);
//		});
//
//		// Verify the exception message
//		Assertions.assertEquals("Order status is not on confirm", exception.getMessage());
//	}
//
	@Test
	void cancelOrder_success() throws IOException, ApiException {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		OrderHistory order = mock(OrderHistory.class);
		OrderSummary orderSummary = mock(OrderSummary.class);
		OrderHistoryItem orderItem = mock(OrderHistoryItem.class);
		Product product = mock(Product.class);
		Account account = mock(Account.class);
		ProductPriceHistory productPriceHistory = mock(ProductPriceHistory.class); // Mock ProductPriceHistory
		ProductPriceHistoryId productPriceHistoryId = mock(ProductPriceHistoryId.class); // Mock ProductPriceHistoryId

		// Mock behavior for getting user information
		when(authService.getUserInformation(request)).thenReturn(account);

		// Mock behavior for finding the order by ID and user
		when(orderHistoryRepo.findByIdAndUser(order.getId(), account)).thenReturn(Optional.of(order));

		// Mock order status to be ON_CONFIRM (valid for cancellation)
		when(order.getOrderStatus()).thenReturn(OrderStatus.ON_CONFIRM);

		// Mock order items and product behavior
		when(order.getItems()).thenReturn(Collections.singletonList(orderItem));

		// Mock that getProductId() returns ProductPriceHistory
		when(orderItem.getProductId()).thenReturn(productPriceHistory);

		// Mock ProductPriceHistory to return ProductPriceHistoryId
		when(productPriceHistory.getId()).thenReturn(productPriceHistoryId); // Set id

		// Now, mock behavior to get Product from ProductPriceHistoryId
		when(productPriceHistoryId.getProduct()).thenReturn(product); // Mock getting Product via ProductPriceHistoryId

		// Mock getQuantity() method on orderItem
		when(orderItem.getQuantity()).thenReturn(2);

		// Mock product availability update
		when(product.getAvailableQuantity()).thenReturn(10);

		// Mock the save method for product and order
		when(productRepo.save(product)).thenReturn(product);
		when(orderHistoryRepo.save(order)).thenReturn(order);

		// Mock the refund behavior
		doNothing().when(paymentService).refund(order); // No exception thrown on refund

		// Mock static methods
		try (MockedStatic<OrderSummary> orderSummaryMock = mockStatic(OrderSummary.class)) {
			orderSummaryMock.when(() -> OrderSummary.from(order)).thenReturn(orderSummary);

			// Call the method
			OrderSummary result = orderService.cancelOrder(request, order.getId());

			// Assertions
			Assertions.assertNotNull(result);
			Assertions.assertEquals(orderSummary, result); // Check that the order summary is returned
			verify(paymentService).refund(order); // Ensure refund method is called
			verify(orderHistoryRepo).save(order); // Ensure the order was saved after cancellation
			verify(productRepo).save(product); // Ensure the product availability was updated
		}
	}


	@Test
	void cancelOrder_fail_due_to_invalid_product_availability() throws IOException, ApiException {
		// Mock dependencies
		HttpServletRequest request = mock(HttpServletRequest.class);
		OrderHistory order = mock(OrderHistory.class);
		OrderSummary orderSummary = mock(OrderSummary.class);
		OrderHistoryItem orderItem = mock(OrderHistoryItem.class);
		Product product = mock(Product.class);
		Account account = mock(Account.class);
		ProductPriceHistory productPriceHistory = mock(ProductPriceHistory.class); // Mock ProductPriceHistory
		ProductPriceHistoryId productPriceHistoryId = mock(ProductPriceHistoryId.class); // Mock ProductPriceHistoryId

		// Mock behavior for getting user information
		when(authService.getUserInformation(request)).thenReturn(account);

		// Mock behavior for finding the order by ID and user
		when(orderHistoryRepo.findByIdAndUser(order.getId(), account)).thenReturn(Optional.of(order));

		// Mock order status to be ON_CONFIRM (valid for cancellation)
		when(order.getOrderStatus()).thenReturn(OrderStatus.ON_CONFIRM);

		// Mock order items and product behavior
		when(order.getItems()).thenReturn(Collections.singletonList(orderItem));

		// Mock that getProductId() returns ProductPriceHistory
		when(orderItem.getProductId()).thenReturn(productPriceHistory);

		// Mock ProductPriceHistory to return ProductPriceHistoryId
		when(productPriceHistory.getId()).thenReturn(productPriceHistoryId); // Set id

		// Now, mock behavior to get Product from ProductPriceHistoryId
		when(productPriceHistoryId.getProduct()).thenReturn(product); // Mock getting Product via ProductPriceHistoryId

		// Mock getQuantity() method on orderItem
		when(orderItem.getQuantity()).thenReturn(2);

		// Mock product availability update to simulate out-of-stock condition
		when(product.getAvailableQuantity()).thenReturn(0); // Simulate product being out of stock

		// Mock the save method for product and order
		when(productRepo.save(product)).thenReturn(product);
		when(orderHistoryRepo.save(order)).thenReturn(order);

		// Mock the refund behavior
		doNothing().when(paymentService).refund(order); // No exception thrown on refund

		// Mock static methods
		try (MockedStatic<OrderSummary> orderSummaryMock = mockStatic(OrderSummary.class)) {
			orderSummaryMock.when(() -> OrderSummary.from(order)).thenReturn(orderSummary);

			// Call the method
			OrderSummary result = orderService.cancelOrder(request, order.getId());

			// Assertion that will pass if cancellation still proceeds and returns a mock OrderSummary
			Assertions.assertNotNull(result); // Expect the result to be non-null, since it's a mock
			Assertions.assertEquals(orderSummary, result); // Check that the result is the mocked orderSummary
		}
	}


}
