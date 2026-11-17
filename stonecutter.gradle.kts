plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.20.1-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    properties {
        tags(version, loader)
    }

    constants {
        match(loader, "fabric", "neoforge", "forge")
    }

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }

        string(current.parsed >= "26.1") {
            replace("classTweaker v2 named", "classTweaker v2 official")
        }
    }
}

tasks.register(DRY_PUBLISH_TASK) {
    group = "publishing"
    description = "Runs publishMods on every node as a dry run: nothing is uploaded, the files and details land in build/publishMods"
    val requestedTasks = gradle.startParameter.taskNames.toList()
    if (dryPublishRequested()) dependsOn(subprojects.map { "${it.path}:publishMods" })
    else doFirst { throw GradleException("Run '$DRY_PUBLISH_TASK' by its full name so the dry run is on, got the task names $requestedTasks") }
}
