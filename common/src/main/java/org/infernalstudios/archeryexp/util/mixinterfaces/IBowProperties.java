package org.infernalstudios.archeryexp.util.mixinterfaces;

import org.infernalstudios.archeryexp.util.json.data.BowParticleData;
import org.infernalstudios.archeryexp.util.json.data.BowEffectData;

import java.util.List;

public interface IBowProperties {
    boolean archeryexp$isSpecial();
    void archeryexp$setSpecial(boolean hasProperties);


    int archeryexp$getBowCooldown();
    void archeryexp$setBowCooldown(int cooldown);


    int archeryexp$getChargeTime();
    void archeryexp$setChargeTime(int chargeTime);


    float archeryexp$getRange();
    void archeryexp$setRange(float range);


    float archeryexp$getBaseDamage();
    void archeryexp$setBaseDamage(float baseDamage);


    float archeryexp$getBreakResist();
    void archeryexp$setBreakResist(float breakResist);


    float archeryexp$getBreakChance();
    void archeryexp$setBreakChance(float breakChance);


    float archeryexp$getWalkSpeed();
    void archeryexp$setWalkSpeed(float walkSpeed);


    float archeryexp$getRecoil();
    void archeryexp$setRecoil(float recoil);


    List<BowEffectData> archeryexp$getEffects();
    void archeryexp$setEffects(List<BowEffectData> effects);
    
    
    List<BowParticleData> archeryexp$getParticles();
    void archeryexp$setParticles(List<BowParticleData> particles);


    float archeryexp$getOffsetX();
    void archeryexp$setOffsetX(float range);


    float archeryexp$getOffsetY();
    void archeryexp$setOffsetY(float range);


    boolean archeryexp$hasDesc();
    void archeryexp$setHasDesc(boolean hasDescText);
}
