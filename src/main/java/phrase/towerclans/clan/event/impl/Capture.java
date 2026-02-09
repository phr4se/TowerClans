package phrase.towerclans.clan.event.impl;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.event.Event;
import phrase.towerclans.clan.event.SchematicManager;
import phrase.towerclans.clan.event.exception.EventAlreadyRun;
import phrase.towerclans.clan.event.exception.SchematicDamaged;
import phrase.towerclans.clan.event.exception.SchematicNotExist;
import phrase.towerclans.clan.event.privilege.PrivilegeManager;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.config.Config;
import phrase.towerclans.event.ClanLevelUpEvent;
import phrase.towerclans.util.Utils;

import java.io.File;
import java.util.*;

public class Capture extends Event {
    public boolean running = false;
    private World world;
    private Location pos1;
    private SchematicManager schematicManager;
    private final static Map<String, Integer> PLAYERS = new HashMap<>();
    private final static Map<String, Integer> POINTS = new HashMap<>();
    private ConfigurationSection configurationSection;

    public Capture(TowerClans plugin) {
        super(plugin);
    }

    @Override
    public void startEvent() throws EventAlreadyRun, SchematicNotExist, SchematicDamaged {
        configurationSection = Config.getFile("event-capture").getConfigurationSection("capture");
        schematicManager = new SchematicManager(plugin, new File(Config.getSettings().pathEventCapture()), configurationSection.getStringList("region-flags"));
        if (!Event.register(EventType.CAPTURE, this)) throw new EventAlreadyRun("Ивент уже запущен");
        if (!schematicManager.existsSchematic()) throw new SchematicNotExist("Схематика не существует");
        if (schematicManager.schematicDamaged()) throw new SchematicDamaged("Схематика повреждена");
        new BukkitRunnable() {
            @Override
            public void run() {
                world = Bukkit.getWorld(configurationSection.getString("world"));
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
                boolean availableCoordines;
                Random random = new Random();
                int xRange = configurationSection.getInt("x-range");
                int zRange = configurationSection.getInt("z-range");
                do {
                    int x = random.nextInt(xRange);
                    int z = random.nextInt(zRange);
                    int y = world.getHighestBlockYAt(x, z);
                    pos1 = new Location(world, x, y, z);
                    while (pos1.getBlock().getType() == Material.WATER || pos1.getBlock().getType() == Material.LAVA)
                        pos1.setY(pos1.getY() + 2);
                    availableCoordines = regionManager.getApplicableRegions(BlockVector3.at(pos1.getX(), pos1.getY(), pos1.getZ())).getRegions().isEmpty();
                } while (!availableCoordines);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        schematicManager.setSchematic(pos1);
                        running = true;
                        broadcastForPlayersAboutStartEvent();
                        enableBossBarForPlayers(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ());
                        searchForPlayers();
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void endEvent() {
        if (!running) return;
        running = false;
        Event.unRegister(EventType.CAPTURE);
        schematicManager.regenerationBlocks();
        broadcastForPlayersAboutEndEvent("Нет");
        disableBossBarForPlayers();
        PLAYERS.clear();
        POINTS.clear();
    }

    @Override
    public void endEvent(ClanImpl clan) {
        if (!running) return;
        running = false;
        Event.unRegister(EventType.CAPTURE);
        schematicManager.regenerationBlocks();
        int plusXp = configurationSection.getInt("xp-for-winning");
        clan.setXp(clan.getXp() + plusXp);
        int nextLevel = clan.getLevel() + 1;
        final LevelManager levelManager = plugin.getClanManager().getLevelManager();
        if (!(levelManager.getXpLevel(nextLevel) == -1)) {
            int xp = levelManager.getXpLevel(nextLevel);
            if (clan.getXp() >= xp) plugin.getServer().getPluginManager().callEvent(new ClanLevelUpEvent(clan));
        }
        broadcastForPlayersAboutEndEvent(clan.getName());
        disableBossBarForPlayers();
        PLAYERS.clear();
        POINTS.clear();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void enableBossBarForPlayers(int x, int y, int z) {
        Server server = plugin.getServer();
        int maxPoint = configurationSection.getInt("max-point");
        ConfigurationSection configurationSection = Config.getFile("event-capture.yml").getConfigurationSection("boss-bar");
        String title = configurationSection.getString("title").replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z));
        BossBar bossBar = server.createBossBar(NamespacedKey.fromString("towerclans_bossbar_event_capture"), Utils.COLORIZER.colorize(title), BarColor.valueOf(configurationSection.getString("bar-color")), BarStyle.valueOf(configurationSection.getString("bar-style")));
        long updateBossBar = configurationSection.getLong("update-boss-bar");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) cancel();
                String clanName = POINTS.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(Config.getSettings().unknownClan());
                String newTitle = title.replace("%clan_name%", clanName);
                bossBar.setTitle(Utils.COLORIZER.colorize(newTitle));
                int point = (POINTS.get(clanName) == null) ? 0 : POINTS.get(clanName);
                if ((double) point / maxPoint <= 1.00) bossBar.setProgress((double) point / maxPoint);
                for (Player player : server.getOnlinePlayers()) {
                    if (bossBar.getPlayers().contains(player)) continue;
                    bossBar.addPlayer(player);
                }
                if (point >= maxPoint) endEvent(plugin.getClanManager().getClan(clanName));
            }
        }.runTaskTimer(plugin, 0L, updateBossBar);
    }

    private void disableBossBarForPlayers() {
        Server server = plugin.getServer();
        BossBar bossBar = server.getBossBar(NamespacedKey.fromString("towerclans_bossbar_event_capture"));
        bossBar.setVisible(false);
        bossBar.removeAll();
    }

    @Override
    public boolean playerAtEvent(Player player) {
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Location pos2 = schematicManager.getPos2();
        return (x >= pos1.getBlockX() && x <= pos2.getBlockX() && y >= pos1.getBlockY() && y <= pos2.getBlockY() && z >= pos1.getBlockZ() && z <= pos2.getBlockZ());
    }

    private void searchForPlayers() {
        int pointPerZone = configurationSection.getInt("point-per-zone");
        long searchForPlayers = configurationSection.getLong("search-for-players");
        final PrivilegeManager privilegeManager = plugin.getPrivilegeManager();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isRunning()) {
                    cancel();
                    return;
                }
                PLAYERS.clear();
                for (Player player : world.getPlayers()) {
                    if (playerAtEvent(player)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (privilegeManager.hasPrivilege(player, Config.getSettings().disablePrivilegeType()))
                                    privilegeManager.disablePrivilege(player, Config.getSettings().disablePrivilegeType());
                            }
                        }.runTask(plugin);
                        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(player);
                        if (modifiedPlayer.getClan() == null) continue;
                        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
                        String clanName = clan.getName();
                        PLAYERS.compute(clanName, (k, v) -> (v == null) ? 1 : v + 1);
                    }
                }
                int maxPlayers = PLAYERS.values().stream().max(Integer::compare).orElse(0);
                List<String> winningClans = PLAYERS.entrySet().stream()
                        .filter(entry -> entry.getValue() == maxPlayers)
                        .map(Map.Entry::getKey)
                        .toList();
                if (winningClans.size() > 1) return;
                winningClans.forEach(clanName -> POINTS.compute(clanName, (k, v) -> (v == null) ? pointPerZone : v + pointPerZone));
            }
        }.runTaskTimerAsynchronously(plugin, 0L, searchForPlayers);
    }

    @Override
    public void broadcastForPlayersAboutStartEvent() {
        List<String> messages = configurationSection.getStringList("messages-start-event");
        broadcast(plugin, messages);
    }

    @Override
    public void broadcastForPlayersAboutEndEvent(String clanName) {
        List<String> messages = configurationSection.getStringList("messages-end-event").stream().map(message -> message.replace("%clan_name%", clanName)).toList();
        broadcast(plugin, messages);
    }
}
