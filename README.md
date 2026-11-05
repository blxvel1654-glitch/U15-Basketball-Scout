
name: Build U15 Basketball Scout APK

on:
  workflow_dispatch:
  push:
    branches:
      - main
    paths:
      - 'U15_Basketball_Scout_Android/**'
      - '.github/workflows/build-apk.yml'

permissions:
  contents: read

jobs:
  build:
    name: Build Android APK
    runs-on: ubuntu-latest

    steps:
      - name: Checkout source
        uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3

      - name: Install Android SDK components
        run: |
          sdkmanager --install "platform-tools" "platforms;android-35" "build-tools;35.0.0"

      - name: Set up Gradle
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.9'

      - name: Build APK
        working-directory: U15_Basketball_Scout_Android
        run: gradle --no-daemon assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: U15-Basketball-Scout-APK
          path: U15_Basketball_Scout_Android/app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error
