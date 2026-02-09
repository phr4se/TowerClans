TowerClans - adds clans

Functionality

- Clan event
- Highlight allies of the clan
- Clan storage
- Disable privileges in the clan event zone
- Restrict command usage in the clan event zone
- Customizable GUIs
- Configure participant permissions in the GUI
- Actions

️Placeholders

%towerclans_clan_name% - displays the name of the clan a player is currently a member of. Use it in chat, signs, or anywhere placeholders are supported!
%towerclans_clan_name% - displays the name of the clan the player belongs to; if the player does not belong to a clan, it shows the value from the config.
%towerclans_clan_name_chat% - displays the name of the clan the player belongs to with symbols specified in the configuration file (config.yml); if the player does not belong to a clan, it shows the value from the config without symbols.
%towerclans_top_<0..9>% - displays the name of the clan based on the top ranking; if there is no clan, it shows the value from the config.

Permissions

- towerclans.event.privilege.bypass - Bypass the privilege disabling in the clan event zone
- towerclans.<command name> - Permission to use the command

Commands

- /clan - View the list of commands
- /clan create <clan name> - Create a clan
- /clan disband - Disband the clan
- /clan invest <amount> - Deposit an amount into the clan balance
- /clan withdraw <amount> - Withdraw an amount from the clan balance
- /clan invite <player> - Invite a player to the clan
- /clan accept - Accept an invitation to the clan
- /clan decline - Decline an invitation to the clan
- /clan kick <player> - Kick a player from the clan
- /clan pvp - Toggle PvP mode in the clan
- /clan glow - Enable highlighting for clan members
- /clan storage - Open the clan storage
- /clan base - Teleport to the clan base
- /clan setbase - Set the clan base
- /clan delbase - Delete the clan base
- /clan rank <player> <1..3> - Set a player's rank in the clan
- /clan setholo <..> - Set the clan hologram
- /clan delholo - Delete the clan hologram
- /clan editholo <..> - Edit the clan hologram
- /clan stats <player> - View a player's statistics in the clan
- /clan info <clan name> - View information about the clan
- /clan top - View the top clans
- /clan leave - Leave the clan
- /clan chat - Send a message to clan members
- /clan event <capture> <start|stop> - Start/stop a clan event
- /clan menu - Open the clan menu
