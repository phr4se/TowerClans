package phrase.towerClans.command.impl.top;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.command.CommandHandler;
import phrase.towerClans.util.ChatUtil;
import phrase.towerClans.util.colorizer.ColorizerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClanTopCommand implements CommandHandler {

    private final Plugin plugin;
    private final ChatUtil chatUtil;
    private final static ColorizerProvider colorizerProvider;

    static  {
        colorizerProvider = Plugin.getColorizerProvider();
    }

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
            list.add(colorizerProvider.colorize(format.replace("%place%", String.valueOf(place)).replace("%clan_name%", o.getName()).replace("%xp%", String.valueOf(o.getXp()))));
            place++;
        }

        if(list.isEmpty()) {
            chatUtil.sendMessage(player, configurationSection.getString("there_are_no_clans"));
            return true;
        }

        for(String string : list) {
            chatUtil.sendMessage(player, string);
        }

        return true;
    }
}
