package com.speccy.speccy.application.constants;

import java.util.Locale;

public final class GlobalConstants {

    public static final String UTC_ZONE_ID = "UTC";
    public static final String ASIA_HO_CHI_MINH_ZONE_ID = "Asia/Ho_Chi_Minh";
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final Locale JAPANESE_LOCALE =
            new Locale.Builder().setLanguage("ja").setRegion("JP").build();

    public static final Locale ENGLISH_LOCALE =
            new Locale.Builder().setLanguage("en").setRegion("US").build();


    private GlobalConstants() {
    }
}
