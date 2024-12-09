package com.culcon.backend.services.implement;

import com.culcon.backend.models.Account;
import com.culcon.backend.models.AccountOTP;
import com.culcon.backend.repositories.AccountOTPRepo;
import com.culcon.backend.services.OTPService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OTPImplement implements OTPService {

	@Value("${spring.mail.username}")
	private String emailAddress;

	private final AccountOTPRepo accountOTPRepo;

	private final JavaMailSender mailSender;


	@Override
	public AccountOTP generateOTP(Account account, String email,
	                              int otpLength,
	                              int expireMinutes) {
		String OTP = RandomString.make(otpLength);

		var accountOTP = accountOTPRepo.findByAccount(account)
			.orElse(AccountOTP.builder()
				.account(account)
					.accountId(account.getId())
				.build());

		accountOTP.setOtp(OTP);

		LocalDateTime currentDateTime = LocalDateTime.now().plusMinutes(expireMinutes);
		Timestamp sqlTimestamp = Timestamp.valueOf(currentDateTime);

		accountOTP.setOtpExpiration(sqlTimestamp);

		accountOTP.setEmail(email);

		return accountOTPRepo.save(accountOTP);
	}

	@Async
	@Override
	public void sendOTPEmail(AccountOTP accountOTP)
		throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(new InternetAddress(emailAddress, "Culinary Connect"));
		Account account = accountOTP.getAccount();

		helper.setTo(account.getEmail());

		String subject = "Here's your One Time Password (OTP) - Expire in 5 minutes!";

		String content = "<p>Hello " + account.getUsername() + "</p>"
			+ "<p>For security reason, you're required to use the following "
			+ "One Time Password to login:</p>"
			+ "<p><b>" + accountOTP.getOtp() + "</b></p>"
			+ "<br>"
			+ "<p>Note: this OTP is set to expire in 5 minutes.</p>";

		helper.setSubject(subject);

		helper.setText(content, true);
		mailSender.send(message);
	}

	@Override
	public void sendConfirmToNewEmail(AccountOTP accountOTP)
			throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(new InternetAddress(emailAddress, "Culinary Connect"));
		Account account = accountOTP.getAccount();

		helper.setTo(accountOTP.getEmail());

		String subject = "Please Confirm Your Email Address";

		String content = "<p>Hello " + account.getUsername() + ",</p>"
				+ "<p>Thank you for registering at our site. Please confirm your email address:</p>"
				+ "<p>OTP code: "+ accountOTP.getOtp()+ "</p>"
				+ "<p>If you did not register, please ignore this email.</p>"
				+ "<br>"
				+ "<p>Thank you!</p>";

		helper.setSubject(subject);

		helper.setText(content, true);
		mailSender.send(message);
	}

	@Override
	public void sendNoticeToOldEmail(AccountOTP accountOTP)
			throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(new InternetAddress(emailAddress, "Culinary Connect"));
		Account account = accountOTP.getAccount();

		helper.setTo(account.getEmail());

		String subject = "Email Address Change Request";

		String content = "<p>Hello " + account.getUsername() + ",</p>"
				+ "<p>We received a request to change the email address associated with your account. If you did not make this request, please ignore this email or contact our support team.</p>"
				+ "<p>If you did initiate this request,  please ignore this email. Else, ask for our customer support for help</p>"
				+ "<br>"
				+ "<p>Thank you!</p>";


		helper.setSubject(subject);

		helper.setText(content, true);
		mailSender.send(message);
	}

}
