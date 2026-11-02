plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
}

detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    autoCorrect = true

    // Отключить строгие правила
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

val detektHtmlReport = layout.buildDirectory.file("reports/detekt/detekt.html")
val detektXmlReport = layout.buildDirectory.file("reports/detekt/detekt.xml")
val ciVersionCode = providers.environmentVariable("VERSION_CODE").orNull?.toIntOrNull()
val ciVersionName = providers.environmentVariable("VERSION_NAME").orNull

val openDetektReport by tasks.registering(Exec::class) {
    group = "verification"
    description = "Opens the Detekt HTML report when issues are found."

    val htmlFile = detektHtmlReport.get().asFile
    val xmlFile = detektXmlReport.get().asFile
    inputs.files(htmlFile, xmlFile)
    isIgnoreExitValue = true

    onlyIf("Detekt reported issues") {
        htmlFile.isFile &&
            xmlFile.isFile &&
            xmlFile.readText().contains("<error ")
    }

    when {
        System.getProperty("os.name").startsWith("Windows", ignoreCase = true) ->
            commandLine("cmd", "/c", "start", "", htmlFile.absolutePath)
        System.getProperty("os.name").startsWith("Mac", ignoreCase = true) ->
            commandLine("open", htmlFile.absolutePath)
        else -> commandLine("xdg-open", htmlFile.absolutePath)
    }
}

tasks.named("detekt") {
    finalizedBy(openDetektReport)
}

android {
    namespace = "com.example.financeapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.financeapp"
        minSdk = 24
        targetSdk = 37
        versionCode = ciVersionCode ?: 1
        versionName = ciVersionName ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:${libs.versions.detekt.get()}")

    val composeBom = platform(libs.androidx.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
