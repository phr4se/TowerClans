package phrase.towerclans.menu.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.attribute.clan.LevelManager;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.menu.ItemBuilder;
import phrase.towerclans.menu.Menu;
import phrase.towerclans.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class MenuClanLevel extends Menu {
    private final ClanImpl clan;

    public MenuClanLevel(String fileName, TowerClans plugin, ModifiedPlayer modifiedPlayer) {
        super(fileName, plugin, modifiedPlayer);
        this.clan = (ClanImpl) modifiedPlayer.getClan();
        setupDefaultItems();
        setupItems();
    }

    @Override
    public void setupItems() {
        final LevelManager levelManager = clan.getLevelManager();
        int startSlot = configurationSection.getInt("slot");
        for (int level = 1; level <= levelManager.getCountLevels(); level++) {
            int finalLevel = level;
            Material material;
            String name;
            List<String> lore;
            if (clan.getLevel() < level) {
                material = Material.matchMaterial(configurationSection.getString("not-received.material"));
                name = Utils.colorizer.colorize(configurationSection.getString("not-received.name").replace("%level%", String.valueOf(level)));
                lore = configurationSection.getStringList("not-received.lore").stream().map(string ->
                        Utils.colorizer.colorize(string
                                .replace("%maximum_balance%", String.valueOf(levelManager.getLevelMaximumBalance(finalLevel)))
                                .replace("%maximum_members%", String.valueOf(levelManager.getLevelMaximumMembers(finalLevel)))
                                .replace("%available%", String.valueOf(levelManager.getAvailableSlots(finalLevel))))
                ).collect(Collectors.toList());
            } else {
                material = Material.matchMaterial(configurationSection.getString("received.material"));
                name = (Utils.colorizer.colorize(configurationSection.getString("received.name").replace("%level%", String.valueOf(level))));
                lore = configurationSection.getStringList("received.lore").stream().map(string ->
                        Utils.colorizer.colorize(string
                                .replace("%maximum_balance%", String.valueOf(levelManager.getLevelMaximumBalance(finalLevel)))
                                .replace("%maximum_members%", String.valueOf(levelManager.getLevelMaximumMembers(finalLevel)))
                                .replace("%available%", String.valueOf(levelManager.getAvailableSlots(finalLevel))))
                ).collect(Collectors.toList());
            }
            final ItemStack itemStack = new ItemBuilder(material)
                    .setName(name)
                    .setLore(lore)
                    .setHideAttributes(true)
                    .build();
            while (inventory.getItem(startSlot) != null && inventory.getItem(startSlot).getType() != Material.AIR)
                startSlot++;
            inventory.setItem(startSlot, itemStack);
            startSlot++;
        }
    }
}
