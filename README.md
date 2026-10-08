# Moil-Android-V1

모일은 그룹을 만들거나 참여하고, 그룹 캘린더에서 일정을 확인·관리하는 Android 앱입니다. 이 설명은 현재 앱 코드와 별도로 제공된 API 기능 명세를 바탕으로 합니다. 제품의 공식 목표와 전체 범위는 PRD가 없어 **확인 필요**입니다.

## 현재 코드에서 확인되는 기능

- 이메일 회원가입·로그인, 인증, 비밀번호 재설정과 소셜 로그인 연동 코드
- 그룹 생성·초대 코드 확인·가입, 그룹 정보·멤버·권한 관리
- 그룹별 월간 캘린더와 일정 조회·생성·수정·삭제
- 프로필 편집, 프로필 이미지 업로드, 다크 모드 설정

화면 또는 API 코드의 존재가 각 기능의 실제 서버 연동이나 배포 준비 완료를 뜻하지는 않습니다.

## 기술 스택과 환경

| 항목 | 저장소에서 확인한 값 |
| --- | --- |
| 언어·빌드 | Kotlin 2.2.10, Gradle 9.4.1, Android Gradle Plugin 9.2.1, Kotlin DSL, Version Catalog |
| Android | `minSdk 34`, `targetSdk 36`, `compileSdk 37` |
| Gradle 실행 JDK | 21 (`gradle/gradle-daemon-jvm.properties`, CI 설정) |
| UI·탐색 | Jetpack Compose, Material 3, Navigation 3 |
| 의존성 주입·통신 | Hilt, Retrofit, OkHttp, kotlinx.serialization |
| 로컬 설정·이미지 | Preferences DataStore, AndroidX Security Crypto, Coil |

## 시작하기

1. JDK 21과 Android SDK Platform 37을 준비합니다. CI는 Build Tools 37.0.0도 설치합니다.
2. Android Studio에서 저장소를 열거나 루트 디렉터리에서 다음 명령을 실행합니다.

```bash
./gradlew :app:assembleDebug
```

3. Android API 34 이상 기기 또는 에뮬레이터에서 `:app`을 실행합니다.

디버그와 릴리스의 서버 기본 주소는 현재 `app/build.gradle.kts`의 `BuildConfig.BASE_URL`에 정의되어 있습니다. 서버가 필요한 기능을 확인할 때에는 해당 환경에 접속 가능한지 별도로 확인해야 합니다. 개인별 Android SDK 경로는 로컬 `local.properties`에서 관리하며, 실제 로컬 경로나 비밀 값은 README에 기록하지 않습니다.

## 프로젝트 구조

```text
app/                               Android 앱 단일 Gradle 모듈
  src/main/java/com/example/moil/
    core/                          공통 컴포넌트, 네트워크, DI, 설정
    feature/                       auth, calendar, event, family, group,
                                   image, profile, schedule 기능 코드
    navigation/                    인증·메인 화면 탐색 및 딥 링크
    ui/theme/                      Compose 테마
  src/test/                        로컬 단위 테스트
  src/androidTest/                 계측 테스트
gradle/libs.versions.toml          라이브러리·플러그인 버전
.agents/skills/                    프로젝트별 작업 지침
.github/workflows/                 CI 및 저장소 자동화
```

`settings.gradle.kts`에는 `:app`만 등록되어 있습니다. 기능별 디렉터리는 별도 Gradle 모듈이 아닙니다. 기능 코드에는 `view`, `viewmodel`, `module/domain`, `module/data` 구성이 있으며, 공통 네트워크와 DI 설정은 `core`에 있습니다.

## 검사 명령

CI 워크플로 `.github/workflows/ci-build-test.yml`에 다음 명령이 정의되어 있습니다. 이 README 작성 과정에서는 빌드·테스트·Lint를 실행하지 않았습니다.

```bash
./gradlew :app:compileDebugKotlin
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

## 문서와 확인 필요 항목

- 저장소 루트에 PRD가 없고 `docs/` 디렉터리도 없습니다. 공식 제품 목표, 포함·제외 범위와 기능별 완료 기준은 **확인 필요**입니다.
- 별도로 제공된 모일 API 기능 명세는 저장소 밖에 있습니다. README의 기능 설명은 이 명세와 현재 소스에 함께 근거합니다.
- 프로젝트별 작업 규칙은 [`.agents/skills/`](.agents/skills/)를 참고합니다.
