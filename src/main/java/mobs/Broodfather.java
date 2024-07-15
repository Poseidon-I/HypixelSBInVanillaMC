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

public class Broodfather implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Tarantula Broodfather" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		e.setHealth(16.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.75);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(Plugin.getNearestPlayer(e));
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("Broodfather");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Spider Relic draws the attention of the Tarantula Broodfather!");
		Bukkit.getLogger().info(p.getName() + " has summoned the Tarantula Broodfather.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		e.setPersistent(true);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		CustomDamage.teleport(damagee, damager, 12);
		CustomDamage.calculateFinalDamage(damagee, damager, 1, DamageType.ABSOLUTE);
		return false;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}
}
