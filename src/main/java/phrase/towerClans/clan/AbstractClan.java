package phrase.towerClans.clan;

import java.util.*;

public abstract class AbstractClan implements Clan {

    private String name;
    private Map<ModifiedPlayer, String> members;
    private int level;
    private int xp;
    private int balance;
    private int kills;
    private int deaths;
    private boolean pvp;

    public AbstractClan() {}

    public AbstractClan(String name) {
        this.name = name;

        members = new HashMap<>();
        level = LevelType.ONE.getId();
        xp = LevelType.ONE.getXp();
        balance = 0;
        kills = 0;
        deaths = 0;
        pvp = false;
    }

    public abstract void showMenu(ModifiedPlayer modifiedPlayer, int id);

    public enum RankType {
        LEADER("Лидер"),
        DEPUTY("Заместитель"),
        MEMBER("Участник");

        private final String name;

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

        private final int maximumMembers;
        private final int maximumBalance;
        private final int xp;
        private final int id;
        public static final int countLevel = 5;

        LevelType(int maximumMembers, int maximumBalance, int xp,int id) {
            this.maximumMembers = maximumMembers;
            this.maximumBalance = maximumBalance;
            this.xp = xp;
            this.id = id;
        }

        public static int getXpLevel(int level) {
            LevelType levelType = null;

            level += ++level;

            levelType = switch (level) {
                case 1 -> LevelType.ONE;
                case 2 -> LevelType.TWO;
                case 3 -> LevelType.THREE;
                case 4 -> LevelType.FOUR;
                case 5 -> LevelType.FIVE;
                default -> levelType;
            };

            return levelType.getXp();
        }

        public static int getLevelMaximumBalance(int level) {
            return switch (level) {
                case 1 -> LevelType.ONE.getMaximumBalance();
                case 2 -> LevelType.TWO.getMaximumBalance();
                case 3 -> LevelType.THREE.getMaximumBalance();
                case 4 -> LevelType.FOUR.getMaximumBalance();
                case 5 -> LevelType.FIVE.getMaximumBalance();
                default -> 0;
            };
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

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}
