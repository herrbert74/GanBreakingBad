import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra["kotlin_version"] = "1.4.21"
    val kotlinVersion = "1.4.21"
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.29.1-alpha")
        classpath("com.squareup.sqldelight:gradle-plugin:1.4.4")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.2")
    }
}

allprojects {
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        jcenter()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}