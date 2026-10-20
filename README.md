# Hitsounds

Hitsounds is a mod that plays a sound when you hit an entity!

- **Custom Hit Sound**: Play your custom `hitsound:hit` sound, or pick from the full set of Minecraft note-block instruments.
- **Pitch Control**: Tune the hit sound from 0.5× to 2.0×.
- **Per-Mob-Type Toggles**: Enable or disable the sound separately for players, hostile mobs, and passive mobs. Neutral-type targets (golems, armor stands, villagers, other non-mob entities) never play the sound.
- **In-Game Config Screen**: Press **H** to open the options screen — no restart needed. Settings save automatically.

Requires Minecraft 26.3 and Fabric Loader. Licensed under the MIT License — see [LICENSE](LICENSE).

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.3.
2. Drop `hitsounds-1.2.1+26.3.jar` into your `mods` folder.
3. Press **H** in-game to open the options screen.

## Building from source

Requires JDK 25.

```bash
./gradlew build        # Linux/macOS
gradlew.bat build      # Windows
```

The built jar is output to `build/libs/hitsounds-1.2.1+26.3.jar`.


