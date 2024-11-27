package com.culcon.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Table(name = "account_otp") // Ensure table name matches your database schema
public class AccountOTP {
    @Id
    @Column(name = "account_id")
    @NotNull
    @NotBlank
    private String accountId;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_expiration")
    private Timestamp otpExpiration;


    // check OTP
    public boolean isOTPValid() {
        if (this.getOtp()==null) {
            return false;
        }

        long currentTimeInMillis = System.currentTimeMillis();
        long otpRequestedTimeInMillis = this.otpExpiration.getTime();

        final long OTP_VALID_DURATION = 5 * 60 * 1000;   // 5 minutes

        if (otpRequestedTimeInMillis + OTP_VALID_DURATION < currentTimeInMillis) {
            // OTP expires
//	        	System.out.println("STO: " + otpRequestedTimeInMillis);
//	        	System.out.println("EXP: " + otpRequestedTimeInMillis + OTP_VALID_DURATION);
//	        	System.out.println("CUR: " + currentTimeInMillis);
            return false;
        }

        return true;
    }


}
