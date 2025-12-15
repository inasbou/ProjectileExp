import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "1.9.10"

    //for javafx
    id("org.openjfx.javafxplugin") version "0.1.0"//
}

repositories {
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.jzy3d.org/releases")
    maven("https://maven.jzy3d.org/snapshots/")
    maven("https://mvnrepository.com/artifact/org.jzy3d/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    mavenCentral()
}

javafx {
    version = "17"

    modules("javafx.swing", "javafx.web", "javafx.controls")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
                // Your DOCX/export dependencies
                implementation("org.apache.poi:poi-ooxml:5.2.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

                //jzy3d
                val jzy3d = "2.2.1"
                implementation("org.jzy3d:jzy3d-native-jogl-swing:$jzy3d")
                implementation("org.jzy3d:jzy3d-native-jogl-awt:$jzy3d")
                implementation("org.jzy3d:jzy3d-core-awt:$jzy3d")
                implementation("org.jzy3d:jzy3d-core:$jzy3d")
                implementation("org.jzy3d:jzy3d-jdt-core:$jzy3d")

                //Log4j
                val log4jVersion = "2.20.0"
                implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
            }
        }
    }
}



compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {

            modules(
                "java.desktop",
                "java.management",
                "java.base",
                "java.security.jgss",
                "java.xml.crypto",
                "java.compiler",
                "java.instrument",
                "java.sql",
                "jdk.jfr",
                "jdk.unsupported",
                "jdk.unsupported.desktop",
                "jdk.xml.dom"
            )


            targetFormats(
                TargetFormat.Exe,
                TargetFormat.Msi,
                TargetFormat.Dmg,
                TargetFormat.Deb
            )
            modules("java.instrument", "jdk.unsupported")

            // Bundle your JRE
            jvmArgs += listOf("-Djava.awt.headless=false")

            packageName = "Projectile Simulation"
            packageVersion = "1.0.0"
            includeAllModules=true

            windows {
                menuGroup = "Projectile Simulation"
                shortcut = true
            }
        }
    }
}
