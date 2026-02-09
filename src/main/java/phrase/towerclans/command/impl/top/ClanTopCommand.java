package phrase.towerclans.command.impl.top;

import org.bukkit.entity.Player;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.command.CommandHandler;
import phrase.towerclans.config.Config;
import phrase.towerclans.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ClanTopCommand implements CommandHandler {
    private final TowerClans plugin;

    public ClanTopCommand(TowerClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handler(Player player, String[] args) {
        List<ClanImpl> clanList = plugin.getClanManager().getClans().values().stream().sorted((o, o1) -> Integer.compare(o1.getXp(), o.getXp())).limit(10).toList();
        int place = 1;
        String format = Config.getMessages().placeFormat();
        List<String> list = new ArrayList<>();
        for (ClanImpl o : clanList) {
            list.add(Utils.COLORIZER.colorize(format.replace("%place%", String.valueOf(place)).replace("%clan_name%", o.getName()).replace("%xp%", String.valueOf(o.getXp()))));
            place++;
        }
        if (list.isEmpty()) {
            Utils.sendMessage(player, Config.getMessages().noClan());
            return true;
        }
        for (String string : list) {
            Utils.sendMessage(player, string);
        }
        return true;
    }
}
