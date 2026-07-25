---
name: android-hilt-di
description: Android 프로젝트에서 Hilt 주입 구조를 추가하거나 수정할 때 사용한다. `@Inject constructor`, `@Binds`, `@Provides`, `@Singleton`, Activity scope, qualifier, ViewModel 주입, binding 누락 오류를 다루는 작업에 적용한다.
---

# Android Hilt Dependency Injection

## 가독성

- 생성자 주입, binding, provider 선언을 한 줄로 압축하지 않는다. 의존성이 둘 이상이면 매개변수마다 줄을 나눈다.

## 기본 원칙

- 직접 생성 가능한 프로젝트 클래스는 `@Inject constructor`를 우선한다.
- Interface → 구현체 연결은 `@Binds`를 우선한다.
- Retrofit, OkHttpClient, ApiService, DataStore, 외부 Builder처럼 직접 생성 규칙이 필요한 타입은 `@Provides`를 사용한다.
- 앱 전체에서 하나만 유지해야 하는 객체에만 `@Singleton`을 사용한다.
- ViewModel은 `@HiltViewModel`과 `@Inject constructor`를 사용한다.
- 임의의 Service Locator 또는 Kotlin `object`로 DI를 우회하지 않는다.

## 선택 기준

### `@Inject constructor`

다음 조건이면 우선 사용한다.

- 생성자를 직접 제어할 수 있는 프로젝트 클래스
- 생성에 특별한 factory 코드가 필요하지 않음
- 구현 타입 자체를 직접 주입해도 됨

```kotlin
class GetProjectsUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
)
```

### `@Binds`

Interface와 구현체를 연결할 때 사용한다.
Module은 `abstract class` 또는 `interface`로 만들고 함수는 abstract로 선언한다.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectBindingModule {

    @Binds
    abstract fun bindProjectRepository(
        implementation: ProjectRepositoryImpl,
    ): ProjectRepository
}
```

다음 binding을 확인한다.

- Repository Interface → RepositoryImpl
- RemoteDataSource Interface → RemoteDataSourceImpl
- 필요하면 local data source Interface → Impl

### `@Provides`

다음 경우에 사용한다.

- 외부 라이브러리 타입
- Builder를 호출해야 하는 객체
- 생성자에 `@Inject`를 붙일 수 없는 타입
- BuildConfig나 qualifier를 이용한 생성 규칙이 필요함

예:

- OkHttpClient
- Retrofit
- ApiService
- Json / Gson
- Room database

## Scope 규칙

Scope는 객체의 실제 필요 수명과 일치시킨다.

| 필요 수명 | 일반적인 선택 |
|---|---|
| 앱 전체에서 하나 | `@Singleton`, `SingletonComponent` |
| Activity 생명주기 | `@ActivityScoped`, `ActivityComponent` |
| Activity-retained | `@ActivityRetainedScoped` |
| ViewModel | `@ViewModelScoped` |
| 별도 공유 필요 없음 | scope 없이 생성 |

`@Singleton`은 편의를 위한 기본값이 아니다.
상태 공유, 연결 유지, cache, 비용이 큰 생성 등 앱 단일 인스턴스 이유가 있어야 한다.

## Context와 qualifier

- Application 수명 객체는 `@ApplicationContext`만 보관한다.
- Activity UI 작업이 필요한 객체는 Activity 범위와 `@ActivityContext`를 사용한다.
- Activity Context를 Singleton에 주입하지 않는다.
- 같은 타입의 여러 객체가 있으면 의미 있는 qualifier를 사용한다.

## ViewModel

```kotlin
@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val getProjectsUseCase: GetProjectsUseCase,
) : ViewModel()
```

- ViewModel을 Module에서 직접 provide하지 않는다.
- Compose Route에서는 `hiltViewModel()`을 사용한다.
- ViewModel에 Activity, View, NavController, Composable lambda를 저장하지 않는다.

## 작업 절차

1. 주입 대상의 생성자를 제어할 수 있는지 확인한다.
2. Interface binding인지 외부 타입 생성인지 구분한다.
3. 가장 짧은 적절한 scope를 정한다.
4. Context가 있다면 qualifier와 수명 일치를 검사한다.
5. Module의 `@InstallIn` component를 확인한다.
6. 동일 타입 binding 충돌이나 qualifier 누락을 확인한다.
7. Hilt compile을 실행해 graph를 검증한다.

## 완료 체크

- 모든 Interface에 binding이 있다.
- RepositoryImpl과 RemoteDataSourceImpl은 `@Inject constructor`로 생성 가능하다.
- Retrofit 계열은 `@Provides`로 한 곳에서 구성된다.
- 불필요한 `@Singleton`이 없다.
- Application 수명 객체가 화면 객체를 참조하지 않는다.
- ViewModel 생성은 Hilt가 담당한다.
