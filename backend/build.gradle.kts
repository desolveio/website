val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val services_version: String by project
val services_profiles_version: String by project
val services_containers_version: String by project

val kmongo_version: String by project

plugins {
	application
	kotlin("jvm") version "1.7.0"
	kotlin("plugin.serialization") version "1.7.0"
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
	implementation(kotlin("stdlib"))
	implementation("io.grpc:grpc-kotlin-stub:1.3.0")
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
	implementation("io.desolve.services:core:$services_version")
	implementation("io.desolve.services:mail:$services_version")
	implementation("io.desolve.services:containers:$services_containers_version")
	implementation("io.desolve.services:protocol-stub:$services_version")
	implementation("io.desolve.services:distcache:$services_version")
	implementation("io.desolve.services:profiles:$services_profiles_version")

	// Misc.
	implementation("commons-codec:commons-codec:1.15")
	implementation("dev.forst:ktor-rate-limiting:1.3.3")

	// KMongo
	implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")

	// tests
	testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
	testImplementation(kotlin("test-junit", kotlin_version))
}
