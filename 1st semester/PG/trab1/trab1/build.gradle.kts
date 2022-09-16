import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.pedri"
version = "1.0-SNAPSHOT"

repositories {
    flatDir { dirs("libs") }
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("pt.isel:CanvasLib-jvm:1.0.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}