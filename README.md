# KM Hud Overlays Mod
Allows you configure the vanilla overlays that appear on the hud or add custom ones.

### Example
```json
{
  "pumpkin": {
    "overlays": [
      {
        "texture": "minecraft:textures/misc/pumpkinblur",
        "layer": -90
      }
    ]
  },
  "powder_snow": {
    "overlays": [
      {
        "texture": "minecraft:textures/misc/powder_snow_outline.png",
        "layer": -90
      }
    ]
  }
}
```

### Properties

```
{
  minecraft:pumpkin?: {
    overlays?: {
      texture: Identifier
      fit_to_screen?: boolean
      nineslice_size?: [int, int] | [int, int, int, int]
      base_size?: [int, int]
      layer?: int
      alpha?: float // buggy
      conditions?: Condition[]
    }[],
    conditions?: Condition[]
  }
  minecraft:powder_snow?: {
    overlays?: {
      texture: Identifier
      fit_to_screen?: boolean
      nineslice_size?: [int, int] | [int, int, int, int]
      base_size?: [int, int]
      layer?: int
      alpha?: float // buggy
      conditions?: Condition[]
    }[],
    conditions?: Condition[]
  }
  [custom: string]: {
    overlays?: {
      texture: Identifier
      fit_to_screen?: boolean
      nineslice_size?: [int, int] | [int, int, int, int]
      base_size?: [int, int]
      layer?: int
      alpha?: float // buggy
      conditions?: Condition[]
    }[],
    conditions?: Condition[]
  }
}
```

### Conditions

Checks if the player is underwater.
```
{
  condition: 'is_underwater'
}
```

Checks if the player is in the required gamemode.
```
{
  condition: 'is_gamemode'
  name: 'survival' | 'creative' | 'spectator' | 'adventure'
}
```

Checks if the player is in the required difficulty.
```
{
  condition: 'is_difficulty'
  name: 'peaceful' | 'easy' | 'normal' | 'hard'
}
```

Checks if the player has an item in the whole inventory.
```
{
  condition: 'has_item_in_inventory'
  item: Identifier
  count?: int
}
```

Checks if the player has an item in the specified slot.
```
{
  condition: 'has_item'
  slot: 'armor.helmet' | 'armor.chestplate' | 'armor.leggings' | 'armor.boots' | 'offhand' | 'mainHand' | 'hotbar.X' | 'inventory.X'
  item: Identifier
  count?: int
  nbt: NBT
}
```

Checks if player has the specified properties.
```
{
  condition: 'player_properties',
  properties: {
    is_on_fire?: boolean
    is_on_ground?: boolean
    is_holding_onto_ladder?: boolean
    is_using_splyglass?: boolean
    is_fall_flying?: boolean
    is_blocking?: boolean
    is_dead?: boolean
    is_alive?: boolean
    is_glowing?: boolean
    is_sneaking?: boolean
    is_sprinting?: boolean
    is_in_lava?: boolean
    is_inside_wall?: boolean
    is_underwater?: boolean
  }
}
```

Checks if the helmet item has a the given nbt property.
```
{
  condition: 'has_item_nbt_property'
  name?: string
}
```