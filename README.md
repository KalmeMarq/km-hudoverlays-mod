# KM Hud Overlays Mod (Fabric)
Allows you to customize the vanilla overlays that appear on the hud or add custom ones with conditions. All through a json file in a resource pack.


Resource pack file: **assets/kmhudoverlays/hud_overlays.json**


### Examples
```json
{
  "minecraft:pumpkin": {
    "overlays": [
      {
        "type": "image",
        "texture": "minecraft:textures/misc/pumpkinblur",
        "layer": -90
      },
      {
        "type": "gradient_renderer",
        "size": [64, 64],
        "offset": ["50%w", "50%h"],
        "orientation": "horizontal",
        "color_start": "rgb(255, 0, 255)",
        "color_end": "rgb(0, 0, 255)",
        "layer": 100
      },
      {
        "type": "text",
        "text": { "translate": "menu.play", "color": "#FF00FF" },
        "offset": ["50% - 50%w", 0],
        "anchor_in_parent": "left_middle",
        "anchor_in_self": "left_middle",
        "layer": 100
      }
    ]
  },
  "minecraft:powder_snow": {
    "overlays": [
      {
        "type": "image",
        "texture": "minecraft:textures/misc/powder_snow_outline.png",
        "layer": -90
      }
    ]
  }
}
```

Custom overlay
```jsonc
{
  "my_custom0": {
    "overlays": [
      {
        "type": "image",
        "texture": "kmhudoverlays:textures/misc/powder_snow_outline_red_nineslice.png",
        "nineslice_size": [178, 128, 178, 128],
        "texture_size": [512, 256],
        "size": ["100%", "100%"],
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
OverlayContainer {
    overlays?: Overlay[]
    conditions?: Condition[]
}

Anchor = "top_left" | "top_middle" | "top_right" | "left_middle" | "center" | "right_middle" | "bottom_left" | "bottom_middle" | "bottom_right"

Text =
    | string
    | { text: string, color?: string, extra?: Text[], bold?: boolean, underlined?: boolean, font?: Identifier, strikethrough?: boolean }
    | { translate: string, with?: Text[], color?: string, extra?: Text[], bold?: boolean, underlined?: boolean, font?: Identifier, strikethrough?: boolean } 

Color = [float, float, float] | "rgb(int, int, int)" | "rgba(int, int, int, int)" | "#RGB" | "#ARGB" |  "#RRGGBB" |  "#AARRGGBB"

// Operators allowed in expressions: + - * /
// X% percentage of the width or height of the screen
// Xpx pixels (the px is not needed)
// X%w percentage of the width of the overlay
// X%h percentage of the height of the overlay
// X%vw percentage of the width of the screen
// X%vh percentage of the height of the screen
Expression = number | "N - N% / N%x + N%y * Npx"

// Some math functions and constants can be used inside expressions
guiScale = options.guiScale
pi = 3.1415927
sqrt2 = 1.4142135
clamp(value, min, max)
sin(value)
cos(value)
min(value0, value1)
max(value0, value1)
abs(value)
invsqrt(value)
sign(value)
square(value)

// type = text
Overlay {
    type: "text"
    text: Text                          // Default: ""
    color?: Color                       // Default: white
    shadow?: boolean                    // Default: "top_left"
    size?: [Expression, Expression]     // Default: [width of the text, 9]
    max_size?: [Expression, Expression] // Default: size
    min_size?: [Expression, Expression] // Default: size
    offset?: [Expression, Expression]   // Default: [0, 0] 
    anchor_in_parent?: Anchor           // Default: "top_left"
    anchor_in_self?: Anchor             // Default: "top_left"
    layer?: integer                     // Default: -90
    alpha?: float                       // Default: 1.0
    conditions?: Condition[]
}

// type = image
Overlay {
    type: "image"
    texture: Identifier
    nineslice_size?: [int, int] | [int, int, int, int]
    base_size?: [int, int]              // Deprecated, use texture_size
    texture_size?: [int, int]
    size?: [Expression, Expression]     // Default: [screen width, screen height]
    max_size?: [Expression, Expression] // Default: size
    min_size?: [Expression, Expression] // Default: size
    offset?: [Expression, Expression]   // Default: [0, 0]
    anchor_in_parent?: Anchor           // Default: "top_left"
    anchor_in_self?: Anchor             // Default: "top_left"
    layer?: integer                     // Default: -90
    alpha?: float                       // Default: 1.0
    conditions?: Condition[]
}

// type = fill_renderer
Overlay {
    type: "fill_renderer"
    color: Color
    size?: [Expression, Expression]     // Default: [screen width, screen height]
    max_size?: [Expression, Expression] // Default: size
    min_size?: [Expression, Expression] // Default: size
    offset?: [Expression, Expression]   // Default: [0, 0]
    anchor_in_parent?: Anchor           // Default: "top_left"
    anchor_in_self?: Anchor             // Default: "top_left"
    layer?: integer                     // Default: -90
    alpha?: float                       // Default: 1.0
    conditions?: Condition[]
}

// type = gradient_renderer
Overlay {
    type: "gradient_renderer"
    color_start: Color
    color_end: Color
    orientation: "vertical" | "horizontal" // Default: "vertical"
    size?: [Expression, Expression]     // Default: [screen width, screen height]
    max_size?: [Expression, Expression] // Default: size
    min_size?: [Expression, Expression] // Default: size
    offset?: [Expression, Expression]   // Default: [0, 0]
    anchor_in_parent?: Anchor           // Default: "top_left"
    anchor_in_self?: Anchor             // Default: "top_left"
    layer?: integer                     // Default: -90
    alpha?: float                       // Default: 1.0
    conditions?: Condition[]
}

{
  minecraft:pumpkin_blur?: OverlayContainer
  minecraft:powder_snow_outline?: OverlayContainer
  minecraft:spyglass?: OverlayContainer
  minecraft:vignette?: OverlayContainer
  minecraft:portal?: OverlayContainer
  [custom: string]: OverlayContainer
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