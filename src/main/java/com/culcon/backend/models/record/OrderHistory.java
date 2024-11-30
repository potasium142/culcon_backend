package com.culcon.backend.models.record;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_history")
public class OrderHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "order_date")
	@JdbcTypeCode(SqlTypes.LOCAL_DATE_TIME)
	private LocalTime date;

	@Column(name = "order_items")
	@ElementCollection(fetch = FetchType.LAZY)
	private List<OrderHistoryItem> items;

	@Column(name = "order_status")
	@Enumerated(EnumType.ORDINAL)
	private OrderStatus orderStatus;

	@Column(name = "total_price")
	private Float totalPrice;
}
