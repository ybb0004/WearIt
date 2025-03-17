plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Plugin de Google Services para Firebase
}

android {
    namespace = "com.example.wearit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wearit"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Firebase BoM (Bill of Materials) para gestionar versiones
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
<<<<<<< Updated upstream

    // Dependencias de Firebase
    implementation("com.google.firebase:firebase-analytics")         // Firebase Analytics
    implementation("com.google.firebase:firebase-database")         // Firebase Realtime Database
    implementation("com.google.firebase:firebase-auth")            // Firebase Authentication

    // Glide para manejo de imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // OkHttp y Gson para solicitudes HTTP y manejo de JSON
    implementation("com.squareup.okhttp3:okhttp:4.9.3")             // OkHttp
    implementation("com.google.code.gson:gson:2.8.9")               // Gson

    // Dependencias de AndroidX
=======
    implementation("com.google.firebase:firebase-auth") // Autenticación de Firebase


    implementation("com.google.firebase:firebase-analytics")
    //
    implementation ("androidx.transition:transition:1.5.0")
    implementation ("androidx.core:core:1.12.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
>>>>>>> Stashed changes
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.8.8")
    implementation("androidx.navigation:navigation-ui:2.8.8")

    // Dependencias de pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}