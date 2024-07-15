package mobs;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.Plugin;
import misc.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class meloGnorI implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "meloG norI" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(200.0);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(Plugin.getNearestPlayer(e));
		e.setHealth(200.0);
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("meloGnorI");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Antimatter has done strange things to this Iron Golem...");
		Bukkit.getLogger().info(p.getName() + " has summoned the meloG norI.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		e.setPersistent(true);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(type == DamageType.MELEE) {
			if(damager instanceof LivingEntity entity1) {
				if(originalDamage > 10.0) {
					if(damagee.getHealth() + (originalDamage - 10.0) > 200) {
						damagee.setHealth(200);
						CustomDamage.calculateFinalDamage(entity1, damagee, (originalDamage - 10) / 2, DamageType.MELEE); // damager takes 50% of their original damage, -10
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!\nIt is at full health and has REFLECTED " + (originalDamage - 10) / 2 + " Damage back to you!");
					} else {
						damagee.setHealth(damagee.getHealth() + (originalDamage - 10.0));
						PluginUtils.changeName(damagee);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!\nIt has HEALED ITSELF by " + (originalDamage - 10.0) + " HP!");
					}
					return false;
				} else {
					Random random = new Random();
					if(random.nextDouble() < 0.15) {
						CustomDamage.calculateFinalDamage(damagee, damager, 20, DamageType.MELEE);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The meloG norI becomes enraged and deals extra damage to you!");
					} else if(random.nextDouble() < 0.25) {
						damagee.teleport(damager);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The meloG norI's unstableness has caused it to teleport to you!");
					}
					return true;
				}
			}
			return false;
		} else {
			damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You cannot deal " + type + " damage to the meloG norI.");
			return false;
		}
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}
}
