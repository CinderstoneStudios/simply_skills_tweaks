# Changelog

## 2.0.0

### Changes
- Mod now requires S-Lib 4.1.0+ and Fabric Loader 0.18+
- Spell Power is now listed as a dependency
- Debug logging is now off by default
- Config screen changes:
  - The branch options are grayed out while the new formula is off
  - The translation keys of the options are now in snake_case (e.g. `simply_skills_tweaks.option.toggle_new_hammer_formula`), so resource packs that translate the mod need updating
- The debug log now calls the branches Physical, Tank and Holy/Lightning
- TinyConfig is no longer bundled, Simply Skills and Spell Power already ship it

### Fixes
- The debug log no longer keeps an entry for every player who ever used Righteous Hammers. A player's entry is removed when they leave
- Spell Power errors are no longer hidden behind the Holy / Lightning branch dealing zero damage

## 1.0.0

### Features
- Initial release
- Righteous Hammers customization:
    - New formula that picks the highest damage from three different branches
        - Physical (favors melee builds)
        - Tank (favors tank builds)
        - Holy / Lightning (favors paladin / lightning builds)
    - Formula can be toggled on/off
    - Easy debug logging that can be toggled in-game