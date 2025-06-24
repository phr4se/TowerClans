package phrase.towerClans.clan.event;

import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;

import java.util.HashMap;
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

}
