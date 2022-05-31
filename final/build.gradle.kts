plugins {
	application
	id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
	mainClass.set("io.desolve.website.ApplicationKt")

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
	implementation(project(":frontend"))
	implementation(project(":backend"))
}

tasks {
	withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
		archiveFileName.set("DesolveWebsite.jar")
		mergeServiceFiles()
	}

	build {
		dependsOn(shadowJar)
	}
}
