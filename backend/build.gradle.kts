import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "2.0.0"
    kotlin("kapt") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.jpa") version "2.0.0"
    kotlin("plugin.lombok") version "2.0.0"
}

group = "com"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

springBoot {
    buildInfo()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_17
    }
}

configurations.all {
    exclude(group = "commons-logging", module = "commons-logging")
}

val swaggerVersion = "2.0.2"
val restAssuredVersion = "5.3.0"
val jjwtVersion = "0.12.5"
val logbackSlackAppenderVersion = "1.4.0"
val cucumberVersion = "7.13.0"
val firebaseVersion = "8.1.0"
val awsS3Version = "2.25.40"
val mockkVersion = "1.13.12"
val springMockkVersion = "4.0.2"
val kotestVersion = "5.9.1"
val kotestExtensionSpringVersion = "1.3.0"
val kotlinLoggingVersion = "7.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${swaggerVersion}")

    // Spring Security
    implementation("org.springframework.security:spring-security-crypto")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.rest-assured:rest-assured:${restAssuredVersion}")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${jjwtVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")

    // Logback Slack Alarm
    implementation("com.github.maricn:logback-slack-appender:${logbackSlackAppenderVersion}")

    // Cucumber
    testImplementation("io.cucumber:cucumber-java:${cucumberVersion}")
    testImplementation("io.cucumber:cucumber-spring:${cucumberVersion}")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:${cucumberVersion}")
    testImplementation("org.junit.platform:junit-platform-suite")

    // Querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // Firebase
    implementation("com.google.firebase:firebase-admin:${firebaseVersion}")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Micrometer
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // AWS S3
    implementation("software.amazon.awssdk:s3:${awsS3Version}")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Querydsl for kotlin
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // Mockk
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-jvm:$mockkVersion")

    // Spring Mockk
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestExtensionSpringVersion")

    // Kotlin logging
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
}

tasks.test {
    useJUnitPlatform()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

kapt {
    keepJavacAnnotationProcessors = true
}