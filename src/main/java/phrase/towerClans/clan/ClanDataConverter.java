package phrase.towerClans.clan;

import org.bukkit.Bukkit;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanDataConverter {

    public static List<String> mapToList(Map<ModifiedPlayer, String> map) {

        List<String> list = new ArrayList<>();

        for(Map.Entry<ModifiedPlayer, String> entry : map.entrySet()) list.add((Bukkit.getPlayer(entry.getKey().getPlayerUUID()) == null) ? Bukkit.getOfflinePlayer(entry.getKey().getPlayerUUID()).getName() : Bukkit.getPlayer(entry.getKey().getPlayerUUID()).getName() + ":" + entry.getValue());

        return list;

    }

    public static Map<ModifiedPlayer, String> listToMap(List<String> data, ClanImpl clan) {
        Map<ModifiedPlayer, String> map = new HashMap<>();

        data.forEach(string -> {
            String[] strings = string.split(":");
            ModifiedPlayer modifiedPlayer = new ModifiedPlayer(Bukkit.getOfflinePlayer(strings[0]).getUniqueId(), clan);
            map.put(modifiedPlayer, strings[1]);
        });

        return map;
    }

}
