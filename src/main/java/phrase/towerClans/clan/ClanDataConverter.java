package phrase.towerClans.clan;

import org.bukkit.Bukkit;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;

import java.util.*;

public class ClanDataConverter {

    public static List<String> mapToList(Map<ModifiedPlayer, String> map) {

        List<String> list = new ArrayList<>();

        for(Map.Entry<ModifiedPlayer, String> entry : map.entrySet()) list.add((entry.getKey().getPlayerUUID().toString() + ":" + entry.getValue()));

        return list;

    }

    public static Map<ModifiedPlayer, String> listToMap(List<String> data, ClanImpl clan) {
        Map<ModifiedPlayer, String> map = new HashMap<>();

        data.forEach(string -> {
            String[] strings = string.split(":");
            UUID uuid = UUID.fromString(strings[0]);
            ModifiedPlayer modifiedPlayer = new ModifiedPlayer(uuid, clan);
            map.put(modifiedPlayer, strings[1]);
        });

        return map;
    }

}
