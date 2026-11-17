import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment
import me.modmuss50.mpp.PublishModTask
import kotlin.enums.enumEntries

plugins {
    `maven-publish`
    id("publish-mutex")
    id("me.modmuss50.mod-publish-plugin")
}

val sc = extensions.getByType<StonecutterBuildExtension>()
val loader = sc.current.project.substringAfter('-')
val dryRunRequested = dryPublishRequested()

val loaderNames = mapOf("fabric" to "Fabric", "forge" to "Forge", "neoforge" to "NeoForge")
val loaderRequirements = mapOf("fabric" to listOf("fabric-api"))
val curseforgeSides = mapOf("both" to (true to true), "client" to (true to false), "server" to (false to true))
val modrinthEnvironments = enumEntries<ModrinthEnvironment>().associateBy { it.name.lowercase() }
val dryRunProjectIds = mapOf("publish.modrinth_id" to "dry-run", "publish.curseforge_id" to "0")

fun tomlList(vararg path: String): List<String>? = sc.properties.rawOrNull(*path)?.asList()?.map { it.toString() }

fun requiredTomlList(vararg path: String): List<String> = requireNotNull(tomlList(*path)) {
    "${path.joinToString(".")} is missing in stonecutter.properties.toml"
}

fun projectId(key: String): Provider<String> {
    val id = sc.properties.get<String>(key)
    val placeholder = dryRunProjectIds.getValue(key)
    return providers.provider {
        if (id.isEmpty() && dryRunRequested) return@provider placeholder
        require(id.isNotEmpty()) { "$key must be set in stonecutter.properties.toml to publish, got '$id'" }
        id
    }
}

val publishedNodes = sc.publishedNodes()
if (publishedNodes != null) {
    val unknownNodes = publishedNodes - rootProject.subprojects.map { it.name }.toSet()
    require(unknownNodes.isEmpty()) { "publish.nodes must only list existing nodes, got unknown $unknownNodes" }
    if (sc.current.project !in publishedNodes) tasks.withType<PublishModTask>().configureEach { enabled = false }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = the<BasePluginExtension>().archivesName.get()
            from(components["java"])
        }
    }
}

publishMods {
    val minecraft = sc.minecraftLabel()
    val modVersion = sc.properties.get<String>("mod.version")
    val loaderName = requireNotNull(loaderNames[loader]) {
        "loader must be one of ${loaderNames.keys.toList()}, got '$loader'"
    }
    val modEnvironment = sc.properties.get<String>("mod.environment")
    val (clientSide, serverSide) = requireNotNull(curseforgeSides[modEnvironment]) {
        "mod.environment must be one of ${curseforgeSides.keys.toList()}, got '$modEnvironment'"
    }
    val modrinthEnvironmentName = sc.properties.get<String>("publish.modrinth_environment")
    val modrinthEnvironment = requireNotNull(modrinthEnvironments[modrinthEnvironmentName]) {
        "publish.modrinth_environment must be one of ${modrinthEnvironments.keys.toList()}, got '$modrinthEnvironmentName'"
    }
    val releaseTypes = mapOf("stable" to STABLE, "beta" to BETA, "alpha" to ALPHA)
    val releaseType = sc.properties.get<String>("publish.type")
    val requiredProjects = requiredTomlList("publish", "requires") + loaderRequirements[loader].orEmpty()
    val optionalProjects = requiredTomlList("publish", "optional")
    val changelogFile = rootProject.file("Changelog.md")
    val javaVersion = providers.provider { the<JavaPluginExtension>().targetCompatibility }
    val releasedVersions = providers.provider { sc.minecraftReleases() }

    type = requireNotNull(releaseTypes[releaseType]) {
        "publish.type must be one of ${releaseTypes.keys.toList()}, got '$releaseType'"
    }
    version = "$modVersion+$minecraft-$loader"
    displayName = "${sc.properties.get<String>("mod.name")} $modVersion for $loaderName $minecraft"
    changelog = providers.provider {
        val lines = changelogFile.readLines()
        val start = lines.indexOf("## $modVersion")
        require(start >= 0) { "Changelog.md has no '## $modVersion' section" }
        val next = lines.subList(start + 1, lines.size).indexOfFirst { it.startsWith("## ") }
        val end = if (next < 0) lines.size else start + 1 + next
        lines.subList(start + 1, end).joinToString("\n").trim()
    }
    modLoaders.add(loader)
    dryRun = dryRunRequested

    modrinth {
        projectId = projectId("publish.modrinth_id")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        environment = modrinthEnvironment
        minecraftVersions.addAll(releasedVersions)
        requires(*requiredProjects.toTypedArray())
        optional(*optionalProjects.toTypedArray())
    }

    curseforge {
        projectId = projectId("publish.curseforge_id")
        accessToken = providers.environmentVariable("cf_token")
        minecraftVersions.addAll(releasedVersions)
        javaVersions.add(javaVersion)
        client = clientSide
        server = serverSide
        changelogType = "markdown"
        requires(*requiredProjects.toTypedArray())
        optional(*optionalProjects.toTypedArray())
    }
}
