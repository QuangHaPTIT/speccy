# Speccy Bounded Context Map (aligned to schema)

## 1) identity
- Aggregate Roots:
  - `User`
  - `ApiKey`
- Entities:
  - `Permission`
  - `RolePermission`
  - `RolePermissionId` (composite key)
- Shared Enum in this context:
  - `ProjectRole`

## 2) project
- Aggregate Roots:
  - `Project`
  - `Phase`
- Entities:
  - `ProjectMember`

## 3) spec
- Aggregate Roots:
  - `Spec`
- Entities:
  - `ApiModule`

## 4) testmanagement
- Aggregate Roots:
  - `TestCase`
  - `TestFlow`
- Entities:
  - `FlowStep`

## 5) execution
- Aggregate Roots:
  - `TestRun`
- Entities:
  - `TestResult`

## 6) environment
- Aggregate Roots:
  - `TargetDatabase`
  - `TestDataProfile`

---

## Notes
- Source-of-truth entities are now in `identity`, `project`, `spec`, `testmanagement`, `execution`, `environment`.
