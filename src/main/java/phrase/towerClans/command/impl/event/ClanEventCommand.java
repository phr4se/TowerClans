package phrase.towerClans.command.impl.event;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.event.Event;
import phrase.towerClans.clan.event.exception.EventAlreadyRun;
import phrase.towerClans.clan.event.exception.SchematicDamaged;
import phrase.towerClans.clan.event.exception.SchematicNotExist;
import phrase.towerClans.clan.event.impl.Capture;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;

public class ClanEventCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanEventCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.event");

        if (args.length < 3) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return false;
        }

        Event.EventType eventType;
        try {
            eventType = Event.EventType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            chatUtil.sendMessage(player, configurationSection.getString("usage_command"));
            return true;
        }
        String action = args[2];

        if (eventType == Event.EventType.CAPTURE) {
            if (action.equalsIgnoreCase("start")) {
                Event event = new Capture(plugin);
                try {
                    event.startEvent();
                } catch (EventAlreadyRun e) {
                    chatUtil.sendMessage(player, configurationSection.getString("event_already_running"));
                    plugin.getLogger().severe(e.getMessage());
                    return true;
                } catch (SchematicNotExist | SchematicDamaged e) {
                    chatUtil.sendMessage(player, configurationSection.getString("schematic_error"));
                    Event.unRegister(Event.EventType.CAPTURE);
                    plugin.getLogger().severe(e.getMessage());
                    return true;
                }
                chatUtil.sendMessage(player, configurationSection.getString("you_runned_event"));
            } else if (action.equalsIgnoreCase("stop")) {
                if (Event.isRunningEventType(Event.EventType.CAPTURE)) {
                    Event runningEvent = Event.getRunningEvent(Event.EventType.CAPTURE);
                    if (runningEvent == null) {
                        Event.unRegister(Event.EventType.CAPTURE);
                        chatUtil.sendMessage(player, configurationSection.getString("you_stopped_event"));
                        return true;
                    }
                    runningEvent.endEvent();
                    chatUtil.sendMessage(player, configurationSection.getString("you_stopped_event"));
                    return true;
                }
                chatUtil.sendMessage(player, configurationSection.getString("event_not_running"));
            }
        }

        return true;
    }
}

