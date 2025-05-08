plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // ... autres dépendances ...
        classpath("com.google.gms:google-services:4.4.1") // <-- Vérifiez la version, utilisez la dernière stable si nécessaire
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
