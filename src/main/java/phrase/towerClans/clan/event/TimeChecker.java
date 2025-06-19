package phrase.towerClans.clan.event;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.event.exception.EventAlreadyRun;
import phrase.towerClans.clan.event.exception.SchematicDamaged;
import phrase.towerClans.clan.event.exception.SchematicNotExist;
import phrase.towerClans.clan.event.impl.Capture;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimeChecker {

    private final Plugin plugin;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TimeChecker(Plugin plugin) {
        this.plugin = plugin;
    }

    public void start() {

        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.event.capture");
        List<String> timeStartEvent = configurationSection.getStringList("time_start_event");

        new BukkitRunnable() {

            @Override
            public void run() {

                if(Event.isRunningEvent()) return;

                LocalTime localTime = LocalTime.now();
                String format = dateTimeFormatter.format(localTime);

                if(timeStartEvent.contains(format)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Event event = new Capture(plugin);
                            try {
                                event.startEvent();
                            } catch (EventAlreadyRun e) {
                                plugin.getLogger().severe(e.getMessage());
                            } catch (SchematicNotExist | SchematicDamaged e) {
                                plugin.getLogger().severe(e.getMessage());
                                Event.unRegister(Event.EventType.CAPTURE);
                            }
                            cancel();
                        }
                    }.runTask(plugin);
                    cancel();
                }

            }

        }.runTaskTimerAsynchronously(plugin, 0L, 20L);

    }

}
