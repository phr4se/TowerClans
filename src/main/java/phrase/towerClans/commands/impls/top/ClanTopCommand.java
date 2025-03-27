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

    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanTopCommand(Plugin plugin) {
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public boolean handler(Player player, String[] args) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.top_clan");

        List<ClanImpl> clanList = ClanImpl.getClans().values().stream().sorted((o, o1) -> Integer.compare(o1.getXp(), o.getXp())).limit(10).collect(Collectors.toList());
        int place = 1;
        String format = configurationSection.getString("format");
        List<String> list = new ArrayList<>();
        for(ClanImpl o : clanList) {
            list.add(HexUtil.color(format.replace("%place%", String.valueOf(place)).replace("%clan_name%", o.getName()).replace("%xp%", String.valueOf(o.getXp()))));
            place++;
        }

        for(String string : list) {
            chatUtil.sendMessage(player, string);
        }

        return true;
    }
}
