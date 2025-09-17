package phrase.towerClans.command.impl.top;

import org.bukkit.entity.Player;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.config.Config;
import phrase.towerClans.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ClanTopCommand implements CommandHandler {


    @Override
    public boolean handler(Player player, String[] args) {

        List<ClanImpl> clanList = ClanImpl.getClans().values().stream().sorted((o, o1) -> Integer.compare(o1.getXp(), o.getXp())).limit(10).toList();
        int place = 1;
        String format = Config.getMessages().placeFormat();
        List<String> list = new ArrayList<>();
        for(ClanImpl o : clanList) {
            list.add(Utils.COLORIZER.colorize(format.replace("%place%", String.valueOf(place)).replace("%clan_name%", o.getName()).replace("%xp%", String.valueOf(o.getXp()))));
            place++;
        }

        if(list.isEmpty()) {
            Utils.sendMessage(player, Config.getMessages().noClan());
            return true;
        }

        for(String string : list) {
            Utils.sendMessage(player, string);
        }

        return true;
    }
}
