import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // thêm dòng này
}


val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun String.escapeForBuildConfig(): String =
    "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""

val smtpOtpUser: String = localProps.getProperty("smtp.otp.user", "")
val smtpOtpPassword: String = localProps.getProperty("smtp.otp.password", "")

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
        buildConfigField("String", "SMTP_OTP_USER", smtpOtpUser.escapeForBuildConfig())
        buildConfigField("String", "SMTP_OTP_PASSWORD", smtpOtpPassword.escapeForBuildConfig())
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

    packaging {
        resources {
            pickFirsts += "META-INF/NOTICE.md"
            pickFirsts += "META-INF/LICENSE.md"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.work.runtime)
    // WorkManager
    implementation(libs.work.runtime)

    // Firebase BOM — quản lý version tự động
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database) // Realtime Database

    implementation(libs.flexbox)

    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.legacy.support.v4)
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

    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.work:work-runtime:2.9.0")
}