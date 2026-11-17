import dev.kikugie.stonecutter.build.StonecutterBuildExtension

val sc = extensions.getByType<StonecutterBuildExtension>()

fun dependencyVersion(key: String): String = sc.properties.get<String>("deps.$key")

val compileMods = listOf(
    "maven.modrinth:simply-skills:${dependencyVersion("simply_skills")}",
    "maven.modrinth:spell-power:${dependencyVersion("spell_power")}-fabric",
)

val runtimeMods = listOf(
    "maven.modrinth:spell-engine:${dependencyVersion("spell_engine")}-fabric",
    "me.shedaniel.cloth:cloth-config-fabric:${dependencyVersion("cloth_config")}",
    "curse.maven:puffish-skills-835091:${dependencyVersion("puffish_skills")}",
    "curse.maven:puffish-attributes-991341:${dependencyVersion("puffish_attributes")}",
    "dev.kosmx.player-anim:player-animation-lib-fabric:${dependencyVersion("player_anim")}",
)

val runtimeLibraries = listOf(
    "com.github.ZsoltMolnarrr:TinyConfig:${dependencyVersion("tiny_config")}",
    "me.shedaniel.cloth:basic-math:${dependencyVersion("basic_math")}",
)

repositories {
    exclusiveContent {
        forRepository { maven("https://maven.shedaniel.me/") { name = "Shedaniel" } }
        filter { includeGroup("me.shedaniel.cloth") }
    }
    exclusiveContent {
        forRepository { maven("https://cursemaven.com") { name = "CurseMaven" } }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.kosmx.dev/") { name = "KosmX" } }
        filter { includeGroup("dev.kosmx.player-anim") }
    }
    exclusiveContent {
        forRepository { maven("https://jitpack.io") { name = "JitPack" } }
        filter { includeGroup("com.github.ZsoltMolnarrr") }
    }
}

dependencies {
    compileMods.forEach {
        add("modCompileOnly", it) { isTransitive = false }
        add("modLocalRuntime", it) { isTransitive = false }
    }
    runtimeMods.forEach { add("modLocalRuntime", it) { isTransitive = false } }
    runtimeLibraries.forEach { add("localRuntime", it) { isTransitive = false } }
}
