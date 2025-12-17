# 헥사고날 아키텍처 패키지 구조 제안

## 제안 구조
- `com.orumi.pelongpelong.domain` — 애그리게이트/값객체, 도메인 서비스, 도메인 이벤트. 외부 의존성 없음.
- `com.orumi.pelongpelong.application` — 유스케이스(애플리케이션 서비스), 트랜잭션 경계. 포트 정의:
  - `port.in` — 들어오는 포트: REST/메시징이 호출할 유스케이스 인터페이스.
  - `port.out` — 나가는 포트: 저장소, AWS, 외부 API 등 인터페이스.
- `com.orumi.pelongpelong.adapter.in` — 들어오는 어댑터: REST 컨트롤러, 요청/응답 DTO, 검증, 보안/예외 핸들러.
- `com.orumi.pelongpelong.adapter.out` — 나가는 어댑터: JPA/쿼리 리포지토리, AWS(S3/SQS 등) 클라이언트, 메시징, 외부 API 클라이언트.
- `com.orumi.pelongpelong.infrastructure` — 공통 기술 설정(Spring Config, 보안, 관측/로깅, ObjectMapper, Bean 등록).
- `com.orumi.pelongpelong.common` — 에러 코드·예외 베이스, 공용 유틸/상수.

## 선택 이유
- 의존 방향 고정: 어댑터 → 포트 → 유스케이스 → 도메인으로 단방향 의존, 도메인이 외부 기술에 끌려가지 않음.
- 테스트 용이성: 포트 인터페이스로 인입/인출 경계가 명확해 Mock/Fake로 유스케이스와 도메인을 독립적으로 검증 가능.
- 기술 교체성: AWS/DB/메시징 구현을 `adapter.out`에 격리해도 유스케이스/도메인 변경 최소화.
- 표현 독립성: REST 요청/응답 DTO가 `adapter.in`에 머물러 도메인 모델 오염 방지, 오류 응답 정책도 한곳에서 관리.
- 단계적 확장: 현재 단일 모듈에서도 적용 가능하며, 필요 시 패키지명을 그대로 멀티모듈(`domain`, `application`, `adapter-in`, `adapter-out`, `infrastructure`)로 승격 가능.
