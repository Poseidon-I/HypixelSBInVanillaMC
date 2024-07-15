package mobs;

import listeners.CustomDamage;
import listeners.DamageType;
import misc.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class VoidgloomSeraph implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Voidgloom Seraph" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(300.0);
		e.setHealth(300.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(15.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(25.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.6);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(Plugin.getNearestPlayer(e));
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("Voidgloom");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The remains of the Superior Dragon has drawn the attention of the Voidgloom Seraph!  Defeat it before it's too late!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Voidgloom Seraph.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		e.setPersistent(true);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		Random random = new Random();
		if(damager instanceof LivingEntity entity1) {
			if(random.nextDouble() < 0.15) {
				damager.teleport(damagee);
				CustomDamage.calculateFinalDamage(entity1, damagee, 10, DamageType.MELEE);
				damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Voidgloom Seraph's Dark Magic has caused you to teleport to it!  It also deals extra damage to you!");
			}
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}
}