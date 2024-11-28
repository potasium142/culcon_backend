package com.culcon.backend.models.etc;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

public class CouponIdGenerator implements IdentifierGenerator {
	@Override
	public Serializable generate(
		SharedSessionContractImplementor session,
		Object obj) {
		int leftLimit = 65; // numeral '0'
		int rightLimit = 90; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1)
			.limit(targetStringLength)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}
}
