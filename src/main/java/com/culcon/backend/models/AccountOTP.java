package com.culcon.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;

@Entity
@Builder
@Setter
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_otp")
public class AccountOTP {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "account_id", insertable = false, updatable = false)
	@JdbcTypeCode(SqlTypes.UUID)
	private String accountId;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "otp")
	private String otp;

	@Column(name = "otp_expiration")
	private Timestamp otpExpiration;

	@Column(name = "activity_type")
	private String type;

	@Column(name = "email")
	private String email;

}
