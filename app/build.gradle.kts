plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.and.apartmentmanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.and.apartmentmanager"
        minSdk = 24
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
}
dependencies {

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    //Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // LiveData
    implementation(libs.lifecycle.livedata)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}