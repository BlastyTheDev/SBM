# SBM
**S**ky**B**lock **M**acro is a [Fabric](https://fabricmc.net/) mod that aims to automate repetitive tasks in [Hypixel](https://hypixel.net/) SkyBlock.

It currently supports Minecraft `1.21.11` on Fabric loader `0.18.4` and above.

> [!IMPORTANT]
> The latest SBM requires
> - [Fabric API](https://modrinth.com/mod/fabric-api) version `0.141.1+1.21.11` or later
> - [Cloth Config API](https://modrinth.com/mod/cloth-config) version `21.11.153` or later
> 
> It is recommended but **not** necessary to also install
> - [Mod Menu](https://modrinth.com/mod/modmenu) version `17.0.0-beta.1` or later

> [!CAUTION]
> This mod will ONLY work if your Hypixel language is set to **ENGLISH**.
>
> This mod will NOT work on Minecraft `26.1` or above until I update it as Mojang have decided to remove obfuscation from the game.
> SBM currently uses the Fabric Yarn mappings, which `26.1` does not support.
>
> Using the command `/sbm resume` does not hold down attack for some reason. Use the keybind instead (default `F23`)

## Features
- Basic Farming macro for S-shaped farms (using water/air walkways)
- Garden Visitor serving (only works once though, macro ends there)

## Planned Features
- All macros to be highly configurable using Cloth Config API
- Handling of Pests and Visitors in the Garden
- Fishing Macro (with options for killing Sea Creatures)
- Automatic Selling to Bazaar and NPC (/trades)
