import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.4.21"
    id("org.jetbrains.dokka") version "1.4.30"
}

group = "me.jankl"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation("com.hivemq:hivemq-mqtt-client:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.30")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "13"
}