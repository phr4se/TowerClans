package phrase.towerClans;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.commands.CommandLogger;
import phrase.towerClans.commands.CommandMapper;
import phrase.towerClans.commands.CommandResult;
import phrase.towerClans.config.ConfigManager;
import phrase.towerClans.listener.EventListener;
import phrase.towerClans.placeholder.PluginPlaceholder;
import phrase.towerClans.utils.ChatUtil;

import java.util.List;


public final class Plugin extends JavaPlugin implements CommandExecutor {

    private ConfigManager configManager;
    public Economy economy;
    private static CommandMapper commandMapper;
    private static ChatUtil chatUtil;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        chatUtil = new ChatUtil(this);
        configManager = new ConfigManager(this);
        commandMapper = new CommandMapper(this);
        new CommandLogger(this);

        if (!setupEconomy()) {
            getLogger().severe("Vault не найден. Плагин будет выключен");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager.loadClans();
        configManager.loadPlayers();

        getCommand("clan").setExecutor(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PluginPlaceholder().register();

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

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
}
