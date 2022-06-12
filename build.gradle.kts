group = "io.desolve.website"
version = "0.0.1"

plugins {
	idea
	`maven-publish`
}

allprojects {
	repositories {
		mavenLocal()
		mavenCentral()

		maven {
			url = uri("${property("desolve_artifactory_contextUrl")}/gradle-release")

			credentials {
				username = property("desolve_artifactory_user") as String
				password = property("desolve_artifactory_password") as String
			}
		}
	}
}

subprojects {
	apply(plugin = "idea")

	idea {
		module {
			isDownloadJavadoc = true
			isDownloadSources = true
		}
	}

}
