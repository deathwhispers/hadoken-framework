---
phase: "01"
phase_slug: "build-environment-code-standards"
created: "2026-05-29"
---

# Validation Architecture — Phase 01

## Wave 0 Tests (Pre-Execution)

| Test ID | Type | Command | Expected |
|---------|------|---------|----------|
| V-01-01 | config | `grep "<java.version>17</java.version>" pom.xml | wc -l` | >= 1 (before) |
| V-01-02 | config | `grep "<maven-compiler-plugin.version>3.12.1</maven-compiler-plugin.version>" pom.xml | wc -l` | >= 1 (before) |

## Wave 1 Tests (Post-Plan 01-01)

| Test ID | Type | Command | Expected |
|---------|------|---------|----------|
| V-01-03 | config | `grep "<java.version>25</java.version>" pom.xml | wc -l` | >= 1 |
| V-01-04 | config | `grep "<maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>" pom.xml | wc -l` | >= 1 |
| V-01-05 | consistency | `diff pom.xml hadoken-dependencies/pom.xml | grep java.version | wc -l` | 0 (一致) |

## Wave 1 Tests (Post-Plan 01-02)

| Test ID | Type | Command | Expected |
|---------|------|---------|----------|
| V-01-06 | file | `test -f .planning/docs/JDK25-CODE-STANDARDS.md && echo "exists"` | "exists" |
| V-01-07 | content | `grep "Virtual Threads" .planning/docs/JDK25-CODE-STANDARDS.md | wc -l` | >= 1 |
| V-01-08 | content | `grep "Pattern Matching" .planning/docs/JDK25-CODE-STANDARDS.md | wc -l` | >= 1 |

## Final Validation

| Test ID | Type | Command | Expected |
|---------|------|---------|----------|
| V-01-FINAL | build | `mvn clean compile -DskipTests 2>&1 | grep "BUILD SUCCESS"` | "BUILD SUCCESS" (需 JDK 25) |