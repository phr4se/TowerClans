package phrase.towerClans.clan.impls;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.AbstractClan;
import phrase.towerClans.clan.Clan;
import phrase.towerClans.clan.ModifiedPlayer;
import phrase.towerClans.utils.ChatUtil;
import phrase.towerClans.utils.HexUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ClanImpl extends AbstractClan implements Listener {

    private static final Map<String, ClanImpl> CLANS = new HashMap<>();
    private ChatUtil chatUtil = new ChatUtil();

    public ClanImpl() {
    }


    public ClanImpl(String name) {
        super(name);
    }

    @Override
    public boolean invite(ModifiedPlayer modifiedPlayer) {
        if (getMembers().containsKey(modifiedPlayer)) return false;
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invite");
        getMembers().put(modifiedPlayer, RankType.MEMBER.getName());

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configSection.getString("notification_of_the_invitation").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean kick(ModifiedPlayer modifiedPlayer) {
        if(!getMembers().containsKey(modifiedPlayer)) return false;
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.kick");
        getMembers().remove(modifiedPlayer);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configSection.getString("notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean invest(ModifiedPlayer modifiedPlayer, int amount) {
        if(Plugin.getInstance().economy.getBalance(modifiedPlayer.getPlayer()) < amount) return false;
        int maximumBalance = LevelType.getLevelMaximumBalance(getLevel());
        if((getBalance() + amount) > maximumBalance) return false;
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.invest");
        Plugin.getInstance().economy.withdrawPlayer(modifiedPlayer.getPlayer(), amount);
        setBalance(getBalance() + amount);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configSection.getString("notification_of_investment").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean withdraw(ModifiedPlayer modifiedPlayer, int amount) {
        if(getBalance() < amount) return false;
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.withdraw");

        Plugin.getInstance().economy.depositPlayer(modifiedPlayer.getPlayer(), amount);
        setBalance(getBalance() - amount);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configSection.getString("notification_of_withdrawal").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%amount%", String.valueOf(amount));
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean leave(ModifiedPlayer modifiedPlayer) {
        if(!getMembers().containsKey(modifiedPlayer)) return false;
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.leave");
        getMembers().remove(modifiedPlayer);

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configSection.getString("notification_of_exclusion").replace("%player%", modifiedPlayer.getPlayer().getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean rank(ModifiedPlayer modifiedPlayer, int id) {
        if(id == 1) return false;

        if(id == 2) getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), RankType.DEPUTY.getName());
        else if(id == 3) getMembers().replace(modifiedPlayer, getMembers().get(modifiedPlayer), RankType.MEMBER.getName());
        else return false;
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.rank");

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            String string = configSection.getString("notification_of_rank").replace("%player%", modifiedPlayer.getPlayer().getName()).replace("%rank%", (id == 2) ? RankType.DEPUTY.getName() : RankType.MEMBER.getName());
            chatUtil.sendMessage(entry.getKey().getPlayer(), string);
        }

        return true;
    }

    @Override
    public boolean disband(ModifiedPlayer modifiedPlayer, Clan clan) {
        if(!getMembers().get(modifiedPlayer).equals("Лидер")) return false;
        ConfigurationSection configSection = Plugin.getInstance().getConfig().getConfigurationSection("message.command.disband");

        for(Map.Entry<ModifiedPlayer, String> entry : getMembers().entrySet()) {
            entry.getKey().setClan(null);

            chatUtil.sendMessage(entry.getKey().getPlayer(), configSection.getString("notification_of_disband"));
        }

        CLANS.remove(((ClanImpl) clan).getName());
        return true;
    }

    @Override
    public void showMenu(ModifiedPlayer modifiedPlayer, int id) {
        Inventory menu;
        ItemStack redStainedGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        int[] indices = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        ItemStack spectralArrow = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta spectralArrowMeta = spectralArrow.getItemMeta();
        int maximumBalance;
        List<String> list;
        List<String> replacedList;
        int slot;
        ConfigurationSection configSection;

        switch(id) {
            case 1:
                menu = Bukkit.createInventory(null, 45, "Клан " + getName());

                for (int index : indices) {
                    menu.setItem(index, redStainedGlassPane);
                }

                configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_clan.items");

                maximumBalance = LevelType.getLevelMaximumBalance(getLevel());

                ItemStack knowledgeBook = new ItemStack(Material.KNOWLEDGE_BOOK);
                ItemMeta knowledgeBookMeta = knowledgeBook.getItemMeta();
                knowledgeBookMeta.setDisplayName(HexUtil.color(configSection.getString("information.title")));
                list = configSection.getStringList("information.lore");
                final int finalMaximumBalance = maximumBalance;
                replacedList = list.stream().map(string -> {
                    String replacedString = string
                            .replace("%name%", getName())
                            .replace("%members%", String.valueOf(getMembers().size()))
                            .replace("%level%", String.valueOf(getLevel()))
                            .replace("%xp%", String.valueOf(getXp()))
                            .replace("%balance%", String.valueOf(getBalance()))
                            .replace("%pvp%", (isPvp()) ? "Да" : "Нет")
                            .replace("%maximum_balance%", String.valueOf(finalMaximumBalance));

                    return HexUtil.color(replacedString);
                }).collect(Collectors.toList());

                knowledgeBookMeta.setLore(replacedList);

                knowledgeBook.setItemMeta(knowledgeBookMeta);

                ItemStack totemOfUndying = new ItemStack(Material.TOTEM_OF_UNDYING);
                ItemMeta totemOfUndyingItemMeta = totemOfUndying.getItemMeta();
                totemOfUndyingItemMeta.setDisplayName(HexUtil.color(configSection.getString("members_clan.title")));
                list = configSection.getStringList("members_clan.lore");
                final int finalMaximumBalance1 = maximumBalance;
                replacedList = list.stream().map(string -> {
                    String replacedString = string
                            .replace("%name%", getName())
                            .replace("%members%", String.valueOf(getMembers().size()))
                            .replace("%level%", String.valueOf(getLevel()))
                            .replace("%xp%", String.valueOf(getXp()))
                            .replace("%balance%", String.valueOf(getBalance()))
                            .replace("%pvp%", (isPvp()) ? "Да" : "Нет")
                            .replace("%maximum_balance%", String.valueOf(finalMaximumBalance1));

                    return HexUtil.color(replacedString);
                }).collect(Collectors.toList());
                totemOfUndyingItemMeta.setLore(replacedList);

                totemOfUndying.setItemMeta(totemOfUndyingItemMeta);

                ItemStack diamond = new ItemStack(Material.DIAMOND);
                ItemMeta diamondMeta = diamond.getItemMeta();

                diamondMeta.setDisplayName(HexUtil.color(configSection.getString("level_clan.title")));
                list = configSection.getStringList("level_clan.lore");
                final int finalMaximumBalance4 = maximumBalance;
                replacedList = list.stream().map(string -> {
                    String replacedString = string
                            .replace("%name%", getName())
                            .replace("%members%", String.valueOf(getMembers().size()))
                            .replace("%level%", String.valueOf(getLevel()))
                            .replace("%xp%", String.valueOf(getXp()))
                            .replace("%balance%", String.valueOf(getBalance()))
                            .replace("%pvp%", (isPvp()) ? "Да" : "Нет")
                            .replace("%maximum_balance%", String.valueOf(finalMaximumBalance4));

                    return HexUtil.color(replacedString);
                }).collect(Collectors.toList());
                diamondMeta.setLore(replacedList);

                diamond.setItemMeta(diamondMeta);
                spectralArrowMeta.setDisplayName(HexUtil.color(configSection.getString("exit.title")));
                spectralArrow.setItemMeta(spectralArrowMeta);

                menu.setItem(34, spectralArrow);
                menu.setItem(10, knowledgeBook);
                menu.setItem(11, totemOfUndying);
                menu.setItem(12, diamond);

                modifiedPlayer.getPlayer().openInventory(menu);
                break;
            case 2:
                menu = Bukkit.createInventory(null, 45, "Участники клана");

                ClanImpl clan = (ClanImpl) modifiedPlayer.getClan();

                for (int index : indices) {
                    menu.setItem(index, redStainedGlassPane);
                }

                configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_clan_members");

                spectralArrow = new ItemStack(Material.SPECTRAL_ARROW);
                spectralArrowMeta = spectralArrow.getItemMeta();
                spectralArrowMeta.setDisplayName(HexUtil.color(configSection.getString("in_menu.title")));
                spectralArrow.setItemMeta(spectralArrowMeta);
                menu.setItem(34, spectralArrow);

                slot = 10;
                for (Map.Entry<ModifiedPlayer, String> entry : clan.getMembers().entrySet()) {

                    if (menu.getItem(slot) == null) {
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                        if (Bukkit.getPlayer(entry.getKey().getPlayer().getName()) == null)
                            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(entry.getKey().getPlayer().getName()));
                        else skullMeta.setOwningPlayer(Bukkit.getPlayer(entry.getKey().getPlayer().getName()));
                        list = configSection.getStringList("player.lore");
                        replacedList = list.stream().map(string -> {
                            String replacedString = string
                                    .replace("%rank%", getMembers().get(modifiedPlayer));
                            return HexUtil.color(replacedString);
                        }).collect(Collectors.toList());
                        skullMeta.setLore(replacedList);
                        skullMeta.setDisplayName(HexUtil.color("&fИгрок " + entry.getKey().getPlayer().getName()));
                        skull.setItemMeta(skullMeta);
                        menu.setItem(slot, skull);
                        slot++;
                    } else {
                        slot++;
                    }

                }

                modifiedPlayer.getPlayer().openInventory(menu);
                break;
            case 3:
                menu = Bukkit.createInventory(null, 45, "Уровень клана");

                for (int index : indices) {
                    menu.setItem(index, redStainedGlassPane);
                }

                configSection = Plugin.getInstance().getConfig().getConfigurationSection("settings.menu.menu_level_clan.level");

                spectralArrowMeta.setDisplayName(HexUtil.color(configSection.getString("in_menu.title")));
                spectralArrow.setItemMeta(spectralArrowMeta);
                menu.setItem(34, spectralArrow);

                slot = 10;
                for (int i = 1; i <= LevelType.countLevel; i++) {
                    maximumBalance = LevelType.getLevelMaximumBalance(i);
                    if (getLevel() < i) {
                        ItemStack furnaceMinecart = new ItemStack(Material.FURNACE_MINECART);
                        ItemMeta furnaceMinecartMeta = furnaceMinecart.getItemMeta();
                        furnaceMinecartMeta.setDisplayName(HexUtil.color(configSection.getString("not_received.title").replace("%level%", String.valueOf(i))));
                        list = configSection.getStringList("not_received.lore");
                        final int finalMaximumBalance2 = maximumBalance;
                        replacedList = list.stream().map(string -> {
                            String replacedString = string
                                    .replace("%name%", getName())
                                    .replace("%members%", String.valueOf(getMembers().size()))
                                    .replace("%level%", String.valueOf(getLevel()))
                                    .replace("%xp%", String.valueOf(getXp()))
                                    .replace("%pvp%", (isPvp()) ? "Да" : "Нет").replace("%maximum_balance%", String.valueOf(finalMaximumBalance2));
                            return HexUtil.color(replacedString);
                        }).collect(Collectors.toList());
                        furnaceMinecartMeta.setLore(replacedList);
                        furnaceMinecart.setItemMeta(furnaceMinecartMeta);
                        menu.setItem(slot, furnaceMinecart);
                        slot++;
                        continue;
                    }

                    ItemStack chestMinecart = new ItemStack(Material.CHEST_MINECART);
                    ItemMeta chestMinecartMeta = chestMinecart.getItemMeta();
                    chestMinecartMeta.setDisplayName(HexUtil.color(configSection.getString("received.title").replace("%level%", String.valueOf(i))));
                    list = configSection.getStringList("received.lore");
                    final int finalMaximumBalance3 = maximumBalance;
                    replacedList = list.stream().map(string -> {
                        String replacedString = string
                                .replace("%name%", getName())
                                .replace("%members%", String.valueOf(getMembers().size()))
                                .replace("%level%", String.valueOf(getLevel()))
                                .replace("%xp%", String.valueOf(getXp()))
                                .replace("%pvp%", (isPvp()) ? "Да" : "Нет").replace("%maximum_balance%", String.valueOf(finalMaximumBalance3));
                        return HexUtil.color(replacedString);
                    }).collect(Collectors.toList());
                    chestMinecartMeta.setLore(replacedList);
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

        private final int id;

        MenuType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }

    public static Map<String, ClanImpl> getClans() {
        return CLANS;
    }
}
