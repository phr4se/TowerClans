package phrase.towerclans.serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializable {
    public static final String EMPTY = "";

    public static String locationToString(Location location) {
        return location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getWorld().getName();
    }

    public static Location stringToLocation(String data) {
        if (data.equals(EMPTY)) return null;
        String[] strings = data.split(":");
        return new Location(Bukkit.getWorld(strings[3]), Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
    }
}
