import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.5.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                val composeVersion = "1.3.0"

                implementation(compose.desktop.currentOs)
                implementation("it.skrape:skrapeit:1.2.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                // Icons
                implementation("androidx.compose.material:material-icons-extended:$composeVersion")
                // Calendar
                implementation("com.maxkeppeler.sheets-compose-dialogs:core:1.0.2")
                implementation("com.maxkeppeler.sheets-compose-dialogs:calendar:1.0.2")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "data-parser"
            packageVersion = "1.0.0"
        }
    }
}
