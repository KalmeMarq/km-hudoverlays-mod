# KM Hud Overlays Mod (Fabric)
Allows you customize the vanilla overlays that appear on the hud or add custom ones with conditions. All through a json file in a resource pack.


Resource pack file: **assets/kmhudoverlays/hud_overlays.json**


### Examples
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

Custom overlay
```json
{
  "my_custom0": {
    "overlays": [
      {
        "texture": "kmhudoverlays:textures/misc/powder_snow_outline_red_nineslice.png",
        "nineslice_size": [178, 128, 178, 128],
        "base_size": [512, 256],
        "fit_to_screen": true,
        "layer": -100
      }
    ],
    "conditions": [
      {
        "condition": "player_properties",
        "properties": {
          "is_on_fire": true
        }
      },
      {
        "condition": "is_underwater",
        "value": false
      },
      {
        "condition": "is_gamemode",
        "name": "survival"
      },
      {
        "condition": "is_difficulty",
        "name": "easy"
      },
      {
        "condition": "has_item",
        "slot": "hotbar.0",
        "item": "minecraft:iron_helmet",
        "nbt": {
          "behaveAsPumpkin": "1b"
        }
      }
    ]
  }
}
```

### Properties

```
{
  minecraft:pumpkin_blur?: {
    overlays?: {
      texture: Identifier
      fit_to_screen?: boolean
      nineslice_size?: [int, int] | [int, int, int, int]
      base_size?: [int, int]
      layer?: int
      alpha?: float // buggy
      conditions?: Condition[]
    }[]
  }
  minecraft:powder_snow_outline?: {
    overlays?: {
      texture: Identifier
      fit_to_screen?: boolean
      nineslice_size?: [int, int] | [int, int, int, int]
      base_size?: [int, int]
      layer?: int
      alpha?: float // buggy
      conditions?: Condition[]
    }[]
  }
   minecraft:spyglass?: {
    overlays?: {
      texture: Identifier
      fit_to_screen?: boolean
      nineslice_size?: [int, int] | [int, int, int, int]
      base_size?: [int, int]
      layer?: int
      alpha?: float // buggy
      conditions?: Condition[]
    }[]
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
  value?: boolean
}
```

Checks if the player is using the spyglass.
```
{
  condition: 'is_using_spyglass'
  value?: boolean
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

Checks if the helmet item has a given nbt property.
```
{
  condition: 'has_item_nbt_property'
  name?: string
}
```