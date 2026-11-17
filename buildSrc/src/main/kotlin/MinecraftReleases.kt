import dev.kikugie.stonecutter.build.StonecutterBuildExtension

fun StonecutterBuildExtension.minecraftReleases(): List<String> {
    val releases = properties.rawOrNull("mod", "mc_releases")?.asList()?.map { it.toString() }.orEmpty()
    require(releases.isNotEmpty()) { "mod.mc_releases is missing or empty for node ${current.project}" }
    return releases
}

fun StonecutterBuildExtension.publishedNodes(): List<String>? {
    val nodes = properties.rawOrNull("publish", "nodes")?.asList()?.map { it.toString() } ?: return null
    require(nodes.isNotEmpty()) { "publish.nodes must list at least one node, or be removed so every node publishes, got []" }
    return nodes
}

fun StonecutterBuildExtension.minecraftLabel(): String {
    if (publishedNodes() == null) return current.version
    val releases = minecraftReleases()
    return if (releases.size == 1) releases.single() else "${releases.first()}-${releases.last()}"
}
