# Simply Skills Tweaks

**Simply Skills Tweaks** is a mod that aims to balance skills from the [Simply Skills](https://modrinth.com/mod/simply-skills) mod.

Supports Fabric on Minecraft 1.20.1. Requires [Simply Skills](https://modrinth.com/mod/simply-skills), [Spell Power](https://modrinth.com/mod/spell-power) and [S-Lib](https://github.com/Stalemated/s-lib).

---

## Features

### In-depth customization of skills

- Righteous Hammers customization:
  - New formula that picks the highest damage from three different branches
    - **Physical:** attack damage times `physicalAttackMultiplier`, up to `physicalSoftCapThreshold`. Attack damage past the threshold only scales by `physicalExcessMultiplier`, so stacking attack damage stops paying off as much.
    - **Tank:** attack damage times `tankAttackMultiplier`, plus max health times `tankHealthMultiplier`, plus armor times `tankArmorMultiplier`. Holding a shield in the off-hand multiplies it by `shieldBonusMultiplier`. Modded shields count too, as do items in the `c:shields` tag.
    - **Holy / Lightning:** attack damage times `holyLightningAttackMultiplier`, plus spell power times `holyLightningSpellMultiplier`, plus armor times `holyLightningArmorMultiplier`. Spell power is the higher of your Healing and Lightning spell power.
  - Formula can be toggled on/off
  - Easy debug logging that can be toggled in-game

And more to come!

## Config

The config file is `config/simply_skills_tweaks.json5`. The server's values are synced to every client.

With [YACL](https://modrinth.com/mod/yacl) and [Mod Menu](https://modrinth.com/mod/modmenu) installed, the config can be edited in-game. On a server only OPs can change it, in singleplayer and LAN the host can.

## Building

Building needs JDK 25 and S-Lib in your mavenLocal, or set `slibLocal=false` in `gradle.properties` to pull from Modrinth's Maven.

If going the first route, publish S-Lib first with `./gradlew publishToMavenLocal` in the [S-Lib repository](https://github.com/Stalemated/s-lib).

```bash
./gradlew build
```

```bash
./gradlew :1.20.1-fabric:runClient
```

- Runs happen in `run/1.20.1-fabric` and include Simply Skills with its dependencies, S-Lib, YACL and Mod Menu. `-PnoYacl` leaves out YACL and Mod Menu.

## License

All Rights Reserved, see [LICENSE](LICENSE).
