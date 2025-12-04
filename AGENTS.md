# Agent Guidellines
- 유저의 특별한 요청이 있는 경우를 제외하곤 한글을 사용한다.
- 요청에 대해 파일을 수정, 생성 하기 전에 반드시 사용자의 승인을 받는다.
- 수정, 생성할 파일의 경로를 절대 경로로 명시한다.

## 5단계 리뷰 프로세스
- 다음의 리뷰 방식으로 리뷰를 한다.
- 1단계: 3년차 개발자의 입장에서 리뷰를한다.
- 2단계: 5년차 개발자의 입장에서 리뷰를한다.
- 3단계: 7년차 개발자(팀장)의 입장에서 리뷰를한다.
- 4단계: 10년차 개발자(테크리드)의 입장에서 리뷰를한다.
- 5단계: 아키텍트의 입장에서 리뷰를한다.
- 각 리뷰단계마다 중점적으로 살펴본 사항을 기술하고, 리뷰 결과도 기술한다.
# Repository Guidelines

## 프로젝트 구조와 모듈 구성
- 코틀린 Spring Boot 서비스; Gradle 설정은 `build.gradle.kts`, `settings.gradle.kts`에 있음.
- 애플리케이션 엔트리포인트: `src/main/kotlin/com/orumi/pelongpelong/PelongpelongApplication.kt`.
- 설정/정적 자원: `src/main/resources` (로컬은 `application-<profile>.yaml` 사용, 비밀값 커밋 금지).
- 테스트는 `src/test/kotlin`에서 메인 패키지와 동일한 경로(`com/orumi/pelongpelong`)로 정렬.

## 빌드, 테스트, 개발 명령
- `./gradlew bootRun` — Spring DevTools 포함 로컬 실행.
- `./gradlew test` — JUnit 5 + Spring Boot 테스트 실행.
- `./gradlew build` — 컴파일+테스트 후 `build/libs/`에 실행 아티팩트 생성.
- `./gradlew clean` — 빌드 산출물 정리 시 사용.

## 코딩 스타일 및 네이밍
- 코틀린 관례: 4칸 들여쓰기, 클래스 `PascalCase`, 메서드/필드 `camelCase`, 패키지 소문자.
- Spring 컴포넌트는 생성자 주입 선호; 설정은 `@Configuration` 클래스에 분리.
- null 명시, `!!` 지양; payload/응답은 data class 사용.
- 로그는 Spring 구조화 로깅 사용, 문자열 연결 대신 컨텍스트 키 추가.

## 테스트 가이드
- 프레임워크: JUnit 5, `spring-boot-starter-test`. 테스트는 `src/test/kotlin`에 위치하며 동일 패키지 사용.
- 클래스명은 `*Test` (예: `UserServiceTest`), 메서드는 의미 드러나는 이름(`returns401WhenTokenMissing` 등).
- 단위/슬라이스 테스트(web/직렬화)를 우선 사용하고, 전체 컨텍스트는 필요 시에만 `@SpringBootTest`.
- PR 전 `./gradlew test` 필수; null/빈 입력, 인증 실패 등 경계 케이스 검증.

## 커밋 및 PR 가이드
- 커밋 메시지는 현재형 짧은 요약 사용(예: `add auth filter`); 스코프는 작게 유지.
- PR: 무엇이/왜 변경되었는지, 테스트 방법을 명시. 이슈 링크, 관련 로그나 API 응답 스크린샷 첨부, 깨지는 변경이나 신규 env var 있으면 강조.

## 보안 및 설정 팁
- 비밀값/AWS 자격 증명은 커밋 금지; 환경변수나 로컬 프로필 YAML로 로드.
- 신규 AWS 호출 추가 시 최소 권한 IAM 필요 권한을 PR에 기록.
