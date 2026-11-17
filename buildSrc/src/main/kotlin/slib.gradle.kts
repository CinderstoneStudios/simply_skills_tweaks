import dev.kikugie.stonecutter.build.StonecutterBuildExtension

val sc = extensions.getByType<StonecutterBuildExtension>()
val loader = sc.current.project.substringAfter('-')
val minecraft = sc.current.version
val slibVersion = sc.properties.get<String>("deps.slib")
val yacl = "dev.isxander:yet-another-config-lib:${sc.properties.get<String>("deps.yacl")}-$loader"
val slibLocal = flag("slibLocal")
val withYacl = !flag("noYacl")

data class LoaderConfigurations(val slib: String, val compileOnly: String, val runtimeOnly: String)

val loaderConfigurations = mapOf(
    "fabric" to LoaderConfigurations("modImplementation", "modCompileOnly", "modLocalRuntime"),
    "forge" to LoaderConfigurations("modImplementation", "modCompileOnly", "modRuntimeOnly"),
    "neoforge" to LoaderConfigurations("implementation", "compileOnly", "runtimeOnly"),
)

val devMods: Map<String, () -> String> = mapOf(
    "fabric" to { "com.terraformersmc:modmenu:${sc.properties.get<String>("deps.modmenu")}" },
)

val slibTestLibraries: Map<String, () -> String> = mapOf(
    "forge" to { "blue.endless:jankson:${sc.properties.get<String>("deps.jankson")}" },
)

val mixinExtrasAnnotations: Map<String, () -> String> = mapOf(
    "forge" to { "io.github.llamalad7:mixinextras-common:${sc.properties.get<String>("deps.mixinextras")}" },
)

val loaderConfiguration = requireNotNull(loaderConfigurations[loader]) {
    "loader must be one of ${loaderConfigurations.keys.toList()}, got '$loader'"
}
val slib = if (slibLocal) "com.stalemated.lib:s-lib-$loader:$slibVersion+$minecraft" else "maven.modrinth:s-lib:$slibVersion+$minecraft-$loader"

repositories {
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent {
        forRepository { mavenLocal() }
        filter { includeGroup("com.stalemated.lib") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.isxander.dev/releases") { name = "Isxander" } }
        filter { includeGroup("dev.isxander") }
    }
    exclusiveContent {
        forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" } }
        filter { includeGroup("thedarkcolour") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.quiltmc.org/repository/release") { name = "QuiltMC" } }
        filter { includeGroup("org.quiltmc.parsers") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" } }
        filter { includeGroup("com.terraformersmc") }
    }
}

dependencies {
    add(loaderConfiguration.slib, slib)
    add(loaderConfiguration.compileOnly, yacl)

    if (withYacl) {
        add(loaderConfiguration.runtimeOnly, yacl)
        devMods[loader]?.let { add(loaderConfiguration.runtimeOnly, it()) { isTransitive = false } }
    }

    slibTestLibraries[loader]?.let { add("testRuntimeOnly", it()) }
    mixinExtrasAnnotations[loader]?.let {
        add("compileOnly", it())
        add("annotationProcessor", it())
    }
}
