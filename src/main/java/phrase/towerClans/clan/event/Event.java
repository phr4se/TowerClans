package phrase.towerClans.clan.event;

import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.event.privilege.PrivilegeManager;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Event {

    public enum EventType {

        CAPTURE(0);

        private final int id;

        EventType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }

    private static final Map<EventType, Event> RUNNING_EVENTS = new HashMap<>();

    protected final Plugin plugin;

    public Event(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract void startEvent();
    public abstract void endEvent();
    public abstract void endEvent(ClanImpl clan);

    public abstract boolean isRunning();

    public static boolean isCurrentRunningEvent() {
        return !RUNNING_EVENTS.isEmpty();
    }

    public static Event getCurrentRunningEvent() {
        return RUNNING_EVENTS.entrySet().stream().map(Map.Entry::getValue).findFirst().get();
    }

    public static boolean register(EventType eventType, Event event) {
        if (RUNNING_EVENTS.containsKey(eventType)) return false;
        RUNNING_EVENTS.put(eventType, event);
        return true;
    }

    public static void unRegister(EventType eventType) {
        RUNNING_EVENTS.remove(eventType);
    }

    public static Event getRunningEvent(EventType eventType) {
        return RUNNING_EVENTS.get(eventType);
    }

    public static boolean isRunningEvent() {
        return !RUNNING_EVENTS.isEmpty();
    }

    public static boolean isRunningEventType(EventType eventType) {
        return RUNNING_EVENTS.containsKey(eventType);
    }

    public abstract boolean playerAtEvent(Player player);
    public abstract void broadcastForPlayersAboutStartEvent();
    public abstract void broadcastForPlayersAboutEndEvent(String clanName);

    public void broadcast(Plugin plugin, List<String> messages) {
        plugin.getServer().getOnlinePlayers().forEach(player -> messages.forEach(message -> Utils.sendMessage(player, message)));
    }

}
