# SPEC2TEST — Software Requirements Specification

> **Phiên bản:** 2.0  
> **Ngày:** 2026-03-26  
> **Tech stack:** Spring Boot 3 · Vue 3 · MySQL 8 · Gemini API · Newman CLI · Docker  
> **Package:** `com.speccy.speccy`

---

## Mục lục

1. [Tổng quan dự án](#1-tổng-quan-dự-án)
2. [Kiến trúc hệ thống](#2-kiến-trúc-hệ-thống)
3. [Domain Model](#3-domain-model)
4. [Auth & Phân quyền](#4-auth--phân-quyền)
5. [Module: Project & Phase](#5-module-project--phase)
6. [Module: Spec & AI Engine](#6-module-spec--ai-engine)
7. [Module: Test Case Management](#7-module-test-case-management)
8. [Module: Test Flow](#8-module-test-flow)
9. [Module: Environment](#9-module-environment)
10. [Module: Execution (Run Test)](#10-module-execution-run-test)
11. [Module: Reporting](#11-module-reporting)
12. [API Endpoints](#12-api-endpoints)
13. [Bảo mật](#13-bảo-mật)
14. [Xử lý các trường hợp thực tế](#14-xử-lý-các-trường-hợp-thực-tế)
15. [Roadmap](#15-roadmap)

---

## 1. Tổng quan dự án

### 1.1 Vấn đề

| Vấn đề | Hệ quả |
|--------|--------|
| Viết test thủ công mất 20–40% thời gian dev/QA | Bottleneck trong release cycle |
| Test bị lệch spec khi spec thay đổi | Test pass nhưng logic sai |
| Test case rải rác (Postman, Excel, code repo) | Không có nguồn sự thật duy nhất |
| Thiếu test luồng nghiệp vụ end-to-end | Bug xuyên module không bị phát hiện |
| AI đọc code để viết test → Tautological Testing | Test pass dù code sai so với spec |

### 1.2 Giải pháp

**Spec2Test** là platform cho phép:

1. Dev paste tài liệu đặc tả (Spec) vào hệ thống
2. AI (Gemini) sinh test case từ Spec + OpenAPI — **không đọc source code**
3. Dev review, chỉnh sửa, accept test case trên giao diện
4. Hệ thống tự chạy test, verify HTTP response + Database
5. Quản lý test case xuyên nhiều phase (Regression Testing)

### 1.3 Nguyên tắc cốt lõi

**Black Box Testing:** Gemini chỉ được đọc Spec text + OpenAPI schema. Không đọc source code.

> Nếu AI đọc code, nó sẽ viết test theo đúng implementation — dù implementation đó sai. Đây là lỗi Tautological Testing.

---

## 2. Kiến trúc hệ thống

### 2.1 Tech Stack

| Tầng | Công nghệ | Lý do |
|------|-----------|-------|
| Frontend | Vue 3 + Vite + TailwindCSS | Composition API, build nhanh |
| Backend | Spring Boot 3 (Java) | JPA, Spring Security, @Async |
| Database | MySQL 8 | ACID, relational phù hợp cấu trúc chặt |
| AI Engine | Gemini API | Structured output tốt, cost thấp |
| Test Runner | Newman CLI trong Docker | Isolated, không ảnh hưởng server chính |
| Auth | JWT + Spring Security | Stateless, scale tốt |
| Realtime | WebSocket (STOMP) | Push kết quả test về UI realtime |
| Queue | Spring `@Async` + ThreadPool | MVP — nâng lên RabbitMQ khi cần scale |
| File Storage | S3 / MinIO | File CSV test, OpenAPI snapshot |

### 2.2 Package structure (DDD)

```

```

### 2.3 Luồng dữ liệu tổng quan

```
[Dev paste Spec text]
        ↓
[Upload OpenAPI JSON từ /v3/api-docs]
        ↓
[OpenAPI Diff → highlight API mới/sửa]
        ↓
[Dev tick API cần test]
        ↓
[Gemini call #1: rawContent → openapiYaml]  ← chỉ khi dự án chưa có OpenAPI
        ↓
[Gemini call #2: OpenAPI schema + Spec → TestCase drafts (PENDING)]
        ↓
[Dev review, chỉnh sửa, Accept → DB (ACCEPTED)]
        ↓
[Run: Newman trong Docker → HTTP call → DB verify]
        ↓
[WebSocket push → Pass/Fail realtime]
        ↓
[Lưu TestRun + TestResult]
```

---

## 3. Domain Model

### 3.1 Entity map

```
users ──────────────────── project_members ──── projects
                                                    │
                                                  phases
                                                    │
                                                  specs
                                                    │
                                               api_modules
                                                    │
                                              test_cases ──── flow_steps ──── test_flows
                                                    │
                                    ┌───────────────┤
                                    │               │
                               test_runs       test_results
                                    │
                     ┌──────────────┤
                     │              │
              target_databases  test_data_profiles
```

### 3.2 Enum toàn hệ thống

```java
// project

```

> **Convention:** Tất cả enum dùng `AttributeConverter` (không dùng `@Enumerated`). Lý do: kiểm soát được giá trị lưu xuống DB, dễ rename sau này.

---

## 4. Auth & Phân quyền

### 4.1 JWT

Token chỉ chứa thông tin **platform-level**. Role là per-project, tra DB mỗi request.

```json
{
  "sub": "42",
  "email": "dev@company.com",
  "plan": "PRO",
  "exp": 1234567890
}
```

**Lý do không nhúng role vào JWT:** Nếu user bị hạ role hoặc bị remove khỏi project, JWT cũ vẫn còn hiệu lực → security hole. Tra DB đảm bảo real-time enforcement.

### 4.2 ProjectMember entity

```java

```

**Rule khi tạo Project:** Auto INSERT ProjectMember với `role = OWNER` cho người tạo.

### 4.3 Ma trận quyền

| Hành động | OWNER | ADMIN | DEVELOPER | VIEWER |
|-----------|:-----:|:-----:|:---------:|:------:|
| Xóa / archive project | ✅ | ❌ | ❌ | ❌ |
| Thêm / xóa / đổi role member | ✅ | ✅* | ❌ | ❌ |
| Quản lý DB connection | ✅ | ✅ | ❌ | ❌ |
| Tạo Phase, upload OpenAPI | ✅ | ✅ | ✅ | ❌ |
| Generate test case (Gemini) | ✅ | ✅ | ✅ | ❌ |
| Accept / sửa / xóa test case | ✅ | ✅ | ✅ | ❌ |
| Tạo / sửa Test Flow | ✅ | ✅ | ✅ | ❌ |
| Run test / trigger CI | ✅ | ✅ | ✅ | ❌ |
| Xem kết quả, lịch sử run | ✅ | ✅ | ✅ | ✅ |

> *ADMIN không thể đổi role hoặc xóa OWNER. ADMIN chỉ quản lý role thấp hơn mình.

### 4.4 Spring Security implementation

```java
// Custom annotation để check role
@PreAuthorize("@projectSecurity.hasRole(#projectId, 'DEVELOPER')")
public void generateTestCases(Long projectId, ...) { }

@PreAuthorize("@projectSecurity.hasRole(#projectId, 'ADMIN')")
public void addMember(Long projectId, ...) { }

// ProjectSecurityService
```

### 4.5 Các trường hợp biên

| Tình huống | Xử lý |
|-----------|--------|
| User bị remove khỏi project | JWT còn hiệu lực nhưng `findRole()` trả null → 403 ngay |
| OWNER duy nhất muốn rời project | Block — phải transfer OWNER trước |
| ADMIN cố đổi role của OWNER | 403 Forbidden |
| API Key CI/CD | Gắn `project_id` + `role=DEVELOPER` vào api_keys table, resolve như JWT |
| User cùng lúc là OWNER project A, VIEWER project B | JWT không đổi, `projectId` trong request path xác định context |

---

## 5. Module: Project & Phase

### 5.1 Project

**Entity fields quan trọng:**
- `ownerId` — FK đến users, có thể transfer
- `status` — ACTIVE / ARCHIVED / DELETED (không hard delete)

**Domain methods:**
```java
project.archive()     // status → ARCHIVED
project.softDelete()  // status → DELETED
project.isOwner(userId) // check nhanh
```

**Business rules:**
- Archived project: không cho tạo Phase mới, không cho Run test
- Deleted project: ẩn khỏi danh sách, giữ data để audit

### 5.2 Phase

**Entity fields quan trọng:**
- `orderIndex` — thứ tự hiển thị, có thể reorder
- `accumulatedRules` — context tích lũy qua các phase để inject vào Gemini prompt
- `status` — PLANNING → ACTIVE → COMPLETED / CANCELLED

**`accumulatedRules` là gì?**

Field này chứa context tổng hợp từ tất cả phase trước, inject vào Gemini khi gen test:

```
"Phase 1 đã accept test case cho POST /auth/login và GET /api/users.
 Phase 2 đã sửa logic tính discount trong POST /api/orders — test case cũ đã deprecated.
 Phase 3 cần focus vào POST /api/reconciliation và GET /api/soukan."
```

Gemini đọc context này để không gen lại test case trùng lặp với phase trước.

**Domain methods:**
```java
phase.activate()
phase.complete()
phase.cancel()
phase.reorder(newIndex)
phase.isEditable()           // PLANNING hoặc ACTIVE
phase.updateAccumulatedRules(rules)
```

---

## 6. Module: Spec & AI Engine

### 6.1 Spec entity

```java
// Fields cốt lõi
String rawContent        // Text dev paste vào (Markdown / plain text)
String openapiJson       // OpenAPI JSON upload từ /v3/api-docs của dự án
String openapiPrevJson   // OpenAPI phase trước — dùng để diff
SpecParseStatus parseStatus
Integer geminiTokensUsed
```

> **Không có `structuredSpecJson`** — tầng này thừa. Gemini đủ khả năng đọc plain text và sinh OpenAPI trực tiếp.

### 6.2 Luồng AI — 2 lần gọi Gemini

**Call #1 — Chỉ khi dự án chưa có OpenAPI:**
```
Input:  rawContent (Spec text)
Output: openapiYaml (OpenAPI 3.0 chuẩn)
Lưu:    specs.openapi_json
```

**Call #2 — Gen test cases (luôn luôn):**
```
Input:  OpenAPI schema của API được tick + rawContent + accumulatedRules
Output: JSON array TestCase drafts
Lưu:    test_cases với status=DRAFT
```

### 6.3 OpenAPI Diff

Khi Dev upload OpenAPI mới, hệ thống so sánh với `openapiPrevJson`:

| Trạng thái | Badge UI | Hành động |
|-----------|---------|-----------|
| API mới thêm | 🟢 Xanh lá | Tự tick sẵn |
| API bị sửa schema | 🟡 Vàng | Tự tick sẵn |
| API không đổi | ⚪ Xám | Không tick |

> Dev có thể tick thêm API không bị Diff phát hiện (service thay đổi logic ngầm).
> OpenAPI Diff chỉ phát hiện thay đổi bề mặt (endpoint, schema). Thay đổi trong Service layer → Dev phải tự tick.

### 6.4 Cấu trúc Prompt gửi Gemini

| Phần | Nội dung |
|------|---------|
| System instruction | "Bạn là QA engineer. Chỉ đọc Spec và OpenAPI. KHÔNG suy luận từ implementation. Trả về JSON array thuần theo schema sau, không có markdown, không có preamble." |
| OpenAPI context | Schema JSON của các endpoint được tick (method, path, params, requestBody, responses) |
| Spec text | `rawContent` của phase này |
| Accumulated rules | `phase.accumulatedRules` từ các phase trước |
| Decision framework | Bảng luật sinh test theo category (xem 6.6) để Gemini giải thích được *vì sao* từng case được sinh |
| Output schema | Cấu trúc JSON bắt buộc (xem 6.5) |
| Coverage yêu cầu | "Với mỗi endpoint: happy_path, validation_error (400), auth_error (401), not_found (404), edge_cases từ Spec" |

### 6.5 Schema JSON Gemini phải trả về

```json
[
  {
    "name": "string — bắt buộc",
    "method": "GET|POST|PUT|PATCH|DELETE — bắt buộc",
    "endpoint": "string — bắt buộc, phải tồn tại trong OpenAPI",
    "auth_type": "none|bearer|flow_inherit — bắt buộc",
    "test_category": "happy_path|validation_error|auth_error|not_found|edge_case — bắt buộc",
    "request_params_json": "object|null",
    "path_variables_json": "object|null",
    "headers_json": "object|null",
    "request_body_json": "object|null — dùng {{placeholder}} cho data động",
    "file_inputs": "[{field, hint}]|null",
    "expected_status": "integer — bắt buộc",
    "expected_response_json": "object|null — hỗ trợ wildcard",
    "match_mode": "exact|partial|status_only — bắt buộc",
    "db_verify_query": "string PreparedStatement|null — chỉ SELECT",
    "db_verify_params_json": "array|null",
    "db_verify_expected": "array|null",
    "gemini_data_hints": "string|null — gợi ý data cần chuẩn bị",
    "test_rationale": "string — bắt buộc, giải thích vì sao case này phải tồn tại",
    "source_evidence": "string — bắt buộc, trích OpenAPI/Spec làm bằng chứng",
    "failure_implication": "string — bắt buộc, nếu fail thì rủi ro nghiệp vụ là gì"
  }
]
```

**Wildcards trong `expected_response_json`:**

| Wildcard | Ý nghĩa |
|----------|---------|
| `{{ANY_NUMBER}}` | Bất kỳ số nào |
| `{{ANY_STRING}}` | Bất kỳ string nào |
| `{{NOT_NULL}}` | Có giá trị, không được null |
| `{{IGNORE}}` | Bỏ qua field này |

### 6.6 Test Generation Rules (framework quyết định sinh test)

Gemini không được sinh test ngẫu nhiên. Mỗi test case phải map được vào 1 luật rõ ràng:

| Điều kiện | Category sinh ra | Quy tắc số lượng | Lý do tồn tại |
|----------|------------------|------------------|---------------|
| Luôn luôn | `happy_path` | 1 case cơ bản (hoặc N case nếu Spec có N luồng thành công) | Xác nhận API chạy đúng với input hợp lệ |
| Có field `required` trong OpenAPI | `validation_error` | 1 case thiếu field / mỗi required field | Đảm bảo backend validate bắt buộc |
| Có constraint type/format/min/max/pattern | `validation_error` | 1–2 case vi phạm / mỗi constraint | Bắt lỗi bypass validation |
| `auth_type != none` | `auth_error` | 2 case: thiếu token + token sai/hết hạn | Đảm bảo endpoint được bảo vệ |
| Spec có RBAC/permission | `auth_error` | 1 case sai quyền (403) | Đảm bảo phân quyền đúng |
| Endpoint có path variable `{id}` | `not_found` | 1 case ID không tồn tại | Đảm bảo xử lý resource không tồn tại |
| Spec chứa điều kiện “nếu/khi/if” | `edge_case` | 1 case / mỗi điều kiện | Bao phủ nghiệp vụ mà OpenAPI không mô tả |
| Spec có giới hạn số lượng/kích thước | `edge_case` | 2 case boundary (min và max) | Chặn bug tại ngưỡng |

**Explainability bắt buộc cho mỗi case sinh ra:**
- `test_rationale`: Vì sao sinh case này
- `source_evidence`: Dòng bằng chứng từ OpenAPI/Spec
- `failure_implication`: Nếu fail sẽ gây rủi ro gì

### 6.7 Validate JSON từ Gemini

Spring Boot validate trước khi đưa lên UI:

1. Đúng JSON array, không lỗi parse
2. Mỗi object có đủ field bắt buộc
3. `method` hợp lệ (GET/POST/PUT/PATCH/DELETE)
4. `endpoint` tồn tại trong OpenAPI đã upload
5. `expected_status` là integer 100–599
6. `test_category` là một trong 5 giá trị hợp lệ
7. Có đủ 3 field explainability: `test_rationale`, `source_evidence`, `failure_implication` (không rỗng)
8. `db_verify_query` nếu có: chỉ chứa SELECT, không có INSERT/UPDATE/DELETE/DROP

Nếu fail → retry Gemini tối đa 2 lần với prompt bổ sung mô tả lỗi.  
Sau 3 lần fail → trả lỗi cho Dev kèm raw response để debug.

### 6.8 ApiModule

Được parse tự động từ `openapiYaml` bởi Spring Boot (không phải Gemini):

- Spring Boot dùng `swagger-parser` đọc YAML
- Extract các `tags` → tạo `ApiModule` records
- Extract các endpoints → gắn vào đúng module theo tag

```java
public class ApiModule {
    Long specId;
    Long projectId;
    String name;        // Tên tag trong OpenAPI
    String basePath;    // Prefix chung: /api/v1/users
    String description;
    Integer orderIndex;
}
```

### 6.9 Phase Context Snapshot & Impact Analysis

Mục tiêu: Phase mới **hiểu được** những test đã tồn tại ở các phase trước để chỉ sinh **test bổ sung** cho phần bị ảnh hưởng, tránh sinh trùng.

**Bước 1 — Build context từ các phase trước:**

- Lấy toàn bộ `TestCase` **ACCEPTED** trong project, group theo `method + endpoint`.
- Tóm tắt theo endpoint: số lượng case, category, rationale ngắn gọn.

**Bước 2 — Snapshot gửi vào Gemini:**

Nội dung này được lưu lại trong `specs.phase_context_snapshot` để audit/debug.

**Bước 3 — Gemini trả về impact analysis:**

- `UNAFFECTED`: không tạo draft mới.
- `EXTENDED / MODIFIED`: tạo test mới và gợi ý giữ/khai tử test cũ.

Kết quả lưu vào `specs.impact_analysis_json` để hiển thị UI.

**Các field cần lưu trong Spec:**

```java
String phaseContextSnapshot  // Snapshot context gửi Gemini
String impactAnalysisJson    // JSON phân tích ảnh hưởng theo endpoint
```

---

## 7. Module: Test Case Management

### 7.1 TestCase entity — fields đầy đủ

```java
// Identity
Long id, apiModuleId, phaseId, createdBy

// Source & lifecycle
TestCaseSource source      // GEMINI | MANUAL
TestCaseStatus status      // DRAFT → ACCEPTED / DISABLED / DEPRECATED
Long replacedById          // FK self → test_cases.id (khi deprecated)
TestCategory testCategory  // HAPPY_PATH | VALIDATION_ERROR | ...

// Request definition
String method, endpoint
TestCaseAuthType authType
String requestParamsJson, pathVariablesJson, headersJson, requestBodyJson
String fileAttachmentsJson // [{field, s3Path, hint}]

// DB setup
String dbSeedQuery         // PreparedStatement chạy TRƯỚC khi call API
String dbSeedParamsJson
String dbTeardownQuery     // PreparedStatement chạy SAU khi test xong
String dbTeardownParamsJson

// Expected output
Integer expectedStatus
String expectedResponseJson
TestCaseMatchMode matchMode

// DB verify
String dbVerifyQuery       // PreparedStatement — chỉ SELECT
String dbVerifyParamsJson
String dbVerifyExpected

// AI metadata
String geminiDataHints

// Explainability metadata
String testRationale
String sourceEvidence
String failureImplication

Integer orderIndex
```

### 7.2 Vòng đời TestCase

```
DRAFT ──→ ACCEPTED ──→ DISABLED (tạm tắt, không chạy)
            │
            └──→ DEPRECATED ──(replacedById)──→ [TestCase mới ACCEPTED]
```

**Không bao giờ hard delete.** Lý do: `test_results` vẫn cần FK đến `test_case_id` cũ.

### 7.3 Domain methods

```java
// Lifecycle
testCase.accept()
testCase.disable()
testCase.deprecateTo(Long replacedById)

// Update (Application layer KHÔNG set field trực tiếp)
testCase.updateRequest(method, endpoint, authType, headers, params, pathVars, body)
testCase.updateExpected(expectedStatus, expectedResponseJson, matchMode)
testCase.updateDbSeed(seedQuery, seedParams, teardownQuery, teardownParams)
testCase.updateDbVerify(verifyQuery, verifyParams, verifyExpected)
testCase.updateFileAttachments(fileAttachmentsJson)
testCase.updateHints(geminiDataHints)
testCase.updateRationale(testRationale, sourceEvidence, failureImplication)
```

### 7.4 Luồng Accept (khi Dev bấm OK)

```
Vue 3 gửi POST /api/v1/test-cases/accept
    ↓
Spring Boot validate input
    ↓
Tìm API này đã có TestCase ACCEPTED ở phase trước chưa?
    │
    ├── Có → Hỏi Dev: Giữ cả 2 | Thay thế | Chỉ thêm mới
    │         ├── Thay thế: cũ.deprecateTo(mới.id)
    │         ├── Giữ cả 2: không làm gì với cũ
    │         └── Chỉ thêm mới: không làm gì với cũ
    │
    └── Không có → INSERT mới, status=ACCEPTED
```

### 7.5 Xử lý file đính kèm

Vấn đề: Gemini không tạo được file vật lý (CSV, PDF).

Giải pháp:
1. Gemini sinh: `file_inputs: [{field: "soukan_file", hint: "CSV hợp lệ encoding CP932"}]`
2. UI hiển thị hint + nút Upload Test File
3. Dev upload file → S3/MinIO, nhận S3 path
4. `fileAttachmentsJson`: `[{field: "soukan_file", s3Path: "s3://bucket/...", hint: "..."}]`
5. Khi run Newman: download file từ S3, mount vào container, truyền dưới dạng multipart

### 7.6 UI Review — Tab “Lý do” cho từng test case

Mỗi test case trên màn hình Review phải có tab riêng để hiển thị explainability:

- **Tại sao test này được sinh?** → `testRationale`
- **Căn cứ từ Spec/OpenAPI** → `sourceEvidence`
- **Nếu fail nghĩa là gì?** → `failureImplication`

Yêu cầu UX:
1. Cho phép Dev chỉnh sửa 3 field này trước khi bấm Accept.
2. Khi xem lại test sau nhiều tháng vẫn hiểu mục đích tồn tại.
3. Khi Run FAIL, UI kết quả hiển thị `failureImplication` để ưu tiên xử lý bug theo mức độ ảnh hưởng.

---

## 8. Module: Test Flow

### 8.1 TestFlow & FlowStep

```java
// TestFlow
Long phaseId, createdBy
String name               // "Login → Tạo đơn → Thanh toán"
TestFlowStatus status     // DRAFT → ACTIVE → ARCHIVED

// FlowStep
Long flowId, testCaseId
Integer stepOrder
String overrideHeadersJson  // Override riêng cho flow, KHÔNG sửa TestCase gốc
String overrideBodyJson
String overrideParamsJson
String extractField         // "data.token"
String injectToVar          // "FLOW_VAR.token"
String note
OnFailureAction onFailureAction // ABORT_FLOW | SKIP_STEP | CONTINUE
```

**Lý do có `override_*` fields:**  
TestCase gốc có thể dùng token cứng khi chạy độc lập. Khi đưa vào flow, cần `{{FLOW_VAR.token}}` động. Override giúp không làm hỏng TestCase gốc — cùng 1 TestCase dùng được ở nhiều flow với cấu hình khác nhau.

### 8.2 Ví dụ flow phức tạp

```
Step 1: POST /auth/login
        extract: data.token → FLOW_VAR.token
        extract: data.userId → FLOW_VAR.userId
        onFailureAction: ABORT_FLOW

Step 2: POST /api/orders
        override headers: {Authorization: "Bearer {{FLOW_VAR.token}}"}
        override body: {userId: "{{FLOW_VAR.userId}}", productId: "{{product_id}}"}
        extract: data.orderId → FLOW_VAR.orderId
        onFailureAction: ABORT_FLOW

Step 3: POST /api/payments
        override headers: {Authorization: "Bearer {{FLOW_VAR.token}}"}
        override body: {orderId: "{{FLOW_VAR.orderId}}"}
        extract: data.paymentId → FLOW_VAR.paymentId
        onFailureAction: ABORT_FLOW

Step 4: GET /api/orders/{{FLOW_VAR.orderId}}
        override headers: {Authorization: "Bearer {{FLOW_VAR.token}}"}
        db_verify_query: SELECT status FROM orders WHERE id = ?
        db_verify_params: ["{{FLOW_VAR.orderId}}"]
        db_verify_expected: [{"status": "PAID"}]
        onFailureAction: CONTINUE
```

---

## 9. Module: Environment

### 9.1 TargetDatabase

Thông tin kết nối DB test environment của dự án:

```java
Long projectId
String name          // "Local MySQL Dev"
ProjectDbType dbType // MYSQL | POSTGRESQL | MSSQL | ORACLE
String host, Integer port, String databaseName
String username
String passwordEnc   // AES-256, key riêng per-tenant
TargetDatabaseStatus status
LocalDateTime lastTestedAt
```

**Security rules:**
- `passwordEnc` mã hóa AES-256, key lưu trong environment variable (không trong DB)
- Chỉ decrypt khi mở connection
- DB user phải có quyền hạn chế: SELECT, INSERT, UPDATE — không có DROP, TRUNCATE, GRANT
- Mỗi SQL execution có timeout 5 giây

### 9.2 TestDataProfile

Tập hợp biến môi trường dùng chung trong project:

```java
Long projectId
String name          // "local_dev", "staging"
String variablesJson // {"admin_email":"admin@test.com","user_vip_id":"5"}
Boolean isDefault
```

**Mỗi project có nhiều profile.** Ví dụ: một profile cho local, một cho staging.

`variablesJson` example:
```json
{
  "admin_email":    "admin@test.com",
  "admin_password": "Test123!",
  "user_vip_id":    "5",
  "product_id":     "10",
  "dept_code":      "99073"
}
```

Khi chạy Newman, Spring Boot replace tất cả `{{placeholder}}` trong request body/headers bằng giá trị từ profile trước khi inject vào Newman environment file.

**`profile_snapshot_json` trong TestRun:** Lưu snapshot của profile lúc chạy. Lý do: profile có thể thay đổi sau, cần giữ bản gốc để sau 3 tháng xem lại kết quả vẫn biết lúc đó chạy với data gì.

---

## 10. Module: Execution (Run Test)

### 10.1 TestRun entity

```java
Long phaseId
Long triggeredBy        // userId hoặc null nếu từ API key
Long dbConnectionId     // FK → target_databases.id
Long profileId          // FK → test_data_profiles.id (profile gốc)
TestRunType runType     // MODULE | FLOW | PHASE_ALL | MANUAL_SELECT
Long refId              // ID module/flow nếu không phải PHASE_ALL
String targetUrl        // URL server lúc chạy (snapshot)
String profileSnapshotJson // Snapshot profile lúc chạy
TestRunStatus status
Integer total, passed, failed, skipped
LocalDateTime startedAt, finishedAt
```

> **`environmentId` bị loại bỏ** — thay bằng `profileId` (rõ ràng hơn) + `dbConnectionId` (đã có).

### 10.2 Luồng thực thi

```
Dev bấm Run → POST /api/v1/runs
    ↓
Spring Boot tạo TestRun (status=QUEUED)
Trả về run_id ngay lập tức (async)
    ↓
@Async job bắt đầu:
    1. Lấy tất cả TestCase status=ACCEPTED trong phạm vi
    2. Replace {{placeholder}} bằng Profile values
    3. Build Postman Collection JSON
    4. Download file attachments từ S3
    5. Khởi động Docker container Newman CLI
    6. TestRun → status=RUNNING
    ↓
Newman gọi từng API vào targetUrl:
    Trước mỗi call:
        - Chạy dbSeedQuery (nếu có) vào TestDB
    Sau mỗi call:
        - Spring Boot so sánh actual response vs expected
        - Chạy dbVerifyQuery (nếu có) vào TestDB
        - Chạy dbTeardownQuery (nếu có)
        - Tạo TestResult record
        - Push realtime qua WebSocket
    ↓
Kết thúc:
    - Aggregate tổng hợp total/passed/failed/skipped
    - TestRun → status=DONE hoặc FAILED
    - Xóa Docker container
    - Gửi webhook nếu đã cấu hình
```

### 10.3 TestResult entity

```java
Long testRunId, testCaseId
Integer stepOrder       // null nếu test độc lập
Boolean passed          // TRUE chỉ khi CẢ HTTP + response + db_verify đều pass
ExecutionStatus executionStatus // PASSED|FAILED|SKIPPED|ERROR
Integer actualStatus    // HTTP status code thực tế
String actualResponseJson
String dbVerifyActual
Boolean dbVerifyPassed  // Riêng biệt — API 200 nhưng DB sai vẫn FAIL
String extractedVarsJson // FLOW_VAR đã extract — debug flow
String failureReason    // "Expected 201 but got 500"
String errorDetail      // Stack trace / chi tiết kỹ thuật
Integer durationMs
```

### 10.4 Cơ chế verify 3 cấp

| Cấp | Verify | Pass khi |
|-----|--------|----------|
| 1 — Status | So sánh HTTP status | `actual == expected` |
| 2 — Partial match | So khớp từng field, hỗ trợ wildcard | Tất cả field trong `expectedResponseJson` khớp |
| 3 — DB verify | SELECT vào TestDB sau call | Kết quả == `dbVerifyExpected` |

`passed = TRUE` chỉ khi **tất cả** cấp được cấu hình đều pass.

### 10.5 Xử lý lỗi khi chạy

| Tình huống | Hành vi |
|-----------|--------|
| Target URL timeout | `failure_reason = "Connection timeout after 30s"`. Case FAIL, tiếp tục |
| DB test env không kết nối | Bỏ qua DB verify, ghi warning. HTTP result vẫn ghi nhận |
| Flow step N fail, step N+1 phụ thuộc FLOW_VAR | Theo `onFailureAction` của step N |
| `onFailureAction = ABORT_FLOW` | Các step sau SKIP, `failure_reason = "Skipped: step N failed"` |
| `onFailureAction = SKIP_STEP` | Step N SKIP, tiếp tục step N+1 |
| `onFailureAction = CONTINUE` | Step N FAIL, tiếp tục step N+1 bình thường |
| Newman container crash | Toàn bộ run FAILED, ghi log |
| `dbSeedQuery` fail | Case đó FAIL, `failure_reason = "Seed data preparation failed"` |

### 10.6 Quản lý vòng đời nhiều Phase (Regression)

Khi Dev bấm **Run All Phases**, hệ thống lấy tất cả TestCase `status=ACCEPTED` trong project:

```
Phase 1: Login (ACCEPTED) ────────────────────────── chạy
Phase 1: SOUKAN cũ (DEPRECATED) ─────────────────── bỏ qua
Phase 2: Upload CSV (ACCEPTED) ──────────────────── chạy
Phase 3: SOUKAN mới (ACCEPTED) ──────────────────── chạy
Phase 3: GLOVIA (ACCEPTED) ──────────────────────── chạy
```

Nếu code Phase 3 vô tình làm hỏng Login → test Phase 1 đỏ ngay → phát hiện regression trước khi release.

### 10.7 Xử lý Phase mới sửa API cũ

```
Dev Accept TestCase mới cho API đã có test ở phase trước
    ↓
Hệ thống detect: "API /api/soukan đã có 3 TestCase ACCEPTED từ Phase 1"
    ↓
Hỏi Dev:
┌─────────────────────────────────────────────────────┐
│ Chọn cách xử lý test case cũ:                       │
│                                                     │
│ ○ Thay thế — cũ DEPRECATED, thêm mới               │
│   (behavior cũ không còn đúng)                      │
│                                                     │
│ ○ Giữ cả 2 — thêm mới, giữ cũ                      │
│   (cần test cả behavior cũ lẫn mới)                 │
│                                                     │
│ ○ Chỉ thêm mới — không động đến cũ                  │
│   (thêm tính năng, không sửa behavior cũ)           │
└─────────────────────────────────────────────────────┘
```

---

## 11. Module: Reporting

### 11.1 Dashboard TestRun

Hiển thị realtime qua WebSocket (STOMP):

```json
{
  "runId": 123,
  "status": "RUNNING",
  "total": 20,
  "passed": 15,
  "failed": 3,
  "skipped": 2,
  "results": [
    {
      "testCaseId": 45,
      "name": "Login thành công",
      "passed": true,
      "actualStatus": 200,
      "durationMs": 145
    }
  ]
}
```

### 11.2 So sánh giữa các run

Query lịch sử: `GET /api/v1/phases/{id}/runs`

Hiển thị trend: Run 1 → Run 2 → Run 3, xem case nào bắt đầu fail từ run nào.

### 11.3 CI/CD Integration

```yaml
# GitHub Actions example
- name: Run Spec2Test
  run: |
    RUN_ID=$(curl -X POST https://spec2test.app/api/v1/runs/trigger \
      -H "X-API-Key: ${{ secrets.SPEC2TEST_KEY }}" \
      -d '{"phase_id":3,"target_url":"http://staging","run_type":"phase_all"}' \
      | jq -r '.runId')
    
    # Poll until done
    while true; do
      STATUS=$(curl -H "X-API-Key: $KEY" \
        https://spec2test.app/api/v1/runs/$RUN_ID/status | jq -r '.status')
      [ "$STATUS" = "DONE" ] || [ "$STATUS" = "FAILED" ] && break
      sleep 5
    done
    
    FAILED=$(curl ... | jq '.failed')
    [ "$FAILED" -gt 0 ] && exit 1 || exit 0
```

---

## 12. API Endpoints

### 12.1 Auth

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/api/v1/auth/register` | — | Đăng ký |
| POST | `/api/v1/auth/login` | — | Đăng nhập, trả JWT |
| POST | `/api/v1/auth/refresh` | JWT | Làm mới token |
| GET | `/api/v1/auth/me` | JWT | Thông tin user hiện tại |

### 12.2 Projects

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|-------|
| GET | `/api/v1/projects` | — | Danh sách project của user |
| POST | `/api/v1/projects` | — | Tạo project mới |
| GET | `/api/v1/projects/{id}` | VIEWER+ | Chi tiết project |
| PUT | `/api/v1/projects/{id}` | ADMIN+ | Cập nhật info |
| POST | `/api/v1/projects/{id}/archive` | OWNER | Archive project |
| GET | `/api/v1/projects/{id}/members` | VIEWER+ | Danh sách member |
| POST | `/api/v1/projects/{id}/members` | ADMIN+ | Thêm member |
| PUT | `/api/v1/projects/{id}/members/{userId}` | ADMIN+ | Đổi role |
| DELETE | `/api/v1/projects/{id}/members/{userId}` | ADMIN+ | Xóa member |

### 12.3 Phases & Specs

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|-------|
| GET | `/api/v1/projects/{id}/phases` | VIEWER+ | Danh sách phase |
| POST | `/api/v1/projects/{id}/phases` | DEVELOPER+ | Tạo phase |
| PUT | `/api/v1/phases/{id}` | DEVELOPER+ | Cập nhật phase |
| POST | `/api/v1/phases/{id}/specs` | DEVELOPER+ | Upload Spec text + OpenAPI JSON |
| POST | `/api/v1/phases/{id}/openapi/diff` | DEVELOPER+ | Diff OpenAPI mới vs cũ |
| POST | `/api/v1/specs/{id}/generate` | DEVELOPER+ | Gen test cases (async, trả job_id) |
| GET | `/api/v1/specs/{id}/generate/{jobId}` | DEVELOPER+ | Poll trạng thái gen |

### 12.4 Test Cases

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|-------|
| GET | `/api/v1/phases/{id}/test-cases` | VIEWER+ | Danh sách (filter by status, module, category) |
| POST | `/api/v1/test-cases/accept` | DEVELOPER+ | Accept 1 hoặc batch |
| PUT | `/api/v1/test-cases/{id}` | DEVELOPER+ | Sửa test case |
| POST | `/api/v1/test-cases/{id}/disable` | DEVELOPER+ | Disable |
| POST | `/api/v1/test-cases/{id}/files` | DEVELOPER+ | Upload file test đính kèm |
| DELETE | `/api/v1/test-cases/{id}/files/{fileId}` | DEVELOPER+ | Xóa file đính kèm |

### 12.5 Test Flows

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|-------|
| GET | `/api/v1/phases/{id}/flows` | VIEWER+ | Danh sách flow |
| POST | `/api/v1/phases/{id}/flows` | DEVELOPER+ | Tạo flow mới |
| GET | `/api/v1/flows/{id}` | VIEWER+ | Chi tiết flow + steps |
| PUT | `/api/v1/flows/{id}/steps` | DEVELOPER+ | Cập nhật toàn bộ steps (thứ tự, override, extract) |
| POST | `/api/v1/flows/{id}/activate` | DEVELOPER+ | Activate flow |

### 12.6 Environment

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|-------|
| GET | `/api/v1/projects/{id}/databases` | DEVELOPER+ | Danh sách DB connections |
| POST | `/api/v1/projects/{id}/databases` | ADMIN+ | Thêm DB connection |
| PUT | `/api/v1/databases/{id}` | ADMIN+ | Cập nhật |
| POST | `/api/v1/databases/{id}/test` | ADMIN+ | Test connection |
| GET | `/api/v1/projects/{id}/profiles` | DEVELOPER+ | Danh sách profiles |
| POST | `/api/v1/projects/{id}/profiles` | DEVELOPER+ | Tạo profile |
| PUT | `/api/v1/profiles/{id}` | DEVELOPER+ | Cập nhật |
| POST | `/api/v1/profiles/{id}/set-default` | DEVELOPER+ | Đặt làm default |

### 12.7 Runs

| Method | Endpoint | Role | Mô tả |
|--------|----------|------|-------|
| POST | `/api/v1/runs` | DEVELOPER+ | Trigger run (async, trả run_id ngay) |
| GET | `/api/v1/runs/{id}` | VIEWER+ | Chi tiết run |
| GET | `/api/v1/runs/{id}/results` | VIEWER+ | Kết quả từng test case |
| GET | `/api/v1/phases/{id}/runs` | VIEWER+ | Lịch sử run |
| POST | `/api/v1/runs/trigger` | API Key | Trigger từ CI/CD |
| GET | `/api/v1/runs/{id}/status` | API Key | Poll status (CI/CD) |

---

## 13. Bảo mật

### 13.1 Mã hóa DB credentials

```
passwordEnc = AES-256-CBC(password, tenantKey)
tenantKey   = HMAC-SHA256(mastKey, projectId)
masterKey   = environment variable (không lưu DB)
```

Chỉ decrypt khi cần mở connection. Không cache credential sau khi dùng xong.

### 13.2 Giới hạn SQL execution

**`dbSeedQuery` và `dbTeardownQuery`:** Chỉ cho phép INSERT, UPDATE. Từ chối bất kỳ query chứa: `DROP`, `TRUNCATE`, `ALTER`, `CREATE`, `GRANT`, `DELETE` (cân nhắc — nên dùng teardown thay vì DELETE thẳng).

**`dbVerifyQuery`:** Chỉ cho phép SELECT.

**Implement bằng PreparedStatement** — ngăn SQL Injection:
```java
// KHÔNG dùng
jdbcTemplate.execute("SELECT * FROM users WHERE email = '" + email + "'");

// PHẢI dùng
jdbcTemplate.queryForList("SELECT * FROM users WHERE email = ?", email);
```

### 13.3 Newman isolation

- Mỗi TestRun chạy trong Docker container riêng
- Container bị xóa sau khi run xong
- Token/credentials inject qua environment variable của Docker, không lưu trong Postman collection
- Rate limit: tối đa 3 run đồng thời mỗi user (tránh DDoS vô ý)

### 13.4 API Key (CI/CD)

```java
// Lưu
api_keys: { key_hash, key_prefix, project_id, role, expires_at, last_used_at }

// key_hash = bcrypt(apiKey)
// key_prefix = 8 ký tự đầu (hiển thị trong UI để user nhận ra)
// Không lưu key thật — chỉ hash
```

---

## 14. Xử lý các trường hợp thực tế

### Case 1: API upload file CSV

Gemini sinh:
```json
{
  "file_inputs": [{"field": "soukan_file", "hint": "CSV hợp lệ encoding CP932, bộ phận 99073"}]
}
```

Dev chuẩn bị file, upload vào hệ thống. `fileAttachmentsJson` lưu S3 path. Khi run Newman fetch file từ S3, mount vào container.

### Case 2: API phụ thuộc data DB có sẵn

```java
// Gemini gợi ý
geminiDataHints = "Cần order id=123 tồn tại trong DB trước khi test GET /api/orders/123"

// Dev điền
dbSeedQuery = "INSERT INTO orders (id, user_id, status) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE status=?"
dbSeedParamsJson = "[123, 5, \"PENDING\", \"PENDING\"]"
dbTeardownQuery = "DELETE FROM orders WHERE id = ?"
dbTeardownParamsJson = "[123]"
```

### Case 3: Dự án không có OpenAPI (/v3/api-docs)

Chế độ **Spec-only**:
1. Dev paste Spec → Gemini Call #1 sinh OpenAPI draft
2. Dev chỉnh sửa YAML trên Monaco Editor
3. Bấm "Xác nhận OpenAPI" → dùng YAML này cho Gen test
4. Cảnh báo rõ: YAML do AI sinh, Dev có trách nhiệm review kỹ hơn

### Case 4: Gemini hiểu sai Spec

Dev thấy case PENDING có `discount_rate: 0.9` thay vì `0.1`.
→ Sửa trực tiếp trong JSON editor trên tab Expected.
→ Accept → lưu với giá trị đúng.

### Case 5: CI/CD block merge PR

```
flow: Pull Request → trigger Spec2Test → polling status
→ failed > 0 → exit 1 → block merge
→ failed = 0 → exit 0 → allow merge
```

Webhook: Spring Boot POST kết quả về URL của team khi run xong.

---

## 15. Roadmap

### MVP (Tuần 1–6)
**Mục tiêu:** Spec vào → test case ra → chạy được ngay

- [ ] Auth (JWT, register, login)
- [ ] Project CRUD + ProjectMember + phân quyền
- [ ] Phase CRUD
- [ ] Upload Spec text + OpenAPI JSON
- [ ] OpenAPI Diff (highlight API mới/sửa)
- [ ] Gemini gen test cases (Call #2)
- [ ] Review/Accept UI (JSON editor, tabs Input/Expected/DBVerify/Hints)
- [ ] Run test cơ bản (Newman CLI, không Docker)
- [ ] Kết quả Pass/Fail trên UI

### v1.0 (Tuần 7–12)
**Mục tiêu:** Test sâu hơn, luồng nghiệp vụ phức tạp

- [ ] TargetDatabase + DB Verify
- [ ] TestDataProfile + placeholder replace
- [ ] TestFlow + FlowStep (multi-step)
- [ ] WebSocket live results
- [ ] Docker isolation cho Newman
- [ ] File attachment upload (S3/MinIO)
- [ ] Deprecated/replaced_by flow
- [ ] Lịch sử run & so sánh
- [ ] Billing (Stripe)

### v1.5 (Tuần 13–18)
**Mục tiêu:** Enterprise, tích hợp pipeline

- [ ] API Key cho CI/CD
- [ ] Webhook notify
- [ ] Export báo cáo PDF
- [ ] Gemini Call #1 (Spec-only mode, không có OpenAPI sẵn)
- [ ] `accumulatedRules` — context tích lũy qua phase
- [ ] Regression run toàn project
- [ ] OpenAPI Diff nâng cao

---

*Spec2Test SRS v2.0 — 2026-03-26*
