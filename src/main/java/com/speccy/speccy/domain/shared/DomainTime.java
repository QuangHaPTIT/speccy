package com.speccy.speccy.domain.shared;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class DomainTime {

    private static final ZoneId UTC_ZONE = ZoneOffset.UTC;

    private DomainTime() {
    }

    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(UTC_ZONE);
    }
}
