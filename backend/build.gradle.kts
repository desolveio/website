val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val services_version: String by project
val services_profiles_version: String by project
val kmongo_version: String by project

plugins {
	application
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.serialization") version "1.6.21"
}

application {
	mainClass.set("io.desolve.website.ApplicationKt")

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
		jvmTarget = "11"
	}
}

dependencies {
	// KTor Core
	implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
	// KTor Content
	implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-locations-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-status-pages:$ktor_version")
	// KTor Auth
	implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
	// KTor Netty
	implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
	// logging
	implementation("ch.qos.logback:logback-classic:$logback_version")

	// Desolve Services
	implementation("io.desolve.services:core:$services_version")
	implementation("io.desolve.services:protocol-stub:$services_version")
	implementation("io.desolve.services:distcache:$services_version")
	implementation("io.desolve.services:profiles:$services_profiles_version")

	// Apache Commons
	implementation("commons-codec:commons-codec:1.15")

	// KMongo
	implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")

	// tests
	testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
	testImplementation(kotlin("test-junit", kotlin_version))
}