import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "1.7.10"
}

group = "io.drullar.inventar"
version = "0.1-SNAPSHOT"

val releaseVersion = "1.0.2"

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
    // https://github.com/Wavesonics/compose-multiplatform-file-picker
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    // Charts dependencies
    implementation("org.jfree:jfreechart:1.5.5")
    //Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation(compose.desktop.uiTestJUnit4)

}

tasks.test {
    useJUnit()
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "io.drullar.inventar.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "Inventar"
            packageVersion = releaseVersion

            windows {
                menu = true
                shortcut = true
                console = false
                upgradeUuid = "c9aefd56-26de-4d6a-9f61-f55c8eac9c2f"
                iconFile.set(project.file("src/main/resources/icons/appIcon.ico"))
            }
            modules("java.sql", "jdk.unsupported")
        }
    }
}

sourceSets {
    create("integrationTest") {
        kotlin.srcDirs("src/integrationTest/kotlin")
        resources.srcDirs("src/integrationTest/resources")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(tasks.named("integrationTest"))
}