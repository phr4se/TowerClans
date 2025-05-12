package phrase.towerClans;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.attribute.clan.Rank;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.attribute.clan.Storage;
import phrase.towerClans.command.CommandLogger;
import phrase.towerClans.command.CommandMapper;
import phrase.towerClans.command.CommandResult;
import phrase.towerClans.config.Config;
import phrase.towerClans.config.impl.ConfigClans;
import phrase.towerClans.config.impl.ConfigPlayers;
import phrase.towerClans.glow.GlowPacketListener;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.listener.ClanListener;
import phrase.towerClans.listener.PlayerListener;
import phrase.towerClans.util.Placeholder;
import phrase.towerClans.util.ChatUtil;
import phrase.towerClans.util.UpdateChecker;
import phrase.towerClans.util.colorizer.ColorizerFactory;
import phrase.towerClans.util.colorizer.ColorizerProvider;
import phrase.towerClans.util.colorizer.ColorizerType;

import java.util.List;
import java.util.logging.Logger;


public final class Plugin extends JavaPlugin implements CommandExecutor {

    private Config configClans;
    private Config configPlayers;
    public Economy economy;
    private static CommandMapper commandMapper;
    private static ChatUtil chatUtil;
    private static ColorizerProvider colorizerProvider;
    private static NamespacedKey namespacedKey;

    @Override
    public void onEnable() {

        Logger logger = getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();

        namespacedKey = new NamespacedKey(this, "clans");

        saveDefaultConfig();

        if(!UpdateChecker.check().equals(getDescription().getVersion())) logger.severe("Вы используете устаревшую версию плагина");

        colorizerProvider = ColorizerFactory.getProvider(ColorizerType.HEX);

        chatUtil = new ChatUtil(this);
        commandMapper = new CommandMapper(this);
        configClans = new ConfigClans(this);
        configPlayers = new ConfigPlayers(this);
        Level.intialize(this);
        Storage.intialize(this);
        Rank.intialize(this);
        MenuPages.intialize(this);
        new CommandLogger(this);

        if (!setupEconomy()) {
            logger.severe("Vault не найден. Плагин будет выключен");
            pluginManager.disablePlugin(this);
            return;
        }

        configClans.load();
        configPlayers.load();

        getCommand("clan").setExecutor(this);

        if (pluginManager.isPluginEnabled("PlaceholderAPI")) new Placeholder().register();


        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ClanListener(this), this);

        ProtocolLibrary.getProtocolManager()
                .addPacketListener(
                        new GlowPacketListener(
                                this,
                                PacketType.Play.Server.ENTITY_EQUIPMENT
                        )
                );

    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);

        if (registeredServiceProvider == null) {
            return false;
        }

        economy = registeredServiceProvider.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        configClans.save();
        configPlayers.save();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigurationSection configurationSection = this.getConfig().getConfigurationSection("message");
        if(!(sender instanceof Player)) {
            chatUtil.sendMessage(sender, configurationSection.getString("you_are_not_a_player"));
            return true;
        }
        Player player = (Player) sender;
        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);

        if (args.length < 1) {

            if (modifiedPlayer.getClan() == null) {
                List<String> list = configurationSection.getStringList("a_player_without_a_clan");
                for (String string : list) {
                    chatUtil.sendMessage(player, string);
                }
                return true;
            }

            List<String> list = configurationSection.getStringList("a_player_with_a_clan");
            for (String string : list) {
                chatUtil.sendMessage(player, string);
            }

            return true;

        }

        CommandResult commandResult = commandMapper.mapCommand(player, args[0], args);

        if (!(commandResult.getResultStatus().equals(CommandResult.ResultStatus.SUCCESS))) {
            if (commandResult.getMessage() != null) {
                chatUtil.sendMessage(sender, commandResult.getMessage());
            }
            return true;
        }

        return true;

    }

    public static NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public static ColorizerProvider getColorizerProvider() {
        return colorizerProvider;
    }
}
