plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.5.8"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.orumi"
version = "0.0.1-SNAPSHOT"
description = "pelongpelong backend"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// AWS SDK v2 - DynamoDB + Enhanced Client
	implementation(platform("software.amazon.awssdk:bom:2.28.17"))
	implementation("software.amazon.awssdk:sagemakerruntime")
	implementation("software.amazon.awssdk:auth")
	implementation("software.amazon.awssdk:dynamodb")
	implementation("software.amazon.awssdk:dynamodb-enhanced")
	implementation("software.amazon.awssdk:bedrockruntime")
	implementation("org.postgresql:postgresql")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
	implementation("org.apache.commons:commons-lang3:3.18.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

// Conflict with Enhanced DynamoDb class loader
//	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation(platform("io.kotest:kotest-bom:5.9.1"))
	testImplementation("io.kotest:kotest-runner-junit5")
	testImplementation("io.kotest:kotest-assertions-core")
	testImplementation("io.kotest:kotest-property")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
	testImplementation("io.mockk:mockk:1.13.14")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.mockito")
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
