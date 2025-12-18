plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.contabilidad"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.contabilidad"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- DEPENDENCIAS PRINCIPALES DE JETPACK COMPOSE Y ANDROID KTX ---
    implementation(libs.androidx.core.ktx) // Extensiones de Kotlin para las APIs de Android.
    implementation(libs.androidx.lifecycle.runtime.ktx) // Proporciona un ciclo de vida consciente para corutinas.
    implementation(libs.androidx.activity.compose) // Integración de Jetpack Compose en Activities.
    implementation(platform(libs.androidx.compose.bom)) // Bill of Materials: asegura versiones compatibles de las librerías de Compose.
    implementation(libs.androidx.compose.ui) // Componentes fundamentales de la UI de Compose.
    implementation(libs.androidx.compose.ui.graphics) // Clases de gráficos de bajo nivel.
    implementation(libs.androidx.compose.ui.tooling.preview) // Soporte para @Preview en Android Studio.
    implementation(libs.androidx.compose.material3) // Componentes de Material Design 3.

    // --- DEPENDENCIAS AÑADIDAS MANUALMENTE ---
    
    // ViewModel en Compose: Permite integrar ViewModels (`TaskViewModel`) en la UI de Compose de forma sencilla.
    // Es la pieza clave para conectar la Vista (Composables) con el ViewModel en la arquitectura MVVM.
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- DEPENDENCIAS DE TEST ---
    testImplementation(libs.junit) // Framework de testing JUnit 4.
    androidTestImplementation(libs.androidx.junit) // Implementación de JUnit para tests de instrumentación.
    androidTestImplementation(libs.androidx.espresso.core) // Framework para tests de UI.
    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM para tests de Compose.
    androidTestImplementation(libs.androidx.compose.ui.test.junit4) // Reglas de JUnit 4 para tests de UI en Compose.
    debugImplementation(libs.androidx.compose.ui.tooling) // Herramientas de diagnóstico para Compose.
    debugImplementation(libs.androidx.compose.ui.test.manifest) // Manifiesto para tests de Compose.
}