plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.kenhtao.site.soundssleep"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kenhtao.site.soundssleep"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "5.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("/Users/macminia5/Downloads/test_app/new_app")
            storePassword = "CongLuu090503@#"
            keyAlias = "musicapp"
            keyPassword = "CongLuu090503@#"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
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
    // UI & Core
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Architecture Components
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Network & JSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.google.code.gson:gson:2.10.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Google Ads & Billing
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    implementation("com.android.billingclient:billing:6.1.0")

    // PersistentCookieJar (GitHub)
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")

    //onboarding
    implementation ("me.relex:circleindicator:2.1.6")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}
