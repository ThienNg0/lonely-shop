plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")

    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.lonelyshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lonelyshop"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8


    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }




}
val nav_version = "2.7.7"
val core_version = "1.13.1"
val appcompat_version = "1.7.0"
val lifecycle_version = "2.8.4"

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Loading Button
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")
    // Circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // ViewPager2 indicator
    implementation("com.tbuonomo:dotsindicator:5.0")
    // Step view
    implementation ("com.github.shuhart:stepview:1.5.1")
    // Android KTX
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //Core
    implementation("androidx.core:core-ktx:$core_version")

    //Compat
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    // For loading and tinting drawables on older versions of the platform
    implementation("androidx.appcompat:appcompat-resources:$appcompat_version")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")
//
    implementation("dev.gitlive:firebase-auth-android:1.12.0")
//
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC.2")


}