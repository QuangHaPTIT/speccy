package com.speccy.speccy.domain.spec.model;

public enum SpecParseStatus {
    PENDING,      // mới tạo, chưa gửi Gemini
    PROCESSING,   // đang gọi Gemini
    DONE,         // có openapi_yaml, sẵn sàng gen test
    FAILED        // Gemini lỗi hoặc YAML không hợp lệ
}
