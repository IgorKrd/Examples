package com.mhunter.game.armory;

public class Potion implements Item {

    public enum Type {

        HEALTH
    }

    private String title;
    private Type type;
    private int power;

    public Type getType() {
        return type;
    }

    @Override
    public Item.Type getItemType() {
        return Item.Type.POTION;
    }

    public int getPower() {
        return power;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public boolean isWearable() {
        return false;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isStackable() {
        return true;
    }

    public Potion(String title, Type type, int power) {
        this.title = title;
        this.type = type;
        this.power = power;

    }
}
