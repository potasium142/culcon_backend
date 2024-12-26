package com.culcon.backend.configs.database;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.descriptor.sql.internal.NamedNativeEnumDdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

public class H2PostgresqlDialect extends H2Dialect {
	@Override
	protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.registerColumnTypes(typeContributions, serviceRegistry);
		DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
		ddlTypeRegistry.addDescriptor(new NamedNativeEnumDdlTypeImpl(this));
	}
}
