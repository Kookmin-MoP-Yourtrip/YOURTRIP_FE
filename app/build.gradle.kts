plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.yourtrip"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.yourtrip"
        minSdk = 24
//        minSdk = 31
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
//    implementation(libs.material)
    implementation(libs.activity)
//    implementation(libs.constraintlayout)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    //스플래시 스크린
    implementation("androidx.core:core-splashscreen:1.0.1")
//    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // naverMap
    implementation("com.naver.maps:map-sdk:3.23.0")
//    implementation("com.naver.maps:map-sdk:3.19.1")

    //구글 맵
//    implementation("com.google.android.gms:play-services-maps:18.2.0")
//    implementation("com.google.android.libraries.places:places:3.4.0") // 검색 API용


    // Retrofit 추가
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 태그리스트
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // 피드 이미지
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // 피드 로딩용 화면
    implementation("com.facebook.shimmer:shimmer:0.5.0")
}