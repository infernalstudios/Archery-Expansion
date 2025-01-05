package org.infernalstudios.archeryexp.util;

import java.util.List;

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

    float getRecoil();
    void setRecoil(float recoil);

    List<PotionData> getEffects();
    void setEffects(List<PotionData> effects);

    List<ParticleData> getParticles();
    void setParticles(List<ParticleData> particles);

    boolean hasSpecialProperties();
    void setSpecialProperties(boolean hasProperties);
}
