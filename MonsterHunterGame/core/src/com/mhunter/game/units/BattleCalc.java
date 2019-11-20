package com.mhunter.game.units;


public class BattleCalc {

    public static int calculateDamage(GameCharacters attacker, GameCharacters target, int baseDamage) {
        int diffAttackToDefense = attacker.getStats().getAttack() - target.getStats().getDefense();
        int diffLevel = attacker.getStats().getLevel() - target.getStats().getLevel();
        int outDamage = (int) (baseDamage * (1 + diffAttackToDefense * 0.05f) * (1 + diffLevel * 0.1f));
        if (outDamage < 1) {
            outDamage = 1;
        }
        return outDamage;
    }

    public static int calculateExperience(GameCharacters attacker, GameCharacters target) {
        int experience = target.getStats().getHealthMax() * 10;
        int diffLevel = attacker.getStats().getLevel() - target.getStats().getLevel();
        experience *= (1.0f + diffLevel * 0.2f);
        if (experience < 0) {
            experience = 0;
        }
        return experience;
    }
}
