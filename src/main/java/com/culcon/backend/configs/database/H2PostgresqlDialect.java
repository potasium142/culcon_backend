package com.culcon.backend.configs.database;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.descriptor.sql.internal.*;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

import static org.hibernate.type.SqlTypes.*;

public class H2PostgresqlDialect extends H2Dialect {
	@Override
	protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.registerColumnTypes(typeContributions, serviceRegistry);
		DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(UUID, "uuid", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(GEOMETRY, "geometry", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(INTERVAL_SECOND, "interval second($p,$s)", this));
		ddlTypeRegistry.addDescriptor(new DdlTypeImpl(JSON, "json", this));
		ddlTypeRegistry.addDescriptor(new NativeEnumDdlTypeImpl(this));
		ddlTypeRegistry.addDescriptor(new NativeOrdinalEnumDdlTypeImpl(this));
		ddlTypeRegistry.addDescriptor(new NamedNativeOrdinalEnumDdlTypeImpl(this));
		ddlTypeRegistry.addDescriptor(new NamedNativeEnumDdlTypeImpl(this));
	}
}
