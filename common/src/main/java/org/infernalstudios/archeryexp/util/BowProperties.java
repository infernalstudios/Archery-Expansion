package org.infernalstudios.archeryexp.util;

public interface BowProperties {
    int getBowCooldown();
    void setBowCooldown(int cooldown);

    int getChargeTime();
    void setChargeTime(int chargeTime);

    float getRange();
    void setRange(float range);

    float getBaseDamage();
    void setBaseDamage(float baseDamage);

    float getBreakingResistance();
    void setBreakingResistance(float breakingResistance);

    float getMovementSpeedMultiplier();
    void setMovementSpeedMultiplier(float movementSpeedMultiplier);

    int getQuickDrawLvl();
    void setQuickDrawLvl(int quickDrawLvl);

    void setSpecialProperties(boolean hasProperties);
}
