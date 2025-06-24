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
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

public class ClanEventCommand implements CommandHandler {

    private final Plugin plugin;

    public ClanEventCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {

        if (args.length < 3) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return false;
        }

        Event.EventType eventType;
        try {
            eventType = Event.EventType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            Utils.sendMessage(player, Config.getCommandMessages().incorrectArguments());
            return true;
        }
        String action = args[2];

        if (eventType == Event.EventType.CAPTURE) {
            if (action.equalsIgnoreCase("start")) {
                Event event = new Capture(plugin);
                try {
                    event.startEvent();
                } catch (EventAlreadyRun e) {
                    Utils.sendMessage(player, Config.getCommandMessages().alreadyRunning());
                    plugin.getLogger().severe(e.getMessage());
                    return true;
                } catch (SchematicNotExist | SchematicDamaged e) {
                    Utils.sendMessage(player, Config.getCommandMessages().schematicDamaged());
                    Event.unRegister(Event.EventType.CAPTURE);
                    plugin.getLogger().severe(e.getMessage());
                    return true;
                }
                Utils.sendMessage(player, Config.getCommandMessages().runnedEvent());
            } else if (action.equalsIgnoreCase("stop")) {
                if (Event.isRunningEventType(Event.EventType.CAPTURE)) {
                    Event runningEvent = Event.getRunningEvent(Event.EventType.CAPTURE);
                    if (runningEvent == null) {
                        Event.unRegister(Event.EventType.CAPTURE);
                        Utils.sendMessage(player, Config.getCommandMessages().stoppedEvent());
                        return true;
                    }
                    runningEvent.endEvent();
                    Utils.sendMessage(player, Config.getCommandMessages().stoppedEvent());
                    return true;
                }
                Utils.sendMessage(player, Config.getCommandMessages().notRunning());
            }
        }

        return true;
    }
}

