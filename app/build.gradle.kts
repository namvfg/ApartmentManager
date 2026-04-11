import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
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
    compileSdk {
        version = release(36)
    }

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
    buildFeatures {
        buildConfig = true
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

    packaging {
        resources {
            pickFirsts += "META-INF/NOTICE.md"
            pickFirsts += "META-INF/LICENSE.md"
        }
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

    implementation(libs.android.mail)
    implementation(libs.android.activation)
}