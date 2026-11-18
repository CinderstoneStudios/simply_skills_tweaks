# Simply Skills Tweaks

**Simply Skills Tweaks** is a mod that aims to balance skills from the Simply Skills mod.

## Features

- **Righteous Hammers:** a new formula picks the highest damage from three branches:
  - **Physical** (favors melee builds), with a soft cap so stacking attack damage stops paying off as much
  - **Tank** (favors tank builds), from max health and armor, with a bonus for holding a shield in the off-hand
  - **Holy / Lightning** (favors paladin / lightning builds), from the higher of your Healing and Lightning spell power
- The formula can be toggled on/off, and every multiplier can be tweaked
- Debug logging that shows each player's stats and which branch won

And more to come!

The config file is `config/simply_skills_tweaks.json5`. The server's values are synced to every client, and on a server only OPs can change them.

## Dependencies

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Simply Skills](https://modrinth.com/mod/simply-skills)
- [Spell Power](https://modrinth.com/mod/spell-power)
- [S-Lib](https://modrinth.com/mod/s-lib)
- **Optional:** [YACL](https://modrinth.com/mod/yacl) for the in-game config screen
- **Optional:** [Mod Menu](https://modrinth.com/mod/modmenu) for the config button