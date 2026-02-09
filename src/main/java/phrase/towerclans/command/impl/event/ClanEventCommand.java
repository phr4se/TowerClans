package phrase.towerclans.command.impl.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.event.Event;
import phrase.towerclans.clan.event.exception.EventAlreadyRun;
import phrase.towerclans.clan.event.exception.SchematicDamaged;
import phrase.towerclans.clan.event.exception.SchematicNotExist;
import phrase.towerclans.clan.event.impl.Capture;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.logging.Logger;

public class ClanEventCommand implements CommandHandler {
    private final TowerClans plugin;

    public ClanEventCommand(TowerClans plugin) {
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

    @Override
    public boolean handler(CommandSender commandSender, String[] args) {
        Logger logger = plugin.getLogger();
        if (args.length < 3) {
            logger.severe(Config.getCommandMessages().incorrectArguments());
            return false;
        }
        Event.EventType eventType;
        try {
            eventType = Event.EventType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.severe(Config.getCommandMessages().incorrectArguments());
            return true;
        }
        String action = args[2];
        if (eventType == Event.EventType.CAPTURE) {
            if (action.equalsIgnoreCase("start")) {
                Event event = new Capture(plugin);
                try {
                    event.startEvent();
                } catch (EventAlreadyRun e) {
                    logger.severe(e.getMessage());
                    return true;
                } catch (SchematicNotExist | SchematicDamaged e) {
                    Event.unRegister(Event.EventType.CAPTURE);
                    logger.severe(e.getMessage());
                    return true;
                }
                logger.info(Config.getCommandMessages().runnedEvent());
            } else if (action.equalsIgnoreCase("stop")) {
                if (Event.isRunningEventType(Event.EventType.CAPTURE)) {
                    Event runningEvent = Event.getRunningEvent(Event.EventType.CAPTURE);
                    if (runningEvent == null) {
                        Event.unRegister(Event.EventType.CAPTURE);
                        logger.info(Config.getCommandMessages().stoppedEvent());
                        return true;
                    }
                    runningEvent.endEvent();
                    logger.info(Config.getCommandMessages().stoppedEvent());
                    return true;
                }
                logger.severe(Config.getCommandMessages().notRunning());
            }
        }
        return true;
    }
}

