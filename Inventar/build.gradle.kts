import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "io.drullar.inventar"
version = "0.1-SNAPSHOT"

dependencies {
    val exposedVersion = "0.54.0"
    val h2Version = "2.2.224"
    val composeNavigationVersion = "2.7.7"
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
    //Other dependencies
    implementation("io.github.classgraph:classgraph:4.8.177")
    //Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

kotlin {
    sourceSets {
        dependencies {
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
        }
    }
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