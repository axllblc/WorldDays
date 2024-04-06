plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.hiltPlugin)
}

android {
    namespace = "com.axllblc.worlddays"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.axllblc.worlddays"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.okHttp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Hilt
    implementation(libs.hiltAndroid)
    annotationProcessor(libs.hiltCompiler)
    // For instrumentation tests
    androidTestImplementation(libs.hiltAndroidTesting)
    androidTestAnnotationProcessor(libs.hiltCompiler)
    // For local unit tests
    testImplementation(libs.hiltAndroidTesting)
    testAnnotationProcessor(libs.hiltCompiler)
}
