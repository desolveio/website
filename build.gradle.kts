group = "io.desolve.website"
version = "0.0.1"

plugins {
	idea
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