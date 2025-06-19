package phrase.towerClans.clan.event.impl;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.attribute.clan.Level;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.event.Event;
import phrase.towerClans.clan.event.SchematicManager;
import phrase.towerClans.clan.event.exception.EventAlreadyRun;
import phrase.towerClans.clan.event.exception.SchematicDamaged;
import phrase.towerClans.clan.event.exception.SchematicNotExist;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.event.LevelUpEvent;
import phrase.towerClans.util.ChatUtil;

import java.util.*;

public class Capture extends Event {

    public boolean running = false;

    private World world;
    private int minX, maxX, minY, maxY, minZ, maxZ;
    private Location pos1;
    private Location pos2;
    private ProtectedRegion region;

    private final static Map<String, Integer> PLAYERS = new HashMap<>();
    private final static Map<String, Integer> POINTS = new HashMap<>();

    private final ChatUtil chatUtil;

    public Capture(Plugin plugin) {
        super(plugin);
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public void startEvent() throws EventAlreadyRun, SchematicNotExist, SchematicDamaged {

        if (!Event.register(EventType.CAPTURE, this)) throw new EventAlreadyRun("Эвент уже запущен");

        if (!SchematicManager.existsSchematic()) throw new SchematicNotExist("Схематика не существует");

        if(SchematicManager.schematicDamaged()) throw new SchematicDamaged("Схематика повреждена");

        new BukkitRunnable() {

            @Override
            public void run() {

                ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");
                world = Bukkit.getWorld(configurationSection.getString("world"));


                int width = configurationSection.getInt("width") - 1;
                int height = configurationSection.getInt("height") - 1;
                int length = configurationSection.getInt("length") - 1;

                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));

                boolean availableCoordines;
                Random random = new Random();
                do {
                    int x = random.nextInt(1000);
                    int z = random.nextInt(1000);
                    int y = world.getHighestBlockYAt(x, z);

                    pos1 = new Location(world, x, y, z);
                    while(pos1.getBlock().getType() == Material.WATER || pos1.getBlock().getType() == Material.LAVA) pos1.setY(pos1.getY() + 2);
                    pos2 = new Location(world, pos1.getX() + width, pos1.getY() + height, pos1.getZ() + length);

                    availableCoordines = regionManager.getApplicableRegions(BlockVector3.at(pos1.getX(), pos1.getY(), pos1.getZ())).getRegions().isEmpty() && regionManager.getApplicableRegions(BlockVector3.at(pos2.getX(), pos2.getY(), pos2.getZ())).getRegions().isEmpty();

                } while (!availableCoordines);

                minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
                maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
                minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
                maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
                minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
                maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

                region = new ProtectedCuboidRegion(UUID.randomUUID().toString(), BlockVector3.at(minX, minY - 1, minZ), BlockVector3.at(maxX, maxY, maxZ));
                region.setFlag(Flags.BUILD, StateFlag.State.DENY);
                regionManager.addRegion(region);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        SchematicManager.setSchematic(world, minX, maxX, minY, maxY, minZ, maxZ);

                        running = true;

                        broadcastForPlayersAboutStartEvent();
                        enableBossBarForPlayers(minX, minY, minZ);
                        searchForPlayers();
                    }
                }.runTask(plugin);

            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void endEvent() {
        if(!running) return;
        running = false;

        WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).removeRegion(region.getId());
        Event.unRegister(EventType.CAPTURE);
        SchematicManager.regenerationBlocks(world, minX, maxX, minY, maxY, minZ, maxZ);

        broadcastForPlayersAboutEndEvent("Нет");
        disableBossBarForPlayers();

        PLAYERS.clear();
        POINTS.clear();
    }

    @Override
    public void endEvent(ClanImpl clan) {
        if(!running) return;
        running = false;

        WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).removeRegion(region.getId());
        Event.unRegister(EventType.CAPTURE);
        SchematicManager.regenerationBlocks(world, minX, maxX, minY, maxY, minZ, maxZ);

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");

        int plusXp = configurationSection.getInt("xp_for_winning");
        clan.setXp(plusXp);
        int nextLevel = clan.getLevel() + 1;
        int xp = Level.getXpLevel(nextLevel);
        if (clan.getXp() >= xp) plugin.getServer().getPluginManager().callEvent(new LevelUpEvent(clan));

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
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");

        int maxPoint = configurationSection.getInt("max_point");

        String title = configurationSection.getString("title").replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z));
        BossBar bossBar = server.createBossBar(NamespacedKey.fromString("towerclans_bossbar_event_capture"), colorizerProvider.colorize(title), BarColor.RED, BarStyle.SOLID);

        new BukkitRunnable() {
            @Override
            public void run() {

                if(!isRunning()) cancel();

                List<String> clansName = POINTS.keySet().stream().limit(1).toList();
                String clanName = (clansName.isEmpty()) ? "&cНет" : clansName.get(0);
                String newTitle = title.replace("%clan_name%", clanName);
                bossBar.setTitle(colorizerProvider.colorize(newTitle));
                int point = (POINTS.get(clanName) == null) ? 0 : POINTS.get(clanName);
                if((double) point / maxPoint <= 1.00) bossBar.setProgress((double) point / maxPoint);
                for(Player player : server.getOnlinePlayers()) {
                    if(bossBar.getPlayers().contains(player)) continue;
                    bossBar.addPlayer(player);
                }

                if(point >= maxPoint) endEvent(ClanImpl.getClans().get(clanName));


            }
        }.runTaskTimer(plugin, 0L,600L);



    }

    private void disableBossBarForPlayers() {
        Server server = plugin.getServer();

        BossBar bossBar = server.getBossBar(NamespacedKey.fromString("towerclans_bossbar_event_capture"));

        bossBar.setVisible(false);
    }

    private void searchForPlayers() {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");
        int pointPerZone = configurationSection.getInt("point_per_zone");

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!isRunning()) {
                    cancel();
                    return;
                }

                PLAYERS.clear();

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    Location location = player.getLocation();
                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();

                    if(x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
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

                if(winningClans.size() > 1) return;

                winningClans.forEach(clanName -> POINTS.compute(clanName, (k, v) -> (v == null) ? pointPerZone : v + pointPerZone));

            }


        }.runTaskTimerAsynchronously(plugin, 0L, 1200L);

    }

    public void broadcastForPlayersAboutStartEvent() {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");

        List<String> messages = configurationSection.getStringList("messages_start_event");

        for(Player player : plugin.getServer().getOnlinePlayers()) {
            for(String message : messages) {
                chatUtil.sendMessage(player, message);
            }
        }

    }

    public void broadcastForPlayersAboutEndEvent(String clanName) {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");

        List<String> messages = configurationSection.getStringList("messages_end_event");

        for(Player player : plugin.getServer().getOnlinePlayers()) {
            for(String message : messages) {
                chatUtil.sendMessage(player, message.replace("%clan_name%", clanName));
            }
        }

    }

}
