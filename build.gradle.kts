import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.7"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
	kotlin("kapt") version "1.6.21"
}

group = "my.kopring"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
val qeurydslVersion = "5.0.0"

repositories {
	mavenCentral()
}

// 아래 블록 추가 설정
sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
	kotlin.srcDir("$buildDir/generated/source/kapt/main")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("com.amazonaws:aws-java-sdk-s3:1.12.268")
	implementation("org.apache.tika:tika-core:2.4.1")
	implementation("io.github.microutils:kotlin-logging:2.1.23")
	implementation("org.hashids:hashids:1.0.3")
	implementation("com.esotericsoftware:kryo:5.3.0")
	implementation("com.auth0:java-jwt:4.0.0")
	implementation("com.github.ksuid:ksuid:1.1.1")
	implementation("com.google.guava:guava:31.1-jre")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("org.apache.poi:poi-ooxml:5.0.0")
	implementation("net.logstash.logback:logstash-logback-encoder:7.2")
	implementation("joda-time:joda-time:2.12.0")
	// https://mvnrepository.com/artifact/com.google.code.gson/gson
	implementation("com.google.code.gson:gson:2.10.1")
	// https://mvnrepository.com/artifact/org.json/json
	implementation("org.json:json:20230227")

	// 금칙어 처리 알고리즘 ahocorasick 라이브러리
	implementation("org.ahocorasick:ahocorasick:0.6.3")


	implementation("com.querydsl:querydsl-jpa:$qeurydslVersion")
	kapt("com.querydsl:querydsl-apt:$qeurydslVersion:jpa")
	kapt("org.springframework.boot:spring-boot-configuration-processor")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

noArg {
	annotation("javax.persistence.Entity")
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}