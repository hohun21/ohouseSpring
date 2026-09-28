# 담당 영역 Spring Legacy 이관

## 구현 경로

| 기능 | URL | 구성 |
| --- | --- | --- |
| 일반 회원가입/중복 검사 | `/auth/signup.htm`, `/auth/idcheck.ajax`, `/auth/namecheck.ajax` | `AuthController`, `MemberSignupService`, `MemberAuthMapper` |
| 일반 로그인/상태 검사 | `/auth/login`, `/auth/login.htm`, `/auth/statusCheck.ajax` | Spring Security, `CustomUserDetailsService` |
| 판매자 가입/중복 검사 | `/seller/signup.htm`, `/emailcheck.ajax`, `/brandnamecheck.ajax`, `/businessnumbercheck.ajax`, `/mailordernumbercheck.ajax` | `SellerAuthController`, `SellerAuthService`, `SellerAuthMapper` |
| 판매자 로그인/입점 상태 | `/seller/login.htm`, `/sellerStatusCheck.ajax`, `/seller/sellerSignupStatus.htm` | 판매자 자격 확인 후 `ROLE_SELLER`와 `sellerAuth` 세션 생성 |
| 비밀번호 검사/변경 | `/changePwd.htm`, `/checkCurrentPwd.ajax`, `/changePwdPro.htm` | 로그인 사용자만 가능, 변경 후 로그아웃 |
| 로그아웃 | `POST /logout.htm` | Spring Security, 헤더의 CSRF 폼 |
| 베스트/단독 상품 | `/best.htm`, `/only.htm` | `FeaturedProductController`, MyBatis, 기존 JSP |

기존 `/login.htm`과 `/signup.htm`은 새 주소로 리다이렉트합니다. 신규 비밀번호는 BCrypt로 저장하며, 기존 JSP 계정의 평문 비밀번호는 최초로 정상 인증될 때 BCrypt로 바뀝니다. 일반 회원의 비밀번호 변경 시 `persistent_logins` 토큰도 삭제합니다.

## 통합 전 확인

1. 팀 DB의 `member`, `member_authorities`, `cart`, `seller`, `brand`, `product`, `orders_detail`, `product_option`, `review`, `product_image`, `persistent_logins` 테이블 및 `seq_member`, `seq_seller`, `seq_brand` 시퀀스를 확인합니다. `member.password`와 `seller.password`는 BCrypt 해시를 저장할 수 있도록 최소 60자 이상이어야 합니다.
2. `root-context.xml`의 기존 DB 연결 정보를 팀 환경에 맞춥니다.
3. Maven WAR 빌드 후 Tomcat에서 일반/판매자 가입 → 로그인 → 비밀번호 변경 → 재로그인 → 로그아웃과 `/best.htm`, `/only.htm`을 실제 DB로 검증합니다.
4. 다른 팀 화면의 POST 요청은 기존 보안 범위를 유지하기 위해 이관 대상 경로에만 CSRF를 적용했습니다. 팀 통합 시 전체 POST 폼의 CSRF 적용을 별도로 조율하세요.

현재 실행 환경에는 Maven과 DB 연결이 없어 WAR 빌드 및 실제 DB 통합 검증은 수행하지 못했습니다. XML 구문과 Java 매퍼 메서드 ↔ MyBatis SQL ID, 담당 폼의 토큰 연결은 정적 검사로 확인했습니다.
