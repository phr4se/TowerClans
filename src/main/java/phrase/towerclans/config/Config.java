package phrase.towerclans.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import phrase.towerclans.Plugin;
import phrase.towerclans.config.data.CommandMessages;
import phrase.towerclans.config.data.Messages;
import phrase.towerclans.config.data.Settings;
import phrase.towerclans.database.DatabaseType;
import phrase.towerclans.util.Utils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Config {
    public static Plugin plugin;
    private static String prefix;
    private static Messages messages;
    private static CommandMessages commandMessages;
    private static Settings settings;

    public static void setupMessages(FileConfiguration fileConfiguration) {
        ConfigurationSection configurationSectionMessages = fileConfiguration.getConfigurationSection("messages");
        prefix = Utils.COLORIZER.colorize(configurationSectionMessages.getString("prefix"));
        messages = new Messages(getMessagePrefixed(configurationSectionMessages.getString("no-permission"), prefix),
                getMessagePrefixed(configurationSectionMessages.getStringList("not-in-clan"), prefix),
                getMessagePrefixed(configurationSectionMessages.getStringList("in-clan"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("clan-level-up"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("not-player"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("unknown-command"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("error"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("clan-name-limit"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("clan-name-bad-word"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("no-clan"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("place-format"), prefix),
                getMessagePrefixed(configurationSectionMessages.getString("use-blocked-command"), prefix)
        );
        ConfigurationSection configurationSectionCommandMessages = fileConfiguration.getConfigurationSection("messages.command");
        commandMessages = new CommandMessages(getMessagePrefixed(configurationSectionCommandMessages.getString("no-permission"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("message-format"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("incorrect-arguments"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("clan-name-exists"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-enough"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("creating-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("open-clan-menu"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("player-not-found"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("invite-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("invited-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("no-invite-yourself"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("notification-invited"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-invited"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("accept-invited"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("player-accept-invited"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("no-place-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("decline-invited"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("player-decline-invited"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("player-not-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("kick-player-with-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("kicked-with-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-leave-with-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("notification-kicked"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-kick-yourself"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("put-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("no-place-currency-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("notification-put"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("withdraw-with-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-currency-in-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("notification-withdraw"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("leave-with-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("notification-leave"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("enable-pvp"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("disable-pvp"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("player-offline"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("give-rank"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("giving-rank"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("notification-rank"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-give-rank-leader"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("rank-no-exists"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-change-rank-yourself"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("delete-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-leader"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("notification-disband"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("reload-config"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("player-not-in-yourself-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getStringList("statistic-player"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("clan-no-exists"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getStringList("information-clan"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("no-base"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("teleport-base"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("set-base"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("delete-base"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("open-storage"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("enable-glow"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("disable-glow"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("runned-event"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("stopped-event"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("already-running"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("not-running"), prefix),
                getMessagePrefixed(configurationSectionCommandMessages.getString("schematic-damaged"), prefix)
        );
    }

    public static void setupSettings(FileConfiguration fileConfiguration) {
        ConfigurationSection configurationSectionSettings = fileConfiguration.getConfigurationSection("settings");
        final File schematicFolder = new File(plugin.getDataFolder() + "/schematics");
        if (!schematicFolder.exists()) schematicFolder.mkdirs();
        settings = new Settings(DatabaseType.valueOf(configurationSectionSettings.getString("database")),
                configurationSectionSettings.getInt("cost-creating-clan"),
                configurationSectionSettings.getInt("xp-for-murder"),
                configurationSectionSettings.getInt("min-size-clan-name"),
                configurationSectionSettings.getInt("max-size-clan-name"),
                getMessagePrefixed(configurationSectionSettings.getStringList("bad-words"), prefix),
                configurationSectionSettings.getString("unknown-clan"),
                configurationSectionSettings.getString("type"),
                plugin.getPrivilegeManager().transformation(configurationSectionSettings.getStringList("disable-privilege-type")),
                configurationSectionSettings.getString("symbol-one"),
                configurationSectionSettings.getString("symbol-two"),
                configurationSectionSettings.getInt("xp-for-kill-player"),
                configurationSectionSettings.getInt("xp-for-break-block"),
                configurationSectionSettings.getStringList("white-blocks").stream().map(Material::valueOf).collect(Collectors.toList()),
                configurationSectionSettings.getString(plugin.getDataFolder().getAbsolutePath() + "/schematics/" + configurationSectionSettings.getString("settings.event.capture.schematic_name") + ".schem")
        );
    }

    public static String getMessagePrefixed(String message, String prefix) {
        if (message == null || prefix == null) {
            return message;
        }
        return Utils.COLORIZER.colorize(message.replace("%prefix%", prefix));
    }

    public static List<String> getMessagePrefixed(List<String> messages, String prefix) {
        if (messages == null || messages.isEmpty() || prefix == null) {
            return messages;
        }
        return messages.stream().map(message -> getMessagePrefixed(message, prefix)).toList();
    }

    public static void createFiles(String... filesName) {
        for (String fileName : filesName) {
            File file = new File(plugin.getDataFolder(), fileName);
            if (!file.exists()) {
                plugin.saveResource(fileName, false);
            }
        }
    }

    public static FileConfiguration getFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static Messages getMessages() {
        return messages;
    }

    public static CommandMessages getCommandMessages() {
        return commandMessages;
    }

    public static Settings getSettings() {
        return settings;
    }
}
