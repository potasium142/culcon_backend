package com.culcon.backend.services.implement;

import com.culcon.backend.configs.VNPayConfig;
import com.culcon.backend.dtos.PaymentDTO;
import com.culcon.backend.models.OrderHistory;
import com.culcon.backend.models.PaymentStatus;
import com.culcon.backend.models.PaymentTransaction;
import com.culcon.backend.repositories.OrderHistoryRepo;
import com.culcon.backend.repositories.PaymentTransactionRepo;
import com.culcon.backend.services.PaymentService;
import com.culcon.backend.services.authenticate.AuthService;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import com.paypal.sdk.utilities.JsonValue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentImplement implements PaymentService {

	private final PaypalServerSdkClient client;
	private final PaymentTransactionRepo paymentTransactionRepo;
	private final OrderHistoryRepo orderHistoryRepo;
	private final AuthService authService;


	@Override
	public void createPayment(OrderHistory order,
	                          HttpServletRequest request)
		throws IOException, ApiException {
		var ordersController = client.getOrdersController();

		OrdersCreateInput ordersCreateInput = new OrdersCreateInput.Builder(
			null,
			new OrderRequest.Builder(
				CheckoutPaymentIntent.fromString("CAPTURE"),
				Collections.singletonList(
					new PurchaseUnitRequest.Builder(
						new AmountWithBreakdown.Builder(
							"USD",
							order.getTotalPrice().toString()
						).build()
					).build()
				)
			).build()
		).build();

		ApiResponse<Order> apiResponse = ordersController.ordersCreate(ordersCreateInput);

		var result = apiResponse.getResult();

		var paymentTransaction = PaymentTransaction.builder()
			.order(order)
			.amount(order.getTotalPrice())
			.transactionId(result.getId())
			.build();

		paymentTransactionRepo.save(paymentTransaction);
	}


	@Override
	public PaymentDTO capturePayment(String transactionId, HttpServletRequest request)
		throws IOException, ApiException {
		OrdersCaptureInput ordersCaptureInput = new OrdersCaptureInput.Builder(
			transactionId, null)
			.build();

		OrdersController ordersController = client.getOrdersController();
		ApiResponse<Order> apiResponse = ordersController.ordersCapture(ordersCaptureInput);
		var result = apiResponse.getResult();

		var paymentTransaction = paymentTransactionRepo.findByTransactionId(result.getId())
			.orElseThrow(() -> new NoSuchElementException("Transaction not found"));

		var paymentId = result
			.getPurchaseUnits().getFirst()
			.getPayments()
			.getCaptures().getFirst()
			.getId();

		paymentTransaction.setPaymentId(paymentId);
		paymentTransaction.setStatus(PaymentStatus.RECEIVED);

		paymentTransactionRepo.save(paymentTransaction);

		var order = paymentTransaction.getOrder();

		order.setPaymentStatus(PaymentStatus.RECEIVED);

		orderHistoryRepo.save(order);

		return PaymentDTO.from(paymentTransaction);
	}

	@Override
	public String getPayment(String orderId, HttpServletRequest request) throws IOException, ApiException {
		var account = authService.getUserInformation(request);
		var pt = paymentTransactionRepo.findByIdAndOrder_User(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Transaction of this order is not found"));

		var isOver3Hrs = pt.getCreateTime().before(
			Timestamp.valueOf(LocalDateTime.now().minusHours(3))
		);

		if (isOver3Hrs) {
			createPayment(
				orderHistoryRepo.findById(orderId).orElseThrow(
					() -> new NoSuchElementException("Order not found")
				),
				request);

			pt = paymentTransactionRepo.findByIdAndOrder_User(orderId, account)
				.orElseThrow(() -> new NoSuchElementException("Transaction of this order is not found"));
		}

		return "https://www.sandbox.paypal.com/checkoutnow?token=" + pt.getTransactionId();
	}

	@Async
	@Override
	public void refund(OrderHistory order) throws IOException, ApiException {
		var paymentTransactionO = paymentTransactionRepo
			.findByOrder(order);

		if (paymentTransactionO.isEmpty()) {
			return;
		}

		var paymentTransaction = paymentTransactionO.get();

		if (paymentTransaction.getStatus() == PaymentStatus.PENDING) {
			paymentTransactionRepo.delete(paymentTransaction);
			return;
		}

		PaymentsController paymentsController = client.getPaymentsController();

		CapturesRefundInput capturesRefundInput = new CapturesRefundInput.Builder(
			paymentTransaction.getPaymentId(), null).build();

		ApiResponse<Refund> refundApiResponse = paymentsController.capturesRefund(capturesRefundInput);

		paymentTransaction.setStatus(PaymentStatus.REFUNDED);
		paymentTransaction.setRefundId(refundApiResponse.getResult().getId());

		order.setPaymentStatus(PaymentStatus.REFUNDED);
		orderHistoryRepo.save(order);

		paymentTransactionRepo.save(paymentTransaction);

	}

	@Async
	@Override
	public void updatePrice(OrderHistory order, Float price) throws IOException, ApiException {
		var ordersController = client.getOrdersController();

		var ptO = paymentTransactionRepo.findByOrder(order);

		if (ptO.isEmpty()) {
			return;
		}

		var pt = ptO.get();

		OrdersPatchInput ordersPatchInput = new OrdersPatchInput.Builder(
			pt.getTransactionId(), null)
			.body(Collections.singletonList(
				new Patch.Builder(PatchOp.REPLACE)
					.path("/purchase_units/@reference_id=='default'/amount")
					.value(JsonValue.fromObject(
						"{\"currency_code\":\"USD\"" +
							",\"value\":" + price + "}"
					))
					.build()
			))
			.build();

		ordersController.ordersPatch(ordersPatchInput);
	}

	@Override
	public void createPaymentVNPay(OrderHistory order, String bank, HttpServletRequest request) throws IOException, ApiException {

		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String orderType = "other";

		double totalPrice = order.getTotalPrice();
		BigDecimal roundedPrice = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP);
		long amount = roundedPrice.multiply(BigDecimal.valueOf(100)).longValue();

		String bankCode = bank;

		String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
		String vnp_IpAddr = VNPayConfig.getIpAddress(request);

		String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(amount));
		vnp_Params.put("vnp_CurrCode", "USD");


		if (bankCode != null && !bankCode.isEmpty()) {
			vnp_Params.put("vnp_BankCode", bankCode);
		}


		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", orderType);

//		String locate = req.getParameter("language");
//		if (locate != null && !locate.isEmpty()) {
//			vnp_Params.put("vnp_Locale", locate);
//		} else {
//			vnp_Params.put("vnp_Locale", "vn");
//		}
		vnp_Params.put("vnp_Locale", "vn");

		vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				//Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				//Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;


		var paymentTransaction = PaymentTransaction.builder()
			.order(order)
			.amount(order.getTotalPrice())
			.url(paymentUrl)
			.build();

		paymentTransactionRepo.save(paymentTransaction);


	}

	@Override
	public String getPaymentVNPay(String orderId, HttpServletRequest request) throws IOException, ApiException {
		var account = authService.getUserInformation(request);
		var pt = paymentTransactionRepo.findByIdAndOrder_User(orderId, account)
			.orElseThrow(() -> new NoSuchElementException("Transaction of this order is not found"));


		return pt.getUrl();
	}

}
