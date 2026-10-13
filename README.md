# CAS.AI Mobile Ads for Kotlin Multiplatform
[![maven](https://img.shields.io/maven-central/v/com.cleveradssolutions/cas-kmp)](https://github.com/cleveradssolutions/CAS-KMP/releases)
![Platform](https://img.shields.io/badge/platform-Android%20%7C%20iOS-blue.svg)
[![App-ads.txt](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/cleveradssolutions/App-ads.txt/master/Shield.json)](https://github.com/cleveradssolutions/App-ads.txt)

The CAS.AI Mobile Ads SDK (by CleverAdsSolutions) offers advanced mobile advertising capabilities with refined ad formats and streamlined APIs to access multiple ad networks and maximize monetization in mobile apps.

This repository contains the official Kotlin Multiplatform plugin for CAS.AI, allowing developers to declare ads once in `commonMain` and monetize both the Android and the iOS side of their app. Banner and Native ads are also available as Compose Multiplatform composables.

## Documentation
For setup instructions, usage examples, and API details, refer to the [Developer Guide](https://docs.page/cleveradssolutions/docs/KMP).

## Downloads
Visit [Maven Central](https://central.sonatype.com/artifact/com.cleveradssolutions/cas-kmp) to get the latest version of the plugin.

```kotlin
commonMain.dependencies {
    implementation("com.cleveradssolutions:cas-kmp:<version>")
}
```

## Sample application
A full sample application with a screen per ad format is available in the [cas-demo-app](cas-demo-app) module, with the iOS entry point in [iosApp](iosApp).

To get started with the sample app, follow the instructions below:
1. Clone or download ZIP of the repository.
2. Open the repository root in Android Studio as a project.
3. For Android, run the `cas-demo-app` configuration.
4. For iOS, run `./gradlew :cas-demo-app:generateDummyFramework`, then `pod install` inside `iosApp`, then open `iosApp/iosApp.xcworkspace` in Xcode.

## Feedback & Contributions
Found a bug? Have a feature request or suggestion?  
Please open an issue using [GitHub's Issue Tracker](https://github.com/cleveradssolutions/CAS-KMP/issues).

## Native SDK Repositories
This plugin is built on top of the official native CAS.AI SDKs:
- [CAS.AI Android SDK (GitHub)](https://github.com/cleveradssolutions/CAS-Android)
  Native Android SDK for Java and Kotlin applications.
- [CAS.AI iOS SDK (GitHub)](https://github.com/cleveradssolutions/CAS-iOS)
  Native iOS SDK for Swift and Objective-C applications.

Refer to these repositories to follow the latest releases and changelogs, or to report platform-specific issues.

## License
The CAS.AI Kotlin Multiplatform plugin is available under the Apache License, Version 2.0. See the
[LICENSE](LICENSE) file for more info.

The CAS.AI SDKs it builds on are distributed under their own
[commercial license](https://github.com/cleveradssolutions/CAS-Android/blob/master/LICENSE.md).
