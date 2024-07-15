package mobs;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AtonedHorror implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Atoned Horror" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		EntityEquipment equipment = e.getEquipment();
		equipment.setItemInMainHand(sword);
		equipment.setItem(EquipmentSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
		equipment.setItem(EquipmentSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
		equipment.setItem(EquipmentSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
		equipment.setItem(EquipmentSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));

		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(100.0);
		e.setHealth(100.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(8.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.5);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(Plugin.getNearestPlayer(e));
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("AtonedHorror");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Atoned Horror has risen from the depths!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Atoned Horror.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		((Ageable) e).setAdult();
		e.setPersistent(true);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		Random random = new Random();
		if(random.nextDouble() < 0.15) {
			TNTPrimed tnt = (TNTPrimed) damager.getWorld().spawnEntity(damager.getLocation(), EntityType.TNT);
			tnt.setFuseTicks(40);
			damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Atoned Horror becomes enraged and summons an extra TNT!");

			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
				List<Entity> entities = tnt.getNearbyEntities(5, 5, 5);
				for(Entity entity : entities) {
					if(entity instanceof LivingEntity entity1 && !entity.equals(damager)) {
						CustomDamage.customMobs(entity1, tnt, 20, DamageType.PLAYER_MAGIC);
					}
				}
				tnt.remove();
			}, 20L);
		}
		if(type == DamageType.PLAYER_MAGIC) {
			damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You cannot deal " + type + " damage to the Atoned Horror.");
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		TNTPrimed tnt = (TNTPrimed) damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.TNT);
		tnt.setFuseTicks(50);

		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> {
			List<Entity> entities = tnt.getNearbyEntities(5, 5, 5);
			for(Entity entity : entities) {
				if(entity instanceof LivingEntity entity1 && !entity.equals(damagee)) {
					CustomDamage.customMobs(entity1, tnt, 20, DamageType.PLAYER_MAGIC);
				}
			}
			tnt.remove();
		}, 30L);
		return true;
	}
}
