package com.culcon.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_otp") // Ensure table name matches your database schema
public class AccountOTP {
	@Id
	@Column(name = "account_id")
	private String accountId;

	@MapsId
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "otp")
	private String otp;

	@Column(name = "otp_expiration")
	private Timestamp otpExpiration;
}
