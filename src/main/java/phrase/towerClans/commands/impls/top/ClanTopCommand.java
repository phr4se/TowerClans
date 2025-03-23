package phrase.towerClans.commands.impls.top;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.commands.CommandHandler;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClanTopCommand implements CommandHandler {

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_clan.items");

        List<ClanImpl> clanList = ClanImpl.getClans().values().stream().sorted((o, o1) -> Integer.compare(o1.getXp(), o.getXp())).limit(10).collect(Collectors.toList());
        int place = 1;
        String format = configurationSection.getString("top_clan.format");
        List<String> list = new ArrayList<>();
        for(ClanImpl o : clanList) {
            list.add(HexUtil.color(format.replace("%place%", String.valueOf(place)).replace("%clan_name%", o.getName()).replace("%xp%", String.valueOf(o.getXp()))));
            place++;
        }

        for(String string : list) {
            ChatUtil.getChatUtil().sendMessage(player, string);
        }

        return true;
    }
}
