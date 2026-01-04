# 자유게시판 1차 구현 - Test 단계

작성일: 2026-01-04

## 목표
- CRUD 핵심 시나리오를 MockMvc로 검증 (인증/인가 미적용 버전)

## 적용 파일
- `src/test/java/dev/earlydreamer/kirini/controller/FreeboardControllerTest.java`

## 시나리오
1) 생성 → 조회(조회수 1 확인) → 삭제(삭제 상태=DELETED 확인)
2) 생성 → 수정(title 변경 확인)

## 테스트 설정
- `@ExtendWith(MockitoExtension.class)` + `MockMvcBuilders.standaloneSetup`로 컨트롤러 단위 테스트 (Service는 Mockito stub)
- H2/스프링 컨텍스트 의존 없이 실행
- 임시 헤더로 사용자 정보 전달 (`X-Account-Id`)

## 상태
- `./gradlew.bat test --no-build-cache --warning-mode=all` **성공** (2026-01-04)

## 비고
- 통합 테스트는 추후 `@SpringBootTest`로 별도 작성 예정 (보안/DB 연동 후)
