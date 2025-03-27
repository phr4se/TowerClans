package phrase.towerClans.clan.impls;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.*;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.HexUtil;
import phrase.towerClans.utils.ItemBuilder;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ClanImpl extends AbstractClan {

    private static final Map<String, ClanImpl> CLANS = new HashMap<>();
    private final Plugin plugin;
    private final ChatUtil chatUtil;

    public ClanImpl(String name, Plugin plugin) {
        super(name);
        this.plugin = plugin;
        chatUtil = new ChatUtil(plugin);
    }

    @Override
    public ClanResponse invite(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.accept");
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (clan.getMembers().containsKey(modifiedPlayer)) return new ClanResponse(configurationSection.getString("you_are_in_a_clan"), ClanResponse.ResponseType.FAILURE);
        int maximumMembers = Level.getLevelMaximumMembers(clan.getLevel());
        if ((clan.getMembers().size() + 1) > maximumMembers) return new ClanResponse(configurationSection.getString("there_is_no_place_in_the_clan"), ClanResponse.ResponseType.FAILURE);
        configurationSection = plugin.getConfig().getConfigurationSection("message.command.invite");
        clan.getMembers().put(modifiedPlayer, RankType.MEMBER.getName());

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_the_invitation").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse kick(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.kick");
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (!clan.getMembers().containsKey(modifiedPlayer)) return new ClanResponse(configurationSection.getString("the_player_is_not_in_the_clan"), ClanResponse.ResponseType.FAILURE);
        clan.getMembers().remove(modifiedPlayer);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse invest(ModifiedPlayer modifiedPlayer, int amount) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.invest");
        if (plugin.economy.getBalance(modifiedPlayer.getPlayer()) < amount) return new ClanResponse(configurationSection.getString("you_don't_have_enough"), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        int maximumBalance = Level.getLevelMaximumBalance(clan.getLevel());
        if ((clan.getBalance() + amount) > maximumBalance) return new ClanResponse(configurationSection.getString("there_is_no_place_in_the_clan"), ClanResponse.ResponseType.FAILURE);
        plugin.economy.withdrawPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() + amount);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_investment").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse withdraw(ModifiedPlayer modifiedPlayer, int amount) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.withdraw");
        if (getBalance() < amount) return new ClanResponse(configurationSection.getString("not_in_the_clan"), ClanResponse.ResponseType.FAILURE);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        plugin.economy.depositPlayer(modifiedPlayer.getPlayer(), amount);
        clan.setBalance(clan.getBalance() - amount);

        for (Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_withdrawal").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse leave(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.leave");
        if (!getMembers().containsKey(modifiedPlayer)) return new ClanResponse(configurationSection.getString("you're_not_in_the_clan)"), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        clan.getMembers().remove(modifiedPlayer);

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configurationSection.getString("notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse rank(ModifiedPlayer modifiedPlayer, int id) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.rank");
        if (id == 1) return new ClanResponse(configurationSection.getString("you_can't_give_out_a_leader_rank"), ClanResponse.ResponseType.FAILURE);
        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        if (id == 2) clan.getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), RankType.DEPUTY.getName());
        else if (id == 3)
            clan.getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), RankType.MEMBER.getName());
        else return new ClanResponse(configurationSection.getString("this_rank_does_not_exist"), ClanResponse.ResponseType.FAILURE);
        ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("message.command.rank");

        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            String string = configSection.getString("notification_of_rank").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", (id == 2) ? RankType.DEPUTY.getName() : RankType.MEMBER.getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public ClanResponse disband(ModifiedPlayer modifiedPlayer) {
        ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("message.command.disband");
        if (!getMembers().get(modifiedPlayer).equals("Лидер")) return new ClanResponse(configurationSection.getString("you_are_not_a_leader"), ClanResponse.ResponseType.FAILURE);

        ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();
        for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
            entry.getKey().setClan(null);

            chatUtil.sendMessage(entry.getKey().getPlayer(), configurationSection.getString("notification_of_disband"));
        }

        CLANS.remove((clan).getName());
        return new ClanResponse(null, ClanResponse.ResponseType.SUCCESS);
    }

    @Override
    public void showMenu(ModifiedPlayer modifiedPlayer, int id) {
        Inventory menu;

        switch (id) {
            case 1, 4:
                menu = MenuType.getMenu((ClanImpl) modifiedPlayer.getClan(), 1, plugin);
                modifiedPlayer.getPlayer().openInventory(menu);
                break;
            case 2:
                menu = MenuType.getMenu((ClanImpl) modifiedPlayer.getClan(), 2, plugin);
                modifiedPlayer.getPlayer().openInventory(menu);
                break;
            case 3:
                menu = MenuType.getMenu((ClanImpl) modifiedPlayer.getClan(), 3, plugin);
                modifiedPlayer.getPlayer().openInventory(menu);
                break;
            case 5:
                modifiedPlayer.getPlayer().closeInventory();
                break;
        }

    }

    public enum MenuType {

        MENU_CLAN_MAIN(1),
        MENU_CLAN_MEMBERS(2),
        MENU_CLAN_LEVEL(3),
        MENU_CLAN_BACK(4),
        MENU_CLAN_EXIT(5);

        private final int id;

        MenuType(int id) {
            this.id = id;
        }

        public static Inventory getMenu(ClanImpl clan, int id, Plugin plugin) {

            Inventory menu = null;

            if(id == 1) {

                ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_main");

                int size = configurationSection.getInt("size");
                String titleMenu = configurationSection.getString("title").replace("%clan_name%", clan.getName());
                menu = Bukkit.createInventory(null, size, HexUtil.color(titleMenu));

                configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_main.items");

                Material material;
                int slot;
                String titleItem;
                List<String> lore;

                for(String key : configurationSection.getKeys(false)) {

                    material = Material.matchMaterial(configurationSection.getString(key + ".material"));
                    slot = configurationSection.getInt(key + ".slot");
                    titleItem = HexUtil.color(configurationSection.getString(key + ".title"));
                    lore = configurationSection.getStringList(key + ".lore").stream().map(
                            string -> {
                                String replacedString = string
                                        .replace("%name%", clan.getName())
                                        .replace("%members%", String.valueOf(clan.getMembers().size()))
                                        .replace("%maximum_members%", String.valueOf(Level.getLevelMaximumMembers(clan.getLevel())))
                                        .replace("%level%", String.valueOf(clan.getLevel()))
                                        .replace("%xp%", String.valueOf(clan.getXp()))
                                        .replace("%balance%", String.valueOf(clan.getBalance()))
                                        .replace("%pvp%", (clan.isPvp()) ? "Да" : "Нет")
                                        .replace("%maximum_balance%", String.valueOf(Level.getLevelMaximumBalance(clan.getLevel())))
                                        .replace("%kills%", String.valueOf(PlayerStats.getKillsMembers(clan.getMembers())))
                                        .replace("%deaths%", String.valueOf(PlayerStats.getDeathMembers((clan.getMembers()))));

                                return HexUtil.color(replacedString);
                            }
                    ).collect(Collectors.toList());

                    if(configurationSection.contains(key + ".actions_when_clicking")) {

                        String action = configurationSection.getString(key + ".actions_when_clicking");

                        ItemStack item = new ItemBuilder(material)
                                .setName(titleItem)
                                .setLore(lore)
                                .setPersistentDataContainer(NamespacedKey.fromString("action"), PersistentDataType.STRING, action)
                                .build();

                        menu.setItem(slot, item);
                        continue;
                    }

                    ItemStack item = new ItemBuilder(material)
                            .setName(titleItem)
                            .setLore(lore)
                            .build();

                    menu.setItem(slot, item);

                }

                return menu;

            }

            if(id == 2) {

                ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members");
                int size = configurationSection.getInt("size");
                String titleMenu = configurationSection.getString("title_menu");

                menu = Bukkit.createInventory(null, size, HexUtil.color(titleMenu));

                Material material;
                int slot;
                String titleItem;
                List<String> lore;

                material = Material.matchMaterial(configurationSection.getString("material"));
                slot = configurationSection.getInt("slot");
                titleItem = configurationSection.getString("title_item");
                lore = configurationSection.getStringList("lore");

                for(Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {
                    ModifiedPlayer modifiedPlayer = entry.getKey();
                    titleItem = HexUtil.color(titleItem.replace("%player_name%", modifiedPlayer.getPlayer().getName()));
                    PlayerStats playerStats = PlayerStats.PLAYERS.get(modifiedPlayer.getPlayerUUID());
                    lore = lore.stream().map(
                            string -> {
                                String replacedString = string
                                        .replace("%player_rank%", entry.getValue())
                                        .replace("%player_kills%", String.valueOf(playerStats.getKills()))
                                        .replace("%player_deaths%", String.valueOf(playerStats.getDeaths()));
                                return HexUtil.color(replacedString);
                            }
                    ).collect(Collectors.toList());

                    ItemStack item = new ItemBuilder(material)
                            .setName(titleItem)
                            .setLore(lore)
                            .build();

                    menu.setItem(slot, item);
                    slot++;
                }

                configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_members.items");

                for(String key : configurationSection.getKeys(false)) {

                    material = Material.matchMaterial(configurationSection.getString(key + ".material"));
                    slot = configurationSection.getInt(key + ".slot");
                    titleItem = HexUtil.color(configurationSection.getString(key + ".title"));
                    lore = configurationSection.getStringList(key + ".lore").stream().map(HexUtil::color).toList();

                    if(configurationSection.contains(key + ".actions_when_clicking")) {

                        String action = configurationSection.getString(key + ".actions_when_clicking");

                        ItemStack item = new ItemBuilder(material)
                                .setName(titleItem)
                                .setLore(lore)
                                .setPersistentDataContainer(NamespacedKey.fromString("action"), PersistentDataType.STRING, action)
                                .build();

                        menu.setItem(slot, item);
                        continue;

                    }

                    ItemStack item = new ItemBuilder(material)
                            .setName(titleItem)
                            .setLore(lore)
                            .build();

                    menu.setItem(slot, item);
                }

                return menu;

            }

           if(id == 3) {

               ConfigurationSection configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_level");
               int size = configurationSection.getInt("size");
               String titleMenu = configurationSection.getString("title");

               menu = Bukkit.createInventory(null, size, HexUtil.color(titleMenu));

               Material material;
               int slot = configurationSection.getInt("slot");
               String titleItem;
               List<String> lore;

               for (int level = 1; level <= Level.levels.size(); level++) {
                   int finalLevel = level;
                   if (clan.getLevel() < level) {
                       material = Material.matchMaterial(configurationSection.getString("not_received.material"));
                       titleItem = HexUtil.color(configurationSection.getString("not_received.title").replace("%level%", String.valueOf(level)));
                       lore = configurationSection.getStringList("not_received.lore").stream().map(string -> {
                           String replacedString = string
                                   .replace("%maximum_balance%", String.valueOf(Level.getLevelMaximumBalance(finalLevel)))
                                   .replace("%maximum_members%", String.valueOf(Level.getLevelMaximumMembers(finalLevel)));
                           return HexUtil.color(replacedString);
                       }).collect(Collectors.toList());

                       ItemStack item = new ItemBuilder(material)
                               .setName(titleItem)
                               .setLore(lore)
                               .build();

                       menu.setItem(slot, item);
                       slot++;
                       continue;
                   }

                   material = Material.matchMaterial(configurationSection.getString("received.material"));
                   titleItem = (HexUtil.color(configurationSection.getString("received.title").replace("%level%", String.valueOf(level))));
                   lore = configurationSection.getStringList("received.lore").stream().map(string -> {
                       String replacedString = string
                               .replace("%maximum_balance%", String.valueOf(Level.getLevelMaximumBalance(finalLevel)))
                               .replace("%maximum_members%", String.valueOf(Level.getLevelMaximumMembers(finalLevel)));
                       return HexUtil.color(replacedString);
                   }).collect(Collectors.toList());

                   ItemStack item = new ItemBuilder(material)
                           .setName(titleItem)
                           .setLore(lore)
                           .build();

                   menu.setItem(slot, item);
                   slot++;

               }

               configurationSection = plugin.getConfig().getConfigurationSection("settings.menu.menu_clan_level.items");

               for(String key : configurationSection.getKeys(false)) {

                   material = Material.matchMaterial(configurationSection.getString(key + ".material"));
                   slot = configurationSection.getInt(key + ".slot");
                   titleItem = HexUtil.color(configurationSection.getString(key + ".title"));
                   lore = configurationSection.getStringList(key + ".lore").stream().map(HexUtil::color).toList();

                   if(configurationSection.contains(key + ".actions_when_clicking")) {

                       String action = configurationSection.getString(key + ".actions_when_clicking");

                       ItemStack item = new ItemBuilder(material)
                               .setName(titleItem)
                               .setLore(lore)
                               .setPersistentDataContainer(NamespacedKey.fromString("action"), PersistentDataType.STRING, action)
                               .build();

                       menu.setItem(slot, item);
                       continue;
                   }

                   ItemStack item = new ItemBuilder(material)
                           .setName(titleItem)
                           .setLore(lore)
                           .build();

                   menu.setItem(slot, item);

               }


           }


            return menu;


        }

        public int getId() {
            return id;
        }



        public static boolean identical(Inventory o1, Inventory o2) {

            ItemStack[] items1 = o1.getContents();
            ItemStack[] items2 = o2.getContents();

            if(items1.length != items2.length) return false;

            for(int i = 0; i < items1.length; i++) {
                ItemStack item1 = items1[i];
                ItemStack item2 = items2[i];

                if(item1 == null && item2 == null) continue;

                if(item1 == null || item2 == null) return false;

                if(!item1.isSimilar(item2)) return false;

            }

            return true;
        }

    }




    public static Map<String, ClanImpl> getClans() {
        return CLANS;
    }
}