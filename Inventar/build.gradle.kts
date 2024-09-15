plugins {
    kotlin("jvm") version "2.0.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:0.54.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.54.0")
    implementation("com.h2database:h2:2.2.224")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}