plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.yakovskij.yspy"
    compileSdk = 30

    defaultConfig {
        applicationId = "com.yakovskij.yspy"
        minSdk = 26
        targetSdk = 30
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    lintOptions {
        disable("ExpiredTargetSdkVersion")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    dependencies {
        implementation("androidx.appcompat:appcompat:1.2.0")
        implementation("com.google.android.material:material:1.3.0")
        implementation("androidx.activity:activity:1.2.0")
        implementation("androidx.constraintlayout:constraintlayout:2.0.4")
        implementation("com.squareup.okhttp3:okhttp:4.9.3")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.2")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    }

}