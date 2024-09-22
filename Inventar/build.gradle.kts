plugins {
    kotlin("jvm") version "2.0.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Database dependencies
    implementation("org.jetbrains.exposed:exposed-core:0.54.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.54.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.54.0")
    implementation("com.h2database:h2:2.2.224")
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