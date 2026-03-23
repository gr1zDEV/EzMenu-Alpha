# EzMenu

EzMenu is a configurable Paper plugin that lets you build player-facing GUI menus from YAML instead of hardcoding inventories. Server owners can ship simple navigation menus, staff utilities, and quick-access hubs using menu files, clickable items, permissions, sounds, and optional PlaceholderAPI integration.

## What EzMenu does

EzMenu loads menu definitions from the `plugins/EzMenu/menus/` folder and turns them into clickable inventories. Each menu can define:

- A custom title and inventory size.
- A menu-level permission.
- Item slots, materials, names, and lore.
- Click actions such as opening another menu, refreshing the current menu, running player commands, running console commands, sending messages, and closing the inventory.
- Per-item permissions.
- Optional hidden items when a player lacks permission.
- Optional placeholder-based visibility rules.
- Optional per-item click sounds.
- Optional per-button click cooldowns.
- Optional per-item glow effects.

Out of the box, the plugin includes example menus for a main hub, server navigation, and admin actions.

## Features

- **YAML-driven menus** with no code edits required for normal menu creation.
- **Nested menu navigation** so one item can open another menu.
- **Permission-aware access control** for entire menus or individual items.
- **Optional hidden restricted items** using `no-permission-hidden`.
- **Placeholder-based conditional items** using `show-if-placeholder`.
- **PlaceholderAPI support** for menu titles, item names, lore, messages, and actions when the hook is enabled.
- **Built-in sounds** for item clicks.
- **Per-button cooldowns** to prevent spam clicks.
- **Optional glowing items** for highlighting important buttons.
- **Reload command** for updating config and menu files without restarting the server.
- **Tab completion** for commands and known menu IDs.
- **Folia-friendly metadata** in the plugin descriptor.

## Requirements

- **Server software:** Paper-compatible server.
- **Minecraft API target:** `1.20` in `plugin.yml`.
- **Build dependency:** Paper API `1.21.1-R0.1-SNAPSHOT`.
- **Java:** 21.
- **Optional plugin:** PlaceholderAPI for placeholder parsing.

## Installation

1. Build the plugin JAR with Gradle:
   ```bash
   ./gradlew build
   ```
2. Put the generated JAR into your server's `plugins/` directory.
3. Start the server once to generate the default config files.
4. Edit the files in `plugins/EzMenu/` to match your server.
5. Use `/ezmenu reload` after making changes, or restart the server.

## Default files generated

After first launch, EzMenu creates these main files:

- `config.yml` - plugin behavior and hook toggles.
- `messages.yml` - user-facing messages.
- `sounds.yml` - default sound settings.
- `menus/main.yml` - example main menu.
- `menus/server.yml` - example server menu.
- `menus/admin.yml` - example admin menu.

## Commands

### `/ezmenu open <menu>`
Opens a menu for the player.

### `/ezmenu reload`
Reloads the plugin configuration and all menus.

### `/ezmenu list`
Lists the menus visible to the command sender.

### `/menu open <menu>`
Shortcut command for opening a menu.

### Aliases

- `/emenu`

## Permissions

- `ezmenu.open` - Allows opening menus. Default: `true`
- `ezmenu.reload` - Allows reloading configs. Default: `op`
- `ezmenu.list` - Allows listing menus. Default: `true`
- `ezmenu.admin` - Grants admin-level access to protected menus/items where configured. Default: `op`

## How menu files work

Each file inside `plugins/EzMenu/menus/` can contain one or more menus under a `menus:` root.

### Menu-level options

```yml
menus:
  main:
    title: "&8Main Menu"
    size: 27
    permission: ""
    items: {}
```

- `title` - Inventory title. Supports color codes and placeholders.
- `size` - Inventory size. Must be a multiple of 9, from 9 to 54.
- `permission` - Optional permission required to open the menu.
- `items` - Item definitions keyed by item ID.

### Item-level options

```yml
items:
  example:
    slot: 11
    material: COMPASS
    name: "&aServer Menu"
    lore:
      - "&7Open the server options"
    actions:
      - "open:server"
    permission: ""
    show-if-placeholder: ""
    no-permission-hidden: false
    deny-message: "&cYou cannot use this."
    sound: "UI_BUTTON_CLICK"
    glow: true
    cooldown: 1.5
    cooldown-message: "&cWait &f{seconds}s&c before clicking this again."
```

- `slot` - Zero-based inventory slot.
- `material` - Bukkit/Paper material name.
- `name` - Item display name.
- `lore` - List of lore lines.
- `actions` - Actions executed when clicked.
- `permission` - Optional permission for that item.
- `show-if-placeholder` - Optional visibility rule based on placeholder output.
- `no-permission-hidden` - If `true`, the item is not shown to players without the permission.
- `deny-message` - Message shown when a player clicks without permission, if the item is visible.
- `sound` - Sound played when clicked.
- `glow` - If `true`, forces the enchantment glint effect on that menu item.
- `cooldown` - Optional per-player cooldown for that button in seconds. Decimals are supported.
- `cooldown-message` - Optional message shown while the button is still on cooldown. Supports `{seconds}`, `{cooldown}`, and `{item}`.

## Supported click actions

EzMenu supports these item actions:

- `open:<menu>` - Opens another EzMenu menu.
- `player-command:<command>` - Makes the player run a command.
- `console-command:<command>` - Runs a command as the server console.
- `message:<text>` - Sends a message to the player.
- `close` - Closes the inventory.
- `refresh` - Re-renders the current menu so placeholders, visibility checks, and permission-gated items update immediately.

### Action examples

```yml
actions:
  - "open:server"
  - "player-command:spawn"
  - "console-command:give {player} diamond 1"
  - "message:&aWelcome, {player}!"
  - "close"
  - "refresh"
```

## Placeholder support

If PlaceholderAPI is installed and enabled in `config.yml`, EzMenu parses placeholders in:

- Menu titles
- Item names
- Item lore
- Deny messages
- Click actions

The plugin also replaces `{player}` inside actions with the player's username.

### Button cooldowns

Buttons can define their own cooldown so players cannot spam them. Cooldowns are tracked per player and per button, so one player triggering a button does not block everyone else, and different buttons can have different cooldowns.

Example:

```yml
cooldown: 2.5
cooldown-message: "&cWait &f{seconds}s&c before using this again."
```

Behavior:

- `cooldown` values less than or equal to `0` disable the cooldown.
- `cooldown-message` is optional; if omitted, EzMenu uses the default `button-cooldown` message from `messages.yml`.
- `{seconds}` and `{cooldown}` are replaced with the remaining cooldown time.
- `{item}` is replaced with the item ID from the menu file.

### Conditional visibility with `show-if-placeholder`

You can show an item only when a placeholder resolves to a matching value.

Examples:

```yml
show-if-placeholder: "%vault_rank%==admin"
```

```yml
show-if-placeholder: "%some_toggle_placeholder%"
```

Behavior:

- `placeholder==value` or `placeholder=value` checks for an exact value match.
- A single placeholder is treated as true when it resolves to `true`, `yes`, `1`, or `on`.
- Multiple items can share the same slot; when more than one is visible, the last visible item defined for that slot is the one rendered and clicked.

## Main configuration

`config.yml`

```yml
debug: false
hooks:
  placeholderapi: true
storage:
  sqlite:
    enabled: false
behavior:
  deny-on-missing-menu: true
```

### Config notes

- `debug` - Enables extra warning output for troubleshooting.
- `hooks.placeholderapi` - Turns PlaceholderAPI parsing on or off.
- `storage.sqlite.enabled` - Present in the config, but SQLite storage is not currently wired into menu behavior.
- `behavior.deny-on-missing-menu` - Present in the config, but not currently used by command or menu logic.

## Example default menus

### Main menu
- Opens the server menu.
- Shows an admin menu button only to players with `ezmenu.admin`.

### Server menu
- Includes a spawn button that runs `/spawn` and closes the menu.
- Includes a server info button that sends a chat message.

### Admin menu
- Requires `ezmenu.admin` to open.
- Includes a reload button that runs `/ezmenu reload`.
- Includes a back button that returns to the main menu.

## Editing workflow

1. Start with one of the example menu files.
2. Change titles, item names, lore, and materials.
3. Add actions for navigation, commands, or messages.
4. Protect items or whole menus with permissions if needed.
5. Save the file.
6. Run `/ezmenu reload`.
7. Test the menu in game.

## Troubleshooting

### A menu does not open

Check the following:

- The menu ID exists under the `menus:` root.
- The menu size is valid.
- The player has the required permission.
- The file is valid YAML.
- You ran `/ezmenu reload` after editing.

### An item does not appear

Possible causes:

- The item slot is outside the menu size.
- `no-permission-hidden` is hiding it.
- `show-if-placeholder` evaluated to false.
- The material name is invalid and fell back to `STONE`.

### Placeholders are not working

- Make sure PlaceholderAPI is installed.
- Make sure `hooks.placeholderapi: true` is set.
- Reload the plugin after changing config.

### A sound does not play

- Verify that the sound name matches a valid Bukkit sound enum for your server version.

## Building from source

```bash
./gradlew build
```

The project uses Gradle with the Java plugin and targets Java 21.

## Notes for server owners

- Menu IDs are referenced by `open:<menuId>`, so keep IDs stable when linking menus together.
- The plugin cancels clicks in EzMenu inventories, making them safe as GUI interfaces.
- Invalid menu sizes are automatically reset to `27` with a warning in the server log.
- Invalid materials fall back to `STONE` with a warning.
- Unknown action types are ignored, and debug logging can help identify them.

## Quick start example

```yml
menus:
  hub:
    title: "&8Hub"
    size: 27
    permission: ""
    items:
      survival:
        slot: 11
        material: GRASS_BLOCK
        name: "&aSurvival"
        lore:
          - "&7Join the survival server"
        actions:
          - "player-command:server survival"
        permission: ""
        show-if-placeholder: ""
        no-permission-hidden: false
        deny-message: ""
        sound: "UI_BUTTON_CLICK"
```

Open it with:

```bash
/ezmenu open hub
```

## License

No license file is currently included in this repository. Add one before redistributing the project if you want explicit usage permissions.
