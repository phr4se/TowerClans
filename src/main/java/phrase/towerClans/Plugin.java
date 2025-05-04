package phrase.towerClans;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.listener.ClanListener;
import phrase.towerClans.config.ConfigManager;
import phrase.towerClans.listener.PlayerListener;
import phrase.towerClans.util.PluginPlaceholder;
import phrase.towerClans.util.ChatUtil;
import phrase.towerClans.util.UpdateChecker;
import phrase.towerClans.util.colorizer.ColorizerFactory;
import phrase.towerClans.util.colorizer.ColorizerProvider;
import phrase.towerClans.util.colorizer.ColorizerType;

import java.util.List;
import java.util.logging.Logger;


public final class Plugin extends JavaPlugin implements CommandExecutor {

    private ConfigManager configManager;
    public Economy economy;
    private static CommandMapper commandMapper;
    private static ChatUtil chatUtil;
    private static ColorizerProvider colorizerProvider;

    @Override
    public void onEnable() {

        Logger logger = getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();

        saveDefaultConfig();

        if(!UpdateChecker.check().equals(getDescription().getVersion())) logger.severe("Вы используете устаревшую версию плагина");

        colorizerProvider = ColorizerFactory.getProvider(ColorizerType.HEX);

        chatUtil = new ChatUtil(this);
        configManager = new ConfigManager(this);
        commandMapper = new CommandMapper(this);
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

        configManager.loadClans();
        configManager.loadPlayers();

        getCommand("clan").setExecutor(this);

        if (pluginManager.getPlugin("PlaceholderAPI") != null) new PluginPlaceholder().register();

        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new ClanListener(this), this);

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
        configManager.saveClans();
        configManager.savePlayers();
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

    public static ColorizerProvider getColorizerProvider() {
        return colorizerProvider;
    }
}
