package com.culcon.backend.models.etc;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.modelmapper.internal.bytebuddy.utility.RandomString;

import java.io.Serializable;

public class CouponIdGenerator implements IdentifierGenerator {
	@Override
	public Serializable generate(
		SharedSessionContractImplementor session,
		Object obj) {
		return RandomString.make(14);
	}
}
