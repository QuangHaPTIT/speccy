# Backend Rules - Speccy (Spring Boot)

Last updated: 2026-04-01

## 1) Đánh giá nhanh hiện trạng

- Dự án đã có khung DDD rõ ràng theo context: `identity`, `project`, `spec`, `testmanagement`, `execution`, `environment`.
- Build stack ổn cho backend production: Java 21, Spring Boot, Security, Validation, JPA, Flyway, MapStruct, Spotless.
- Một số lớp use case/controller còn skeleton, nên cần khóa chặt quy tắc triển khai để tránh lệch kiến trúc khi bắt đầu code mạnh.
- Cấu hình `spring.jpa.hibernate.ddl-auto=update` hiện phù hợp local bootstrap, nhưng không được xem là chiến lược schema cho staging/prod.

## 2) Quyết định đã chốt (để tránh rework)

Các quyết định dưới đây được xem là source of truth cho backend:

1. Test case draft status dùng duy nhất `DRAFT`.
2. Dữ liệu OpenAPI dùng naming trung tính:
   - `structuredSpecJson` (JSON có cấu trúc, input chính cho AI)
   - `openapiContent` (nội dung raw)
   - `openapiFormat` (`JSON` | `YAML`)
   - Không dùng song song nhiều cặp tên kiểu `openapiJson/openapiPrevJson/openapiYaml`.
3. Gemini Call #1 (raw spec -> openapi) là optional, đặt sau feature flag và mặc định `OFF` ở MVP.
4. `POST /api/v1/runs` phải idempotent theo `Idempotency-Key` header (tối thiểu trong khoảng thời gian TTL cấu hình), và phải persist ở DB (`test_runs.idempotency_key`, unique index).
5. DB teardown policy:
   - Chỉ cho phép lệnh an toàn trong môi trường test.
   - Cấm tuyệt đối lệnh phá hủy schema (`DROP`, `ALTER`, `TRUNCATE`).
   - `DELETE` chỉ hợp lệ khi có `WHERE` rõ ràng và whitelist bảng được phép.
6. Phân tách role model:
   - `roles` + `user_roles`: platform-level role (quyền hệ thống).
   - `project_members.role`: project-level role (quyền trong project).
   - Không trộn 2 scope khi check authz.

## 3) Kiến trúc bắt buộc

### 3.1 Layering

- `interfaces/rest`: chỉ nhận request, validate input, trả response.
- `application/service`: điều phối use case, transaction boundary, gọi domain + repository.
- `domain/*`: business rule và model nghiệp vụ.
- `infrastructure/*`: adapter kỹ thuật (JPA impl, security provider, external gateway).

Không được để controller gọi trực tiếp repository hoặc viết business rule trong controller.

### 3.2 Quy tắc dependency

- Flow phụ thuộc một chiều: `interfaces` -> `application` -> `domain`.
- `infrastructure` được phép phụ thuộc `domain` và được inject vào `application` qua interface repository/gateway.
- Không import class ở tầng `interfaces` vào `domain`.

### 3.3 DDD tactical rules

- Aggregate root là điểm vào duy nhất để thay đổi trạng thái nghiệp vụ.
- Không update trực tiếp entity con từ service khi aggregate đã có method nghiệp vụ tương ứng.
- Mọi business invariant quan trọng phải nằm trong domain method hoặc domain rule chuyên biệt.

## 4) Coding conventions (Java/Spring)

### 4.1 API và DTO

- Base path chuẩn: `/api/v1/...`.
- Request DTO đặt tại `application/model/.../request`.
- Response DTO đặt tại `application/model/.../response`.
- Không trả JPA entity trực tiếp ra API.
- Validation đặt ở DTO bằng Jakarta Validation; lỗi trả qua `GlobalExceptionHandler`.

### 4.2 Error contract

- Chuẩn lỗi:
  - Lỗi đơn: `{ "error": "..." }`
  - Lỗi validation/domain: `{ "errors": [{ "code": "...", "message": "...", "fields": [...] }] }`
- Mã lỗi phải thống nhất pattern `ER-\d{4}`.
- Không hardcode text tiếng Anh trong business path nếu đã có message key.

### 4.3 Transaction và consistency

- Method ghi dữ liệu ở application service phải có `@Transactional`.
- Method đọc phức tạp dùng `@Transactional(readOnly = true)` khi cần nhất quán snapshot.
- Không mở transaction ở controller.
- Không gọi external IO chậm (AI provider, network nặng) trong transaction dài.

### 4.4 JPA và persistence

- Enum trong DB dùng `AttributeConverter`, không dùng `@Enumerated` cho code mới.
- Mọi thay đổi schema đi qua Flyway migration có version rõ ràng.
- Không dựa vào `ddl-auto` để tạo/sửa schema ở staging/prod.
- Tránh quan hệ EAGER mặc định; ưu tiên truy vấn rõ ràng theo use case.
- Ở tầng `infrastructure`, được phép dùng thêm `EntityManager` hoặc `NamedParameterJdbcTemplate` khi repository JPA không phù hợp.
- Use case điển hình: API lấy danh sách, filter/sort động, truy vấn join nhiều bảng, hoặc read-model/report cần tối ưu hiệu năng.
- Quy tắc áp dụng:
   - Viết ở `infrastructure/data` dưới dạng DAO/query service chuyên biệt.
   - Chỉ xử lý data access và mapping DTO/projection, không đặt business rule.
   - Bắt buộc có pagination và whitelist sort field cho endpoint list.

### 4.5 Security

- Endpoint public phải khai báo tường minh trong `SecurityConfig`.
- Các thao tác theo project bắt buộc check role theo `projectId` bằng method security.
- JWT chỉ chứa thông tin platform-level; role theo project luôn tra DB runtime.
- Không dùng `roles/user_roles` để thay thế `project_members.role` trong phân quyền theo project.
- Không log token, password, secret key, thông tin kết nối DB dạng plaintext.

### 4.6 Logging và observability

- Log theo cấu trúc: `traceId`, `projectId`, `phaseId`, `runId` khi có.
- Không dùng `log.error(ex.toString())` cho lỗi không kiểm soát; phải log stacktrace ở nhánh unhandled.
- Tác vụ async/run test phải có log trạng thái chuyển tiếp: `QUEUED -> RUNNING -> DONE/FAILED`.

## 5) Testing standards

### 5.1 Bắt buộc theo loại thay đổi

- Sửa domain rule: thêm/đổi unit test domain.
- Sửa API contract: thêm/đổi integration test controller/service.
- Sửa persistence/query: thêm test repository hoặc integration test truy vấn.

### 5.2 Definition of Done cho 1 endpoint mới

1. Có request/response DTO rõ ràng.
2. Có validation + mapping lỗi chuẩn.
3. Có check authn/authz đúng vai trò.
4. Có transaction boundary đúng chỗ.
5. Có unit/integration test cho happy path + fail path chính.
6. Có migration (nếu đụng schema) và rollback strategy hợp lý.
7. Qua format/lint và test local trước khi mở PR.

## 6) Quy trình làm việc bắt buộc

1. Viết theo vertical slice hoàn chỉnh (controller -> service -> domain -> repository -> test).
2. Mỗi PR chỉ một mục tiêu nghiệp vụ chính, tránh trộn refactor lớn không liên quan.
3. Luôn cập nhật docs khi thay đổi contract hoặc business rule.
4. Nếu gặp xung đột rule/spec, ưu tiên cập nhật file này trước khi code tiếp.

## 7) Lệnh local chuẩn

```bash
./gradlew spotlessApply
./gradlew test
./gradlew bootRun
```

## 8) Anti-pattern bị cấm

- Fat controller (chứa logic nghiệp vụ).
- Service thao tác trực tiếp nhiều aggregate không qua rule rõ ràng.
- Trả stacktrace nội bộ ra API.
- Viết migration phá dữ liệu mà không có cơ chế kiểm soát.
- Gộp nhiều thay đổi không liên quan vào một commit/PR.
