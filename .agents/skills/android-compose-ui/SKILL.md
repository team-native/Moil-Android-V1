---
name: android-compose-ui
description: Jetpack Compose 화면, Route, Screen, UiState, UiModel, 공용 Component를 구현하거나 리팩터링할 때 사용한다. Scaffold, collectAsStateWithLifecycle, 단방향 데이터 흐름, Preview, accessibility, inset 처리가 포함된 작업에 적용한다.
---

# Android Compose UI Workflow

## 가독성

- Composable 본문, 조건문, 상태 갱신, 객체 생성은 한 줄로 압축하지 않는다. 인자 또는 UI 요소가 둘 이상이면 줄바꿈과 들여쓰기를 사용한다.

## 기본 구조

```text
Navigation
→ Route
→ Screen
→ Component
```

상태 흐름은 다음과 같이 유지한다.

```text
ViewModel StateFlow
→ Route가 collectAsStateWithLifecycle()
→ Screen에 UiState와 callback 전달
→ 사용자 이벤트 callback
→ Route가 ViewModel 메소드 호출
```

## Screen 단방향 이벤트

Screen에서 발생하는 사용자 의도는 개별 callback을 여러 개 노출하기보다 화면 전용 `ScreenEvent`의 단일 `onEvent` callback으로 Route에 전달한다.

```text
Screen
→ onEvent(ScreenEvent)
→ Route가 when으로 분기
→ ViewModel 호출 / 화면 상태 변경 / 네비게이션
```

- `ScreenEvent`는 Screen과 같은 presentation 패키지에 `sealed interface`로 선언하고, 이벤트가 필요한 값만 포함한다.
- Screen은 `onEvent`만 호출하며 ViewModel, NavController, 다이얼로그 상태를 직접 참조하거나 변경하지 않는다.
- Route는 `onEvent`를 단일 진입점으로 받아 `when`으로 분기한다. ViewModel 호출, 네비게이션 callback 호출, 다이얼로그·bottom sheet 표시 상태 변경은 모두 여기서 수행한다.
- 입력값 변경, 클릭, 확인·취소처럼 사용자의 모든 화면 이벤트는 같은 `ScreenEvent` 흐름에 포함한다. 단, 재사용 가능한 하위 Component의 내부 callback까지 일괄적으로 이벤트 타입으로 바꾸지는 않는다.
- 일회성 효과는 ViewModel의 `SharedFlow` 또는 `Channel`에서 lifecycle-aware 방식으로 Route가 수집하고, Screen 이벤트와 UiState에 섞지 않는다.

예시:

```kotlin
sealed interface ProjectScreenEvent {
    data object BackClicked : ProjectScreenEvent
    data class QueryChanged(val query: String) : ProjectScreenEvent
}

@Composable
fun ProjectRoute(onNavigateBack: () -> Unit) {
    ProjectScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                ProjectScreenEvent.BackClicked -> onNavigateBack()
                is ProjectScreenEvent.QueryChanged -> viewModel.updateQuery(event.query)
            }
        },
    )
}
```

## Route 규칙

- `hiltViewModel()`로 ViewModel을 가져온다.
- `collectAsStateWithLifecycle()`로 UiState를 수집한다.
- ViewModel event 또는 SharedFlow를 lifecycle-aware 방식으로 수집한다.
- 네비게이션 callback을 처리한다.
- 다이얼로그의 표시 여부와 확인·취소 이벤트를 관리하고, 필요한 다이얼로그를 렌더링한다.
- Screen의 `onEvent`를 ViewModel 메소드·네비게이션 callback·Route 상태 변경에 연결한다.
- 실제 배치와 디자인 코드는 최소화한다.
- Repository, UseCase, ApiService를 직접 호출하지 않는다.

예시 구조:

```kotlin
@Composable
fun ProjectRoute(
    onNavigateBack: () -> Unit,
    viewModel: ProjectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProjectScreen(
        uiState = uiState,
        onRetryClick = viewModel::loadProjects,
        onBackClick = onNavigateBack,
    )
}
```

## Screen 규칙

- 상태와 callback만 매개변수로 받는 stateless UI를 우선한다.
- 최상위 컨테이너는 특별한 이유가 없으면 `Scaffold`를 사용한다.
- `innerPadding`을 실제 content에 전달한다.
- 시스템 바 inset을 `Scaffold`와 content에 중복 적용하지 않는다.
- ViewModel, Repository, Context 기반 저장소를 참조하지 않는다.
- 네트워크 요청이나 장기 Coroutine을 직접 시작하지 않는다.

## UiState

로딩, 성공, 빈 결과, 오류를 모호하지 않게 표현한다.
프로젝트 기존 패턴에 따라 단일 data class 또는 sealed hierarchy를 사용한다.

예시 data class:

```kotlin
data class ProjectUiState(
    val isLoading: Boolean = false,
    val projects: List<ProjectUiModel> = emptyList(),
    val errorMessage: String? = null,
)
```

서로 동시에 존재할 수 없는 상태가 많으면 sealed type을 고려한다.

## UiModel과 UI mapper

- Domain Model에 화면 전용 문자열, resource ID, 색상을 넣지 않는다.
- 화면 표시 구조가 Domain과 다르면 UiModel을 만든다.
- 포맷 문자열은 resource와 결합 가능한 구조로 설계한다.
- 서버 동적 값은 `strings.xml`에 넣지 않는다.

## Component 분리 기준

다음 중 하나면 별도 Composable을 고려한다.

- 두 곳 이상에서 재사용
- 독립적인 UI 책임
- 자체 상태나 interaction 규칙
- Preview와 테스트가 유용한 단위

한 줄 Text나 단순 Spacer까지 기계적으로 분리하지 않는다.
기능 전용 component와 앱 전체 공용 component를 패키지로 구분한다.

## Spacer 공백 규칙

- `Spacer`는 앞뒤 UI 코드와 각각 빈 줄 하나로 구분한다.
- 조건문이나 람다 블록 안에서도 같은 규칙을 적용한다.
- `Spacer`가 여러 줄로 작성된 경우에는 호출 전체의 앞뒤에 빈 줄을 둔다.

## Preview

- 실제 서버, Hilt ViewModel, Repository를 연결하지 않는다.
- 샘플 UiState와 callback을 전달한다.
- 로딩, 정상, 빈 결과, 오류 상태 Preview를 필요에 따라 만든다.

## 접근성

- 의미 있는 Image와 Icon에는 적절한 `contentDescription`을 제공한다.
- 장식용 이미지는 `contentDescription = null`을 사용한다.
- 클릭 가능한 영역은 Icon만이 아니라 적절한 touch target을 가진 component로 만든다.
- 텍스트만으로 구분하기 어려운 상태는 semantic 정보를 제공한다.

## 하드코딩 방지

- 고정 문자열: `strings.xml` + `stringResource()`
- 색상: `MaterialTheme.colorScheme`
- 반복 spacing/radius/elevation/duration: 디자인 토큰
- 한 번만 쓰이고 의미가 명확한 값까지 무조건 전역 상수로 만들지 않는다.

## 완료 체크

- Route와 Screen이 분리되어 있다.
- 다이얼로그는 Screen이 아닌 Route에서 관리한다.
- Screen은 ViewModel을 모른다.
- UiState 수집은 lifecycle-aware 방식이다.
- Scaffold padding과 system inset이 중복되지 않는다.
- Preview가 실제 의존성 없이 동작한다.
- 문자열과 색상 하드코딩이 없다.
- accessibility 설명이 필요한 요소가 처리되어 있다.
