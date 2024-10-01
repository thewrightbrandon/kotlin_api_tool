plugins {
    // Specify the Kotlin version
    kotlin("jvm") version "2.0.20"
}

repositories {
    // Use Maven Central to fetch dependencies
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okio:okio:3.5.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

// Specify the Java toolchain
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Set the Java version to use
    }
}

