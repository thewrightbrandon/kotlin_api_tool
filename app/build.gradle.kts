plugins {
    // Specify the Kotlin version
    kotlin("jvm") version "2.0.20"
}

repositories {
    // Use Maven Central to fetch dependencies
    mavenCentral()
}

dependencies {
    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    // Retrofit for API integration
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson converter for Retrofit. convert JSON into Kotlin objects
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("io.ktor:ktor-client-core:2.0.0")
    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("io.ktor:ktor-client-json:2.0.0")
    implementation("io.ktor:ktor-client-logging:2.0.0")

}

// Specify the Java toolchain
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Set the Java version to use
    }
}

