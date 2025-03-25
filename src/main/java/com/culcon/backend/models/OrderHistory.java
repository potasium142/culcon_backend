package com.culcon.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_history")
public class OrderHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@Column(name = "user_id", insertable = false, updatable = false)
	private String accountId;

	@Column(name = "order_date")
	@JdbcTypeCode(SqlTypes.LOCAL_DATE_TIME)
	@Builder.Default
	private LocalDateTime date = LocalDateTime.now();

	@Column(name = "order_items")
	@ElementCollection(fetch = FetchType.LAZY)
//	@AttributeOverrides({
//		@AttributeOverride(
//			name = "productId.id.product", column = @Column(name = "product_id")
//		),
//		@AttributeOverride(
//			name = "productId.id.date", column = @Column(name = "date")
//		)
//	})
	private List<OrderHistoryItem> items;

	@Column(name = "order_status")
	@Enumerated(EnumType.ORDINAL)
	@Builder.Default
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private OrderStatus orderStatus = OrderStatus.ON_CONFIRM;

	@Column(name = "total_price")
	private Float totalPrice;

	@JoinColumn(name = "coupon")
	@ManyToOne(fetch = FetchType.LAZY)
	private Coupon coupon;

	@Column(name = "delivery_address")
	private String deliveryAddress;

	@Column(name = "note")
	private String note;

	@Column(name = "receiver")
	private String receiver;

	@Column(name = "phonenumber", length = 12)
	private String phonenumber;

	@Column(name = "payment_method")
	@Enumerated(EnumType.ORDINAL)
	@Builder.Default
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private PaymentMethod paymentMethod = PaymentMethod.COD;

	@Column(name = "payment_status")
	@Enumerated(EnumType.ORDINAL)
	@Builder.Default
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;
}
