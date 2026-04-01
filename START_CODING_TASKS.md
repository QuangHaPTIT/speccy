# Spec2Test - Đánh Giá Spec và Task Khởi Động

Last updated: 2026-04-01

## 1) Đánh giá nhanh: spec ổn không?

Đánh giá nhanh: spec rất chi tiết và đủ để bắt đầu code backend MVP.

Điểm mạnh:
- Scope rõ ràng theo module (auth, project, phase, spec, test case, execution, reporting).
- Domain model và lifecycle đã được mô tả đầy đủ.
- Có decision framework cho AI test generation (để giảm output ngẫu nhiên).
- Có roadmap theo giai đoạn MVP -> v1.0 -> v1.5.

Cần chốt lại trước khi code mạnh (để tránh rework):
- Inconsistency 1: `openapiJson/openapiPrevJson` vs `openapiYaml` (đang có cả 2 cách đặt tên trong spec).
- Inconsistency 2: trạng thái draft test case lúc ghi `PENDING`, lúc ghi `DRAFT`.
- Inconsistency 3: Security section nói hạn chế `DELETE` cho teardown query, nhưng example lại dùng `DELETE`.
- Inconsistency 4: Gemini Call #1 được mô tả trong luồng chính, nhưng roadmap lại đẩy xuống v1.5.
- Clarification 1: Quy ước response envelope cho API (có dùng chung format hay trả payload trực tiếp).
- Clarification 2: Quy tắc pagination/filter/sort cho list endpoints.
- Clarification 3: Chiến lược idempotency cho endpoint `POST /api/v1/runs`.

Khuyến nghị: đóng băng các quyết định trên trong 1 file ADR nhỏ trước khi implement endpoint.

## 2) Current codebase readiness snapshot

Đã có:
- Domain entities cho identity, project, spec, testmanagement, execution, environment.
- Nhiều enum đã có converter.
- Security config và exception handler có khung cơ bản.

Chưa hoàn chỉnh:
- Controller layer gần như chưa implement (ví dụ AuthController đang rỗng).
- Application services cho auth đang skeleton.
- Chưa thấy luồng end-to-end: upload spec -> gen test -> accept -> run.

Kết luận: đây là trạng thái tốt để vào implementation theo vertical slice.

## 3) Starter backlog (ưu tiên thực thi)

Mục tiêu: tạo đường dây MVP chạy được từ đầu đến cuối với phạm vi nhỏ nhất.

### P0 - Alignment and architecture guardrails (1-2 ngày)

- [ ] T001 - Chốt naming contract cho Spec fields (`openapi_json` hay `openapi_yaml`) và cập nhật thống nhất toàn codebase.
- [ ] T002 - Chốt duy nhất 1 lifecycle cho test case draft (`DRAFT`), bỏ `PENDING` khỏi docs và payload.
- [ ] T003 - Chốt SQL safety policy cho `dbTeardownQuery` (có cho `DELETE` hay không).
- [ ] T004 - Tạo ADR cho Gemini Call #1 (MVP có hay không), ghi rõ feature flag.
- [ ] T005 - Chốt API response envelope (success/error), error code naming convention.
- [ ] T006 - Tạo checklist Definition of Done cho mỗi endpoint (validation, authz, log, test).

Deliverable P0:
- ADR docs + convention docs được commit.

### P1 - Identity + Project + Phase foundation (3-5 ngày)

- [ ] T101 - Implement `POST /api/v1/auth/register` + `POST /api/v1/auth/login` + JWT issue.
- [ ] T102 - Implement `GET /api/v1/auth/me`.
- [ ] T103 - Hoàn thiện Project CRUD cần thiết cho MVP: list/create/detail/update/archive.
- [ ] T104 - Implement member management có role gate (OWNER/ADMIN/DEVELOPER/VIEWER).
- [ ] T105 - Implement Phase CRUD (create/list/update) + rule block khi project archived.
- [ ] T106 - Unit test domain rule cho Project/Phase (archive, editable status, owner checks).
- [ ] T107 - Integration test cho auth + project + phase APIs.

Deliverable P1:
- User có thể đăng nhập, tạo project, tạo phase qua API.

### P2 - Spec ingestion + OpenAPI diff + module parsing (4-6 ngày)

- [ ] T201 - Implement `POST /api/v1/phases/{id}/specs` để lưu raw spec + openapi source.
- [ ] T202 - Implement OpenAPI parser (swagger-parser): tách tags -> `ApiModule`.
- [ ] T203 - Implement `POST /api/v1/phases/{id}/openapi/diff` (new/modified/unchanged).
- [ ] T204 - Lưu snapshot context (`phase_context_snapshot`) cho audit.
- [ ] T205 - Thêm validation khi OpenAPI không hợp lệ (message rõ để debug).
- [ ] T206 - Integration test: upload openapi -> parse modules -> diff output.

Deliverable P2:
- Có thể upload spec/openapi và nhận được danh sách API module + diff result.

### P3 - AI test generation + review/accept workflow (4-6 ngày)

- [ ] T301 - Tạo Gemini gateway abstraction (provider interface + retry policy).
- [ ] T302 - Build prompt composer theo framework category rules.
- [ ] T303 - Implement response validator (schema, method, endpoint, status range, explainability fields).
- [ ] T304 - Implement `POST /api/v1/specs/{id}/generate` (async job) + poll endpoint.
- [ ] T305 - Persist generated test cases với status `DRAFT`.
- [ ] T306 - Implement `POST /api/v1/test-cases/accept` (single + batch), gồm logic replace/deprecate.
- [ ] T307 - Implement `PUT /api/v1/test-cases/{id}` cho edit trước accept.
- [ ] T308 - Integration test cho luồng generate -> review -> accept.

Deliverable P3:
- Dev có thể sinh draft test case, sửa, accept và quản lý lifecycle.

### P4 - Test run engine MVP (HTTP verify first) (5-7 ngày)

- [ ] T401 - Implement `POST /api/v1/runs` tạo run async (QUEUED -> RUNNING -> DONE/FAILED).
- [ ] T402 - Build collection generator từ accepted test cases.
- [ ] T403 - Integrate Newman runner (ban đầu local process, chưa cần Docker).
- [ ] T404 - Implement verify cấp 1+2: status + response partial/wildcard.
- [ ] T405 - Persist `TestResult` cho từng case, aggregate vào `TestRun`.
- [ ] T406 - Implement `GET /api/v1/runs/{id}` + `/results` + lịch sử run theo phase.
- [ ] T407 - Integration test run happy path + fail path.

Deliverable P4:
- Có thể trigger run và xem kết quả pass/fail cho test case.

### P5 - Environment + DB verify + hardening (5-8 ngày)

- [ ] T501 - Implement TargetDatabase CRUD + test connection endpoint.
- [ ] T502 - Implement TestDataProfile CRUD + placeholder replacement.
- [ ] T503 - Implement DB verify (`SELECT only`) + db seed/teardown policy enforcement.
- [ ] T504 - Encrypt/decrypt database credentials theo env key strategy.
- [ ] T505 - Thêm timeout, retry, structured logs cho runner.
- [ ] T506 - Add WebSocket push realtime cho run progress.
- [ ] T507 - Add CI smoke test pipeline.

Deliverable P5:
- Run có khả năng verify DB và sử dụng profile env.

## 4) First-week executable task list (gợi ý để vào code ngay)

Ngày 1:
- [ ] Hoàn tất T001-T004 (đóng băng các inconsistency trong spec).
- [ ] Tạo package/use-case skeleton cho P1 endpoint.

Ngày 2:
- [ ] Hoàn tất T101-T103.
- [ ] Viết integration test cho auth login + me.

Ngày 3:
- [ ] Hoàn tất T104-T105.
- [ ] Viết integration test cho project + phase.

Ngày 4:
- [ ] Bắt đầu T201-T202 (upload spec + parse api module).

Ngày 5:
- [ ] Hoàn tất T203-T206.
- [ ] Demo luồng: create phase -> upload spec/openapi -> xem diff/module.

## 5) Suggested branch strategy

- `feature/p1-auth-project-phase`
- `feature/p2-spec-openapi-diff`
- `feature/p3-ai-generate-accept`
- `feature/p4-runner-mvp`
- `feature/p5-env-db-verify`

Mỗi branch merge khi đạt:
- All tests green.
- API contract docs update.
- Không thêm debt blocker cho phase sau.
