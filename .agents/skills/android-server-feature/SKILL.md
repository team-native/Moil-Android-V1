---
name: android-server-feature
description: Android에서 새로운 Retrofit 서버 연동 기능을 추가하거나 기존 서버 기능의 전체 호출 흐름을 재구성할 때 사용한다. DTO, ApiService, RemoteDataSource, Repository, UseCase, ViewModel, Compose UI까지 연결하는 작업에서 반드시 적용하며, 단순 UI 수정이나 서버와 무관한 로컬 코드에는 사용하지 않는다.
---

# Android Server Feature Workflow

## 가독성

- API 결과 변환, `when` 성공·실패 분기, DTO·Domain 객체 생성을 한 줄로 압축하지 않는다. 각 계층의 데이터 흐름을 쉽게 검토할 수 있어야 한다.

## 목표

새 서버 기능을 기존 프로젝트 규칙에 맞게 끝까지 연결한다.
계층을 건너뛰지 않고, 서버 형식과 Domain 의미와 화면 상태를 분리한다.

## 구현 전 조사

코드를 만들기 전에 다음을 확인한다.

1. 기능이 들어갈 모듈과 패키지 구조
2. 프로젝트의 공통 결과 타입 예: `Result`, `NetworkResult`, `ApiResult`
3. 공통 `ApiExecutor`의 위치, 입력·반환 타입, 예외 변환 정책
4. Gson 또는 Kotlin Serialization 중 현재 사용 중인 도구
5. Retrofit, OkHttp, Hilt Module, BuildConfig 환경 설정
6. 유사 기능의 DTO, mapper, Repository, UseCase, UiState 패턴
7. local cache나 Store를 함께 사용해야 하는지
8. 장기 수명 객체나 Callback이 추가되는지

장기 수명 객체가 추가되면 구현 전에 `$android-memory-safety`를 먼저 적용한다.

## 필수 데이터 흐름

새 서버 기능은 다음 흐름을 실제 코드에 포함한다.

```text
MainActivity
→ AppNavigation / NavHost
→ Route
→ Screen / UI
→ 사용자 이벤트
→ Route가 이벤트를 ViewModel 메소드에 연결
→ ViewModel
→ UseCase
→ Repository Interface
→ RepositoryImpl
→ RemoteDataSource Interface
→ RemoteDataSourceImpl
→ ApiExecutor
→ ApiService
→ Retrofit
→ OkHttpClient
→ Interceptor
→ BuildConfig Base URL + Endpoint
→ Server
→ JSON Response
→ Response<T> 또는 프로젝트 공통 네트워크 응답 타입
→ DTO
→ Mapper
→ Domain Model
→ UiModel
→ ViewModel
→ UiState
→ Route가 UiState 수집
→ Screen / UI 업데이트
```

기존 공통 추상화가 일부 단계를 이미 포함한다면 새 타입을 중복 생성하지 않는다.
다만 `UseCase`와 `RemoteDataSource Interface / Impl`은 이 저장소 계약상 생략하지 않는다.

## 서버 통신 DataSource 경유 규칙

기능별 원격 서버 통신은 반드시 다음 경로를 따른다.

```text
RepositoryImpl
→ RemoteDataSource Interface
→ RemoteDataSourceImpl
→ ApiExecutor
→ ApiService
```

- `ApiService`는 `RemoteDataSourceImpl`에서만 `ApiExecutor.execute { ... }` 내부로 호출한다.
- `RepositoryImpl`, UseCase, ViewModel, Route, Screen은 `ApiService`를 직접 주입하거나 호출하지 않는다.
- `RepositoryImpl`은 RemoteDataSource만 주입받아 데이터 소스 선택·조합과 DTO → Domain 변환을 수행한다.
- RemoteDataSource는 DTO 또는 프로젝트 공통 `NetworkResult<DTO>`까지만 반환하며 Domain Model과 UI 상태를 만들지 않는다.
- Core 네트워크 기술 인프라(예: 인증 토큰 갱신)는 feature Repository 흐름과 별도일 수 있다. 이 경우에도 호출 위치와 공통 오류 처리 책임을 Core 계층에 한정한다.

## 파일 생성 순서

다음 순서를 기본으로 한다.

1. 기존 패키지와 공통 타입 조사
2. 메모리 누수 사전 검사와 경고
3. Endpoint 및 BuildConfig 환경 확인
4. Domain Model
5. Repository Interface
6. UseCase
7. Request / Response DTO
8. DTO ↔ Domain Mapper
9. ApiService
10. RemoteDataSource Interface
11. RemoteDataSourceImpl
12. RepositoryImpl
13. Hilt `@Binds` 및 `@Provides`
14. UiModel 및 Domain → UI mapper
15. UiState
16. ViewModel
17. Route
18. Screen
19. 기능 전용 또는 공용 Component
20. `strings.xml`, theme, 디자인 토큰
21. 테스트 또는 최소 검증

## 계층별 구현 계약

### Domain Model

- 서버 JSON 필드 구조를 그대로 복사하지 않는다.
- 앱의 업무 의미를 표현한다.
- `Context`, resource ID, 색상, 화면 문구를 넣지 않는다.

### Repository Interface

- Domain 계층에 둔다.
- Repository는 반드시 Interface로 먼저 정의하고, Data 계층의 `RepositoryImpl`이 이를 구현하도록 한다.
- Domain Model 또는 프로젝트 공통 Domain 결과 타입만 반환한다.
- DTO, Retrofit `Response`, ApiService를 노출하지 않는다.

### UseCase

- 사용자 행동 또는 업무 규칙 하나를 담당한다.
- Repository Interface에만 의존한다.
- Android Framework 타입과 Retrofit 타입에 의존하지 않는다.
- 단순 위임이어도 생성한다. 이후 정책과 검증 규칙이 들어갈 확장 지점이다.

### DTO와 Mapper

- Request DTO와 Response DTO를 서버 계약에 맞춰 분리한다.
- Kotlin Serialization을 사용하는 프로젝트에서는 모든 Request DTO와 Response DTO에 `kotlinx.serialization.Serializable`의 `@Serializable`을 붙인다.
- 모든 DTO 프로퍼티에는 `kotlinx.serialization.SerialName`의 `@SerialName`을 기본으로 명시한다. Kotlin 프로퍼티명과 서버 JSON 필드명이 같아도 서버 계약을 드러내기 위해 생략하지 않으며, 다르면 서버 필드명을 정확히 지정한다.
- DTO는 Retrofit Kotlin Serialization Converter가 직렬화·역직렬화하는 전용 타입으로 유지한다.
- `@SerializedName` 또는 `@SerialName`은 JSON 필드명 매핑일 뿐 DTO → Domain 변환이 아니다.
- DTO와 Domain은 별도 타입으로 만들고 명시적인 mapper로 변환한다.
- 한 줄 변환 함수에 불필요하게 긴 주석을 붙이지 않는다.

### ApiService

- HTTP method, endpoint, `@Path`, `@Query`, `@Body`, Header 형식만 정의한다.
- 상태 저장, Domain 변환, 화면 로직을 넣지 않는다.

### RemoteDataSource

- Interface와 Impl을 모두 만든다.
- Impl은 `ApiExecutor.execute { apiService.request(...) }` 형태로 ApiService 호출을 `ApiExecutor`에 위임한다.
- `ApiExecutor`가 이미 HTTP·IO·serialization 오류를 공통 결과로 변환하면 RemoteDataSource에서 같은 오류를 다시 변환하지 않는다.
- Domain Model, UiModel, UiState를 만들지 않는다.
- 예외 변환은 프로젝트 공통 네트워크 정책을 따른다.
- 같은 예외를 RemoteDataSource와 Repository 양쪽에서 중복 변환하지 않는다.

### RepositoryImpl

- Repository Interface를 구현한다.
- Presentation과 UseCase는 `RepositoryImpl`이 아닌 Repository Interface에 의존하고, 구현체는 Hilt binding으로 주입한다.
- ApiService가 아니라 RemoteDataSource를 주입받는다.
- DTO를 Domain으로 변환한다.
- remote/local 선택, cache 정책, 여러 데이터 소스 조합, 저장 순서를 책임진다.
- 화면 표시용 문자열 조합이나 UI 상태 생성은 하지 않는다.

### ViewModel

- UI 이벤트를 받아 UseCase를 호출한다.
- `MutableStateFlow`는 private, 외부에는 읽기 전용 `StateFlow`만 공개한다.
- DTO, ApiService, Retrofit `Response`를 참조하지 않는다.
- Coroutine은 기본적으로 `viewModelScope`에서 시작한다.
- 로딩, 성공, 빈 결과, 오류를 UiState로 분명히 표현한다.

### Route와 Screen

- Route는 `hiltViewModel()`로 ViewModel을 얻는다.
- Route는 `collectAsStateWithLifecycle()`로 UiState를 수집한다.
- Route는 네비게이션과 Screen callback을 ViewModel 메소드에 연결한다.
- Screen은 상태와 callback만 받는 stateless UI를 우선한다.
- Screen에서 네트워크 요청이나 장기 Coroutine을 시작하지 않는다.

## 메소드 주석

새로 만드는 주요 메소드에는 다음 정보를 가능한 범위에서 한국어로 적는다.

- 메소드가 수행하는 일
- 호출되는 시점
- 중요한 매개변수 의미
- 반환값 또는 실패 동작

예시:

```kotlin
// 추천 프로젝트 목록을 서버에서 불러오는 메소드다.
// ViewModel의 새로고침 이벤트에서 호출되며, 성공 시 Domain Model 목록을 반환한다.
suspend operator fun invoke(): List<Project> {
    return projectRepository.getRecommendedProjects()
}
```

## 예외 적용 규칙

기존 코드베이스가 다른 공통 추상화를 강제하는 경우에만 일부 구조를 조정한다.
조정 전 다음을 먼저 설명한다.

1. 어떤 기존 추상화가 같은 책임을 이미 담당하는지
2. 새 타입을 만들면 어떤 중복이 생기는지
3. 필수 데이터 흐름이 실제로 어디에서 충족되는지
4. 테스트와 유지보수에 미치는 영향

## 완료 조건

- UseCase와 RemoteDataSource Interface / Impl이 실제 호출 흐름에 포함되어 있다.
- RemoteDataSourceImpl의 모든 ApiService 호출이 `ApiExecutor.execute { ... }` 내부에서만 이뤄진다.
- RepositoryImpl이 ApiService를 직접 호출하지 않는다.
- UseCase, ViewModel, Route, Screen이 ApiService를 직접 참조하거나 호출하지 않는다.
- ViewModel이 DTO와 Retrofit 타입을 참조하지 않는다.
- DTO → Domain → UiModel 변환 경계가 분명하다.
- Hilt binding이 모두 연결되어 있다.
- 서버 주소와 로그 설정이 feature 코드에 하드코딩되어 있지 않다.
- 화면이 로딩, 성공, 빈 결과, 오류 상태를 표현한다.
- 가능한 검증을 실행하고 결과를 보고한다.
