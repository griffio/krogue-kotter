import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    application
}

group = "griffio.krogue"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.varabyte.kotter:kotter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-XXLanguage:+RangeUntilOperator"
}

application {
    mainClass.set("griffio.krogue.MainKt")
}

kotlin {
    jvmToolchain(11)
}
