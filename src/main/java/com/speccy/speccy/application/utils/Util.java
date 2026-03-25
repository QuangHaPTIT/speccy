package com.speccy.speccy.application.utils;

import com.speccy.speccy.application.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

@Slf4j(topic = "APPLICATION")
public final class Util {

    private Util() {
    }

    public static <T> boolean isNotNullOrEmpty(Collection<T> collection) {
        return !isNullOrEmpty(collection);
    }

    public static <K, V> boolean isNullOrEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static String joinList(String[] items, String delimiter) {
        if (items == null || items.length <= 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String item : items) {
            sb.append(delimiter);
            sb.append(item);
        }
        if (StringUtils.isBlank(sb.toString())) {
            return "";
        }
        return sb.substring(1);
    }

    public static LocalDateTime getNowUTC() {
        return getNow(GlobalConstants.UTC_ZONE_ID);
    }

    public static LocalDateTime getNow() {
        return getNow(GlobalConstants.ASIA_HO_CHI_MINH_ZONE_ID);
    }

    public static LocalDateTime getNow(String zoneId) {
        return LocalDateTime.now(ZoneId.of(zoneId));
    }
}
