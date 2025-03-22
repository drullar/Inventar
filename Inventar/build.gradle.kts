import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.0.21"
}

group = "io.drullar.inventar"
version = "0.1-SNAPSHOT"

dependencies {
    val exposedVersion = "0.54.0"
    val h2Version = "2.2.224"
    // Database dependencies
    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-java-time:${exposedVersion}")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    implementation("com.h2database:h2:${h2Version}")
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.ui:ui-tooling:1.6.11")
    implementation("org.jetbrains.compose.material3:material3:1.6.11")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    //Other dependencies
    implementation("io.github.classgraph:classgraph:4.8.177")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    //Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
}

tasks.test {
    useJUnit()
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Inventar"
            packageVersion = "1.0.0"
        }
    }
}