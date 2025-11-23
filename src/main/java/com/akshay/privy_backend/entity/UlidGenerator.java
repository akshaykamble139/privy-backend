package com.akshay.privy_backend.entity;

import java.util.EnumSet;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
public class UlidGenerator implements BeforeExecutionGenerator {

    private static final long serialVersionUID = 6849967472397624285L;

	@Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }

	@Override
	public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue,
			EventType eventType) {
		return com.github.f4b6a3.ulid.UlidCreator.getUlid().toString();
	}
}
