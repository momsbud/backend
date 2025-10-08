package com.momsbud.backend.shared.jpa.id;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/** Hibernate ID generator that returns a 26-char Crockford Base32 ULID string. */
public class UlidGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return Ulids.newUlid(); // 26-char ULID
    }
}
