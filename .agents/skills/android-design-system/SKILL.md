---
name: android-design-system
description: Compose의 색상, MaterialTheme, Light·Dark·System 테마 모드, strings.xml, spacing, radius, elevation, animation duration 같은 디자인 토큰을 추가하거나 정리할 때 사용한다. feature 내부 하드코딩을 제거하는 작업에도 적용한다.
---

# Android Design System and Resources

## 가독성

- 토큰과 UI 리소스 선언의 객체 생성·조건 분기를 한 줄로 압축하지 않는다.

## 색상 구조

색상 원본과 의미 기반 사용 위치를 분리한다. 색상 값을 담는 별도 `object`를 만들어 화면에서 꺼내 쓰지 않는다. Light/Dark 테마에서 사용할 색상은 역할별 변수로 선언하고, 최상위 theme에서 `ColorScheme`에 매핑한다.

```text
Theme.kt
├─ 역할별 Light/Dark 색상 변수
├─ LightColorScheme / DarkColorScheme 구성
└─ ThemeMode 처리
   ├─ SYSTEM
   ├─ LIGHT
   └─ DARK
```

각 배경·표면·텍스트 역할에는 의미가 드러나는 변수를 만든다. 예를 들어 `lightBackgroundColor`, `darkBackgroundColor`, `lightSurfaceColor`, `darkSurfaceColor`처럼 테마별 역할을 이름에 포함한다. `object AppColors` 또는 `object Colors`에 색상을 모아 직접 참조하는 방식은 사용하지 않는다.

```kotlin
private val lightBackgroundColor = Color(0xFFFFFBFE)
private val darkBackgroundColor = Color(0xFF1C1B1F)

private val LightColorScheme = lightColorScheme(
    background = lightBackgroundColor,
)

private val DarkColorScheme = darkColorScheme(
    background = darkBackgroundColor,
)

@Composable
fun BookOnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BookOnTypography,
        content = content,
    )
}
```

feature 코드는 원본 색상 값이 아니라 semantic slot을 사용한다.

```kotlin
MaterialTheme.colorScheme.primary
MaterialTheme.colorScheme.background
MaterialTheme.colorScheme.surface
MaterialTheme.colorScheme.onSurface
MaterialTheme.colorScheme.error
```

feature 내부에서 `Color(0xFF...)`를 직접 만들지 않는다.
Light와 Dark에서 같은 역할의 색상은 같은 semantic slot에 연결한다.

## Typography 확장

표준 Material 텍스트 역할은 `MaterialTheme.typography`를 사용한다. 앱 전용 역할(`bookMeta`, `badge`, `privacyNotice` 등)이 필요하면 `object`를 화면에서 직접 참조하지 않고 `CompositionLocalProvider`로 제공한다.

```kotlin
@Immutable
data class AppExtraTypography(
    val bookMeta: TextStyle,
)

val LocalAppExtraTypography = staticCompositionLocalOf<AppExtraTypography> {
    error("AppExtraTypography is not provided.")
}

val MaterialTheme.extraTypography: AppExtraTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalAppExtraTypography.current
```

`AppTheme`은 `MaterialTheme`과 같은 범위에서 `LocalAppExtraTypography provides extraTypography`를 제공한다. feature는 `LocalAppExtraTypography.current`를 직접 호출하지 않고, `MaterialTheme.extraTypography.bookMeta`처럼 `MaterialTheme` 확장 프로퍼티를 통해서만 역할 기반 스타일을 사용한다. 폰트는 `Typography`와 앱 전용 typography 모두에 일관되게 적용한다.

표준 `ColorScheme` slot으로 역할을 정확히 표현할 수 없을 때만 커스텀 색상 확장을 만든다. `ColorScheme`에 필드를 직접 추가할 수 없으므로, 다음 구조를 사용한다.

```kotlin
@Immutable
data class AppExtraColors(
    val bookCoverPlaceholder: Color,
)

val LocalAppExtraColors = staticCompositionLocalOf<AppExtraColors> {
    error("AppExtraColors is not provided.")
}

val MaterialTheme.extraColors: AppExtraColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppExtraColors.current
```

`AppTheme`은 Light/Dark에 따라 같은 역할명의 `AppExtraColors` 값을 선택한 뒤, `MaterialTheme`을 감싼 `CompositionLocalProvider`에서 제공한다.

```kotlin
CompositionLocalProvider(
    LocalAppExtraColors provides extraColors,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
```

feature 코드는 `LocalAppExtraColors.current`를 직접 호출하지 않고, `MaterialTheme.extraColors.bookCoverPlaceholder`처럼 `MaterialTheme` 확장 프로퍼티를 통해서만 접근한다. `Light...`나 `Dark...` 색상 원본은 직접 참조하지 않는다. 표준 `MaterialTheme.colorScheme` slot으로 충분하면 커스텀 확장을 만들지 않는다.

## ThemeMode

- `SYSTEM`, `LIGHT`, `DARK`를 명시적으로 모델링한다.
- 사용자 설정은 DataStore 또는 프로젝트의 설정 저장소에서 관리한다.
- 앱 최상위 theme에서 설정 Flow를 구독해 모드를 선택한다.
- feature 화면이 직접 DataStore를 읽지 않는다.

## 문자열

다음은 `strings.xml`에 둔다.

- 버튼 이름
- 제목과 설명
- 접근성 설명
- 오류의 사용자 표시 문구
- 포맷 문자열

다음은 `strings.xml`에 넣지 않는다.

- 서버에서 내려오는 사용자 이름
- 게시물 본문
- 동적 프로젝트 제목
- API 응답의 임의 문자열

동적 값과 고정 문구를 합칠 때 포맷 문자열을 사용한다.

## 디자인 토큰

반복되는 값은 역할 기반 이름으로 관리한다.

```text
AppSpacing
AppRadius
AppElevation
AppAnimationDuration
AppIconSize
```

예:

```kotlin
object AppSpacing {
    val ScreenHorizontal = 20.dp
    val Section = 24.dp
    val Item = 12.dp
}
```

다음은 feature 안에서 반복 하드코딩하지 않는다.

- 화면 좌우 padding
- 카드 radius
- 공통 elevation
- 반복 animation duration
- 공통 icon size

단 한 번만 사용되고 의미가 분명한 값까지 모두 전역 토큰으로 만들지는 않는다.

## 상수 규칙

- compile-time 상수는 가능한 경우 `const val`
- runtime 값은 읽기 전용 `val`
- 네트워크 제한 개수, paging size, timeout은 UI 토큰과 분리
- 상수 이름은 숫자 값이 아니라 의미를 표현

## 작업 절차

1. 현재 theme와 color scheme을 확인한다.
2. 같은 의미의 기존 semantic slot 또는 token이 있는지 찾는다.
3. 역할별 Light/Dark 색상 변수를 선언하고 `ColorScheme`에 매핑한다.
4. feature 하드코딩을 semantic token으로 교체한다.
5. Light, Dark, System에서 대비와 역할 일관성을 확인한다.
6. Preview 또는 screenshot 가능한 범위에서 상태를 검증한다.

## 완료 체크

- feature 코드에 직접 색상 값이 없다.
- 색상 값을 보관·공유하는 `object`가 없고, 역할별 색상 변수가 `ColorScheme`에 매핑되어 있다.
- 고정 UI 문자열이 resource에 있다.
- 동적 서버 값은 resource로 옮기지 않았다.
- 반복 수치가 의미 있는 token으로 관리된다.
- Light와 Dark가 같은 semantic 역할을 유지한다.
- ThemeMode 저장과 구독이 Presentation/Data 경계를 침범하지 않는다.
