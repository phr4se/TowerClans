package phrase.towerClans.clan;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impls.ClanImpl;
import phrase.towerClans.utils.HexUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractClan implements Clan {

    private String name;
    private Map<ModifiedPlayer, String> members;
    private int level;
    private int xp;
    private int balance;
    private boolean pvp;

    public AbstractClan() {
    }

    public AbstractClan(String name) {
        this.name = name;

        members = new HashMap<>();
        level = LevelType.ONE.getId();
        xp = LevelType.ONE.getXp();
        balance = 0;
        pvp = false;
    }

    public void showMenu(ModifiedPlayer modifiedPlayer, int id) {

        if(id == 1) {
            Inventory menu = Bukkit.createInventory(null, 45, "Клан " + name);

            ItemStack redStainedGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);

            menu.setItem(0, redStainedGlassPane);
            menu.setItem(1, redStainedGlassPane);
            menu.setItem(2, redStainedGlassPane);
            menu.setItem(3, redStainedGlassPane);
            menu.setItem(4, redStainedGlassPane);
            menu.setItem(5, redStainedGlassPane);
            menu.setItem(6, redStainedGlassPane);
            menu.setItem(7, redStainedGlassPane);
            menu.setItem(8, redStainedGlassPane);
            menu.setItem(9, redStainedGlassPane);
            menu.setItem(17, redStainedGlassPane);
            menu.setItem(18, redStainedGlassPane);
            menu.setItem(26, redStainedGlassPane);
            menu.setItem(27, redStainedGlassPane);
            menu.setItem(35, redStainedGlassPane);
            menu.setItem(36, redStainedGlassPane);
            menu.setItem(37, redStainedGlassPane);
            menu.setItem(38, redStainedGlassPane);
            menu.setItem(39, redStainedGlassPane);
            menu.setItem(40, redStainedGlassPane);
            menu.setItem(41, redStainedGlassPane);
            menu.setItem(42, redStainedGlassPane);
            menu.setItem(43, redStainedGlassPane);
            menu.setItem(44, redStainedGlassPane);

            int maximumBalance = LevelType.getLevelMaximumBalance(getLevel());

            ItemStack knowledgeBook = new ItemStack(Material.KNOWLEDGE_BOOK);
            ItemMeta knowledgeBookMeta = knowledgeBook.getItemMeta();
            knowledgeBookMeta.setDisplayName(HexUtil.color(Plugin.instance.getConfig().getString("settings.menu.menu_clan.items.information.title")));
            List<String> list = Plugin.instance.getConfig().getStringList("settings.menu.menu_clan.items.information.title");
            List<String> streamList = list.stream().map(string -> {
                        String replacedString = string
                        .replace("%name%", name)
                        .replace("%members%", String.valueOf(members.size()))
                        .replace("%level%", String.valueOf(level))
                        .replace("%pvp%", (pvp) ? "Да" : "Нет")
                        .replace("%maximum_balance%", String.valueOf(maximumBalance));

                        return HexUtil.color(replacedString);
                    }).collect(Collectors.toList());

            knowledgeBookMeta.setLore(streamList);

            knowledgeBook.setItemMeta(knowledgeBookMeta);

            ItemStack totemOfUndying = new ItemStack(Material.TOTEM_OF_UNDYING);
            ItemMeta totemOfUndyingItemMeta = totemOfUndying.getItemMeta();
            totemOfUndyingItemMeta.setDisplayName(HexUtil.color(Plugin.instance.getConfig().getString("settings.menu.menu_clan.items.members_clan.title")));
            list = Plugin.instance.getConfig().getStringList("settings.menu.menu_clan.items.members_clan.lore");
            streamList = list.stream().map(string -> {
                String replacedString = string
                        .replace("%name%", name)
                        .replace("%members%", String.valueOf(members.size()))
                        .replace("%level%", String.valueOf(level))
                        .replace("%pvp%", (pvp) ? "Да" : "Нет")
                        .replace("%maximum_balance%", String.valueOf(maximumBalance));

                return HexUtil.color(replacedString);
            }).collect(Collectors.toList());
            totemOfUndyingItemMeta.setLore(streamList);

            totemOfUndying.setItemMeta(totemOfUndyingItemMeta);

            ItemStack diamond = new ItemStack(Material.DIAMOND);
            ItemMeta diamondMeta = diamond.getItemMeta();

            diamondMeta.setDisplayName(HexUtil.color(Plugin.instance.getConfig().getString("settings.menu.menu_clan.items.level_clan.title")));
            list = Plugin.instance.getConfig().getStringList("settings.menu.menu_clan.items.level_clan.lore");
            streamList = list.stream().map(string -> {
                String replacedString = string
                        .replace("%name%", name)
                        .replace("%members%", String.valueOf(members.size()))
                        .replace("%level%", String.valueOf(level))
                        .replace("%pvp%", (pvp) ? "Да" : "Нет")
                        .replace("%maximum_balance%", String.valueOf(maximumBalance));

                return HexUtil.color(replacedString);
            }).collect(Collectors.toList());
            diamondMeta.setLore(streamList);

            diamond.setItemMeta(diamondMeta);

            ItemStack spectralArrow = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta spectralArrowMeta = spectralArrow.getItemMeta();
            spectralArrowMeta.setDisplayName(HexUtil.color(Plugin.instance.getConfig().getString("settings.menu.menu_clan.items.exit.title")));
            spectralArrow.setItemMeta(spectralArrowMeta);

            menu.setItem(34, spectralArrow);
            menu.setItem(10, knowledgeBook);
            menu.setItem(11, totemOfUndying);
            menu.setItem(12, diamond);

            modifiedPlayer.getPlayer().openInventory(menu);
            return;
        }

        if(id == 2) {

            Inventory menu = Bukkit.createInventory(null, 45, "Участники клана");

            ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

            ItemStack redStainedGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            menu.setItem(0, redStainedGlassPane);
            menu.setItem(1, redStainedGlassPane);
            menu.setItem(2, redStainedGlassPane);
            menu.setItem(3, redStainedGlassPane);
            menu.setItem(4, redStainedGlassPane);
            menu.setItem(5, redStainedGlassPane);
            menu.setItem(6, redStainedGlassPane);
            menu.setItem(7, redStainedGlassPane);
            menu.setItem(8, redStainedGlassPane);
            menu.setItem(9, redStainedGlassPane);
            menu.setItem(17, redStainedGlassPane);
            menu.setItem(18, redStainedGlassPane);
            menu.setItem(26, redStainedGlassPane);
            menu.setItem(27, redStainedGlassPane);
            menu.setItem(35, redStainedGlassPane);
            menu.setItem(36, redStainedGlassPane);
            menu.setItem(37, redStainedGlassPane);
            menu.setItem(38, redStainedGlassPane);
            menu.setItem(39, redStainedGlassPane);
            menu.setItem(40, redStainedGlassPane);
            menu.setItem(41, redStainedGlassPane);
            menu.setItem(42, redStainedGlassPane);
            menu.setItem(43, redStainedGlassPane);
            menu.setItem(44, redStainedGlassPane);

            ItemStack spectralArrow = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta spectralArrowMeta = spectralArrow.getItemMeta();
            spectralArrowMeta.setDisplayName("В меню");
            spectralArrow.setItemMeta(spectralArrowMeta);
            menu.setItem(34, spectralArrow);

            int slot = 10;
            for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {

                if (menu.getItem(slot) == null) {
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                    if (Bukkit.getPlayer(entry.getKey().getPlayer().getName()) == null)
                        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey().getPlayer().getName()));
                    else skullMeta.setOwningPlayer(Bukkit.getPlayer(entry.getKey().getPlayer().getName()));
                    skullMeta.setLore(Arrays.asList("Ранг " + clan.getMembers().get(entry.getKey())));
                    skullMeta.setDisplayName("Игрок " + entry.getKey().getPlayer().getName());
                    skull.setItemMeta(skullMeta);
                    menu.setItem(slot, skull);
                    slot++;
                } else {
                    slot++;
                }

            }

            modifiedPlayer.getPlayer().openInventory(menu);
            return;

        }

        if(id == 3) {

            Inventory menu = Bukkit.createInventory(null, 45, "Уровень клана");

            ItemStack redStainedGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            menu.setItem(0, redStainedGlassPane);
            menu.setItem(1, redStainedGlassPane);
            menu.setItem(2, redStainedGlassPane);
            menu.setItem(3, redStainedGlassPane);
            menu.setItem(4, redStainedGlassPane);
            menu.setItem(5, redStainedGlassPane);
            menu.setItem(6, redStainedGlassPane);
            menu.setItem(7, redStainedGlassPane);
            menu.setItem(8, redStainedGlassPane);
            menu.setItem(9, redStainedGlassPane);
            menu.setItem(17, redStainedGlassPane);
            menu.setItem(18, redStainedGlassPane);
            menu.setItem(26, redStainedGlassPane);
            menu.setItem(27, redStainedGlassPane);
            menu.setItem(35, redStainedGlassPane);
            menu.setItem(36, redStainedGlassPane);
            menu.setItem(37, redStainedGlassPane);
            menu.setItem(38, redStainedGlassPane);
            menu.setItem(39, redStainedGlassPane);
            menu.setItem(40, redStainedGlassPane);
            menu.setItem(41, redStainedGlassPane);
            menu.setItem(42, redStainedGlassPane);
            menu.setItem(43, redStainedGlassPane);
            menu.setItem(44, redStainedGlassPane);

            ItemStack spectralArrow = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta spectralArrowMeta = spectralArrow.getItemMeta();
            spectralArrowMeta.setDisplayName("В меню");
            spectralArrow.setItemMeta(spectralArrowMeta);
            menu.setItem(34, spectralArrow);



            int slot = 10;
             for(int i = 1; i <= LevelType.countLevel; i++) {

                 if (getLevel() < i) {
                     ItemStack furnaceMinecart = new ItemStack(Material.FURNACE_MINECART);
                     ItemMeta furnaceMinecartMeta = furnaceMinecart.getItemMeta();
                     furnaceMinecartMeta.setDisplayName("Уровень " + i);
                     furnaceMinecartMeta.setLore(Arrays.asList(
                             "Не получен",
                             "При получении будет доступно: ",
                             " Лимит баланса увеличен до " + LevelType.getLevelMaximumBalance(i)
                     ));
                     furnaceMinecart.setItemMeta(furnaceMinecartMeta);
                     menu.setItem(slot, furnaceMinecart);
                     slot++;
                     continue;
                 }

                 ItemStack chestMinecart = new ItemStack(Material.CHEST_MINECART);
                 ItemMeta chestMinecartMeta = chestMinecart.getItemMeta();
                 chestMinecartMeta.setDisplayName("Уровень " + i);
                 chestMinecartMeta.setLore(Arrays.asList(
                         "Получен",
                         "Доступно: ",
                         " Лимит баланса " + LevelType.getLevelMaximumBalance(i)
                 ));
                 chestMinecart.setItemMeta(chestMinecartMeta);
                 menu.setItem(slot, chestMinecart);
                 slot++;

             }

            modifiedPlayer.getPlayer().openInventory(menu);
        }

    }

    public enum MenuType {

        MENU_CLAN(1),
        MENU_CLAN_MEMBERS(2),
        MENU_LEVEL_CLAN(3);

        private int id;

        MenuType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum RankType {
        LEADER("Лидер"),
        DEPUTY("Заместитель"),
        MEMBER("Участник");

        private String name;
        RankType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum LevelType {

        ONE(20, 250000, 0,1),
        TWO(20, 500000, 1000,2),
        THREE(20, 1000000, 1500, 3),
        FOUR(20, 1750000, 2500,4),
        FIVE(20, 2000000, 5000,5);

        private int maximumMembers;
        private int maximumBalance;
        private int xp;
        private int id;
        public static final int countLevel = 5;

        LevelType(int maximumMembers, int maximumBalance, int xp,int id) {
            this.maximumMembers = maximumMembers;
            this.maximumBalance = maximumBalance;
            this.xp = xp;
            this.id = id;
        }

        public static int getXpLevel(int level) {
            LevelType levelType = null;

            switch (++level) {
                case 1:
                    levelType = LevelType.ONE;
                    break;
                case 2:
                    levelType = LevelType.TWO;
                    break;
                case 3:
                    levelType = LevelType.THREE;
                    break;
                case 4:
                    levelType = LevelType.FOUR;
                    break;
                case 5:
                    levelType = LevelType.FIVE;
            }

            return levelType.getXp();
        }

        public static int getLevelMaximumBalance(int level) {
            int maximumBalance = 0;
            switch (level) {
                case 1:
                    maximumBalance = LevelType.ONE.getMaximumBalance();
                    break;
                case 2:
                    maximumBalance = LevelType.TWO.getMaximumBalance();
                    break;
                case 3:
                    maximumBalance = LevelType.THREE.getMaximumBalance();
                    break;
                case 4:
                    maximumBalance = LevelType.FOUR.getMaximumBalance();
                    break;
                case 5:
                    maximumBalance = LevelType.FIVE.getMaximumBalance();
            }

            return maximumBalance;
        }

        public int getMaximumMembers() {
            return maximumMembers;
        }

        public int getMaximumBalance() {
            return maximumBalance;
        }

        public int getXp() {
            return xp;
        }

        public int getId() {
            return id;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<ModifiedPlayer, String> getMembers() {
        return members;
    }

    public void setMembers(Map<ModifiedPlayer, String> members) {
        this.members = members;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

}
