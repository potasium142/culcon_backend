package com.culcon.backend.dtos;

import com.culcon.backend.models.user.AccountOTP;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record OTPResponse(
	String accountId,
	Timestamp expireTime
) {
	public static OTPResponse of(AccountOTP otp) {
		return OTPResponse.builder()
			.accountId(otp.getAccountId())
			.expireTime(otp.getOtpExpiration())
			.build();
	}
}
