buildscript {
    repositories {
        google()
        mavenCentral() // Đảm bảo rằng mavenCentral được thêm vào nếu bạn cần các thư viện từ đó

    }
    dependencies {
        val nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50") // Đảm bảo có dòng này
        // Các dependencies khác nếu cần
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    alias(libs.plugins.google.gms.google.services) apply false

}
