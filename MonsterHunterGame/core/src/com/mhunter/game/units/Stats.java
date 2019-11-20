package com.mhunter.game.units;

public class Stats {

    private int level;

    private int attack;
    private int defense;
    private int healthMax;

    private int attackBase;
    private int defenseBase;
    private int healthMaxBase;


    private int attackPerLevel;
    private int defensePerLevel;
    private int healthMaxPerLevel;

    private float speed;
    private int health;

    private int experience;
    private int[] experienceTo = {1_000, 2_000, 4_000, 8_000, 16_000, 32_000, 80_000, 150_000, 200_000, 400_000, 600_000, 1_200_000, 1_400_000, 2_000_000, 3_200_000};

    public float getSpeed() {
        return speed;
    }

    public int getHealth() {
        return health;
    }

    public int getHealthMax() {
        return healthMax;
    }

    public int getLevel() {
        return level;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public Stats() {
    }

    public Stats(int level, int attackBase, int defenseBase, int healthMaxBase, int attackPerLevel, int defensePerLevel, int healthMaxPerLevel, float speed) {
        this.level = level;
        this.attackBase = attackBase;
        this.defenseBase = defenseBase;
        this.healthMaxBase = healthMaxBase;
        this.attackPerLevel = attackPerLevel;
        this.defensePerLevel = defensePerLevel;
        this.healthMaxPerLevel = healthMaxPerLevel;
        this.speed = speed;
        this.calculate();
        this.fillHealth();
    }

    public void set(int level, Stats stats) {
        this.level = level;
        this.attackBase = stats.attackBase;
        this.defenseBase = stats.defenseBase;
        this.healthMaxBase = stats.healthMaxBase;
        this.attackPerLevel = stats.attackPerLevel;
        this.defensePerLevel = stats.defensePerLevel;
        this.healthMaxPerLevel = stats.healthMaxPerLevel;
        this.speed = stats.speed;
        this.calculate();
        this.fillHealth();
    }

    public void decreaseHealth(int amount) {
        health -= amount;
    }

    public void fillHealth() {
        health = healthMax;
    }

    public int restoreHealth(int amount) {
        int health0 = health;
        health += amount;
        if (health > healthMax) {
            health = healthMax;
        }
        return health - health0;
    }

    public void addExperience(int amount) {
        experience += amount;
        if (experience >= experienceTo[level - 1]) {
            experience = 0;
            level++;
            calculate();
            fillHealth();
        }


    }


    public void calculate() {

        attack = attackBase + level * attackPerLevel;
        defense = attackBase + level * defensePerLevel;
        healthMax = healthMaxBase + level * healthMaxPerLevel;


    }

}
