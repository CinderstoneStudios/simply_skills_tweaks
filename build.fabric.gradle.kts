plugins {
    id("dev.kikugie.loom-back-compat")
    id("mod-conventions")
    id("mod-publishing")
    id("slib")
    id("simplyskills")
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    testRuntimeOnly(files(sourceSets.main.get().compileClasspath))
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run/${sc.current.project}")
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }
}

tasks {
    named<Copy>("buildAndCollect") {
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
    }
}
