package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.Utils;
import co.aikar.timings.Timings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityZombieVillager extends EntityWalkingMob {

    public static final int NETWORK_ID = 44;

    public EntityZombieVillager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.72f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public double getSpeed() {
        return 1.1;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(20);
        this.setDamage(new int[] { 0, 3, 4, 6 });
    }

    @Override
    public void attackEntity(Entity player) {
        if (this.attackDelay > 10 && this.distanceSquared(player) < 1) {
            this.attackDelay = 0;
            HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.DamageModifier.BASE, (float) this.getDamage());

            if (player instanceof Player) {
                @SuppressWarnings("serial")
                HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>() {
                    {
                        put(Item.LEATHER_CAP, 1f);
                        put(Item.LEATHER_TUNIC, 3f);
                        put(Item.LEATHER_PANTS, 2f);
                        put(Item.LEATHER_BOOTS, 1f);
                        put(Item.CHAIN_HELMET, 1f);
                        put(Item.CHAIN_CHESTPLATE, 5f);
                        put(Item.CHAIN_LEGGINGS, 4f);
                        put(Item.CHAIN_BOOTS, 1f);
                        put(Item.GOLD_HELMET, 1f);
                        put(Item.GOLD_CHESTPLATE, 5f);
                        put(Item.GOLD_LEGGINGS, 3f);
                        put(Item.GOLD_BOOTS, 1f);
                        put(Item.IRON_HELMET, 2f);
                        put(Item.IRON_CHESTPLATE, 6f);
                        put(Item.IRON_LEGGINGS, 5f);
                        put(Item.IRON_BOOTS, 2f);
                        put(Item.DIAMOND_HELMET, 3f);
                        put(Item.DIAMOND_CHESTPLATE, 8f);
                        put(Item.DIAMOND_LEGGINGS, 6f);
                        put(Item.DIAMOND_BOOTS, 3f);
                    }
                };

                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }

                damage.put(EntityDamageEvent.DamageModifier.ARMOR,
                        (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.ARM_SWING;
            Server.broadcastPacket(this.getViewers().values(), pk);
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate;
        Timings.entityBaseTickTimer.startTiming();

        hasUpdate = super.entityBaseTick(tickDiff);

        if (level.shouldMobBurn(this)) {
            this.setOnFire(100);
        }

        Timings.entityBaseTickTimer.stopTiming();
        return hasUpdate;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        if (this.hasCustomName()) {
            drops.add(Item.get(Item.NAME_TAG, 0, 1));
        }

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent && !this.isBaby()) {
            for (int i = 0; i < Utils.rand(0, 2); i++) {
                drops.add(Item.get(Item.ROTTEN_FLESH, 0, 1));
            }
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : 5;
    }

    @Override
    public String getName() {
        return "Zombie Villager";
    }
}
