package com.speccy.speccy.domain.spec.model;

public enum SpecParseStatus {
    PENDING,      // mới tạo, chưa gửi Gemini
    PROCESSING,   // đang gọi Gemini
    DONE,         // có openapi_content, sẵn sàng gen test
    FAILED        // Gemini lỗi hoặc OpenAPI không hợp lệ
}
