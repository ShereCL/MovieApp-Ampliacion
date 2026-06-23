plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {

    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.movieapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.movieapp"
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    //Navigation
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")

    //Retrofit y Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    //Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //Cards

    implementation("com.google.android.material:material:1.11.0")

    // libreria para animación de palomitas
    implementation("com.github.gold24park:PopcornView:release-1.0.2")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    // Firebase Auth
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In (Play Services Auth)
    implementation("com.google.android.gms:play-services-auth:21.5.0")

    // Dependencia de Firestore
    implementation("com.google.firebase:firebase-firestore")

    //Material CardView
    implementation("com.google.android.material:material:1.10.0")

    // Retrofit para realizar peticiones HTTP
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp para manejo avanzado de solicitudes HTTP
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Glide para cargar imágenes desde Supabase Storage
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    //Para los gráficos (libreria MPAndroidChart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //Material Desing 3
    implementation("com.google.android.material:material:1.12.0")
}

