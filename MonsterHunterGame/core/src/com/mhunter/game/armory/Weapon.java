package com.mhunter.game.armory;

import com.badlogic.gdx.math.MathUtils;

public class Weapon implements Item {

    public enum Type {
        MELEE, RANGED
    }

    public Type getType() {
        return type;
    }

    private  Type type;
    private String title;
    private float attackPeriod;
    private float attackRange;
    private int minDamage;
    private int maxDamage;

    @Override
    public Item.Type getItemType() {
        return Item.Type.WEAPON;
    }

    public float getAttackRange() {
        return attackRange;
    }

    @Override
    public String getTitle() {
        return title + "*" + minDamage + "-" + maxDamage + "*" ;
    }

    @Override
    public boolean isUsable() {
        return false;
    }

    @Override
    public boolean isWearable() {
        return true;
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    public float getAttackPeriod() {
        return attackPeriod;
    }

    public int getDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    public Weapon(String title, Type type, float attackPeriod, float attackRange, int minDamage, int maxDamage) {
        this.title = title;
        this.type = type;
        this.attackPeriod = attackPeriod;
        this.attackRange = attackRange;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }


}
