import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	id("org.springframework.boot") version "2.7.16"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	id("org.jetbrains.kotlin.plugin.allopen") version "1.3.72"

	jacoco

	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	kotlin("plugin.jpa") version "1.3.72"
}

jacoco {
	toolVersion = "0.8.5"
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

group = "net.softbell"
version = "1.1.0-ALPHA"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Laungage
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// DB
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.16") // JPA
	implementation("mysql:mysql-connector-java:8.0.29")
	implementation("com.h2database:h2:1.4.200") // H2
	implementation("org.hibernate:hibernate-search-orm:5.11.5.Final") // JPA Search Engine

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security:2.7.16") // Security
	testImplementation("org.springframework.security:spring-security-test:5.4.0") // Security Test
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5:3.0.4.RELEASE") // Thymeleaf Spring Security 5
	implementation("io.jsonwebtoken:jjwt:0.9.1") // JWT
	implementation("org.springframework.security:spring-security-messaging:5.4.0") // Message Security

	// View
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.7.16") // Thymeleaf
	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.3.0") // Thymeleaf Layout

	// API
	implementation("io.springfox:springfox-swagger2:2.6.1") // Swagger
	implementation("io.springfox:springfox-swagger-ui:2.6.1") // Swagger UI

	// Server
	implementation("org.springframework.boot:spring-boot-starter-web:2.7.16") // Web (tomcat)

	// Websocket
	implementation("org.springframework.boot:spring-boot-starter-websocket:2.7.16") // Websocket

	// Library
	implementation("io.github.microutils:kotlin-logging:2.0.3") // Kotlin Logging
	developmentOnly("org.springframework.boot:spring-boot-devtools:2.7.16") // Devtools
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor") // Profile Config Processor
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.16") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine") // Test
	}
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2") // Jackson
	implementation("com.google.code.gson:gson-parent:2.8.6") // Gson
}

tasks.named<BootRun>("bootRun") {
	setupEnvironment()
}

fun BootRun.setupEnvironment() {
	environment("SPRING_PROFILES_ACTIVE", "local")
}

// https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image
tasks.named<BootBuildImage>("bootBuildImage") {
	setupEnvironment(this)
	setupBuildProperty(this)
	setupImageProperty(this)
	setupDocker(this)
}

fun setupEnvironment(bootBuildImage: BootBuildImage) {
	bootBuildImage.run {
		environment = environment + mapOf("SPRING_PROFILES_ACTIVE" to "production")
	}
}

fun setupBuildProperty(bootBuildImage: BootBuildImage) {
	bootBuildImage.run {
		val bindingsDir: String by project
		val gradleDir: String by project

		val bindingVolumes = mutableListOf<String>()

		if (project.hasProperty("bindingsDir")) bindingVolumes.add("$bindingsDir:/platform/bindings:rw")
		if (project.hasProperty("gradleDir")) bindingVolumes.add("$gradleDir:/home/cnb/.gradle:rw")

		bindings = bindingVolumes
		builder = "paketobuildpacks/builder:${paketobuildpacks.versions.builder.get()}"
	}
}

fun setupImageProperty(bootBuildImage: BootBuildImage) {
	bootBuildImage.run {
		val imagePath: String by project
		val imageBaseName: String by project
		val imageTag: String by project

		if (project.hasProperty("imagePath")) imageName = imagePath
		if (project.hasProperty("imageBaseName")) imageName = "${imageName}/$imageBaseName"
		if (project.hasProperty("imageTag")) tags = mutableListOf("${imageName}:$imageTag")
	}
}

fun setupDocker(bootBuildImage: BootBuildImage) {
	bootBuildImage.run {
		val dockerHost: String by project
		val isDockerTlsVerify: String by project
		val dockerCertPath: String by project

		val projectRegistryUrl: String by project
		val registryUser: String by project
		val registryPassword: String by project
		val registryEmail: String by project

		docker {
			if (project.hasProperty("dockerHost")) host = dockerHost
			if (project.hasProperty("isDockerTlsVerify")) isTlsVerify = isDockerTlsVerify.toBoolean()
			if (project.hasProperty("dockerCertPath")) certPath = dockerCertPath

			publishRegistry {
				if (project.hasProperty("projectRegistryUrl")) url = projectRegistryUrl
				if (project.hasProperty("registryUser")) username = registryUser
				if (project.hasProperty("registryPassword")) password = registryPassword
				if (project.hasProperty("registryEmail")) email = registryEmail
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()

	environment("SPRING_PROFILES_ACTIVE", "test")

	finalizedBy("jacocoTestReport")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.jacocoTestReport {
	reports {
		csv.isEnabled = true

		csv.destination = file("$buildDir/jacoco/csv")
	}

//	finalizedBy("jacocoTestCoverageVerification")  // 활성화시 violationRules 통과 실패할경우 테스트도 실패처리 됨
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			// element 가 없으면 프로젝트의 전체 파일을 합친 값 기준

			limit {
				// counter 를 지정하지 않으면 default 는 INSTRUCTION
				// value 를 지정하지 않으면 default 는 COVEREDRATIO
				minimum = "0.30".toBigDecimal()
			}
		}

		rule {
			enabled = true
			element = "CLASS"  // class 단위로 rule check

			// 브랜치 커버리지 최소 90% 만족
			limit {
				counter = "BRANCH"
				value = "COVEREDRATIO"
				minimum = "0.90".toBigDecimal()
			}

			// 라인 커버리지 최소 80% 만족
			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.80".toBigDecimal()
			}

			// 빈 줄을 제외한 코드의 라인수를 최대 200라인으로 제한
			limit {
				counter = "LINE"
				value = "TOTALCOUNT"
				maximum = "200".toBigDecimal()
			}
		}
	}
}
