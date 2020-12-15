import com.babestudios.ganbb.buildsrc.Libs

plugins {
    id("jacoco")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.squareup.sqldelight")
    id("dagger.hilt.android.plugin")
}
apply {
    plugin("kotlin-android")
    plugin("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.babestudios.ganbb"
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        minSdkVersion(21)
        targetSdkVersion(30)
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        all {
            buildConfigField("String", "GAN_BB_BASE_URL", "\"https://breakingbadapi.com/\"")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isTestCoverageEnabled = true
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    @Suppress("UnstableApiUsage")
    buildFeatures.viewBinding = true

    applicationVariants.all {
        val isTest: Boolean = gradle.startParameter.taskNames.find { it.contains("test") || it.contains("Test") } != null
        if (isTest) {
            apply(plugin = "kotlin-allopen")
            allOpen {
                annotation("com.babestudios.base.annotation.Mockable")
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {

    implementation(Libs.Kotlin.stdLib)
    implementation(Libs.baBeStudiosBase)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.Navigation.ktx)
    implementation(Libs.AndroidX.Navigation.fragment)
    implementation(Libs.Google.material)
    implementation(Libs.AndroidX.Navigation.ktx)
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.SquareUp.OkHttp3.loggingInterceptor)
    implementation(Libs.KotlinResult.result)
    implementation(Libs.KotlinResult.coroutines)
    implementation(Libs.Kotlin.FlowBinding.android)
    implementation(Libs.Kotlin.FlowBinding.material)

    implementation(Libs.Google.gson)
    implementation(Libs.SquareUp.Retrofit2.retrofit)
    implementation(Libs.SquareUp.Retrofit2.converterGson)

    androidTestImplementation(Libs.SquareUp.OkHttp3.loggingInterceptor)

    implementation(Libs.Google.Dagger.dagger)
    kapt(Libs.Google.Dagger.compiler)
    kaptTest(Libs.Google.Dagger.compiler)
    kaptAndroidTest(Libs.Google.Dagger.compiler)

    implementation(Libs.AndroidX.Hilt.viewModel)
    implementation(Libs.Google.Dagger.Hilt.android)
    kapt(Libs.AndroidX.Hilt.compiler)
    kaptTest(Libs.AndroidX.Hilt.compiler)
    kapt(Libs.Google.Dagger.Hilt.androidCompiler)
    kaptTest(Libs.Google.Dagger.Hilt.androidCompiler)
    kaptAndroidTest(Libs.Google.Dagger.Hilt.androidCompiler)
    implementation(Libs.SquareUp.SqlDelight.driver)

    implementation(Libs.Views.Groupie.core)
    implementation(Libs.Views.Groupie.kotlinExtensions)

    implementation(Libs.Views.Glide.core)
    kapt(Libs.Views.Glide.compiler)

    implementation(Libs.Views.Groupie.viewBinding)

    implementation(Libs.Views.FlowBinding.android)
    implementation(Libs.Views.FlowBinding.material)

    implementation(Libs.Javax.inject)
    kapt(Libs.Javax.annotations)

    testImplementation(Libs.AndroidX.Test.core)
    testImplementation(Libs.AndroidX.Test.Ext.jUnit)
    testImplementation(Libs.AndroidX.Arch.Core.testing)
    testImplementation(Libs.Test.mockK)
    testImplementation(Libs.Kotlin.Coroutines.test)
    testImplementation(Libs.Kotest.assertions)

    androidTestImplementation(Libs.Test.mockKAndroidTest)
    androidTestImplementation(Libs.Test.conditionWatcher)
    androidTestImplementation(Libs.AndroidX.Test.Espresso.core)
    androidTestImplementation(Libs.AndroidX.Test.Ext.jUnit)
    androidTestImplementation(Libs.AndroidX.Test.rules)
    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.Google.gson)
    androidTestImplementation(Libs.SquareUp.Retrofit2.retrofit)
    androidTestImplementation(Libs.SquareUp.Retrofit2.rxJava2Adapter)
}
repositories {
    mavenCentral()
}