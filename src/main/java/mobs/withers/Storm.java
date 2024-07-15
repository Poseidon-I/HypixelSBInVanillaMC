package mobs.withers;

import listeners.CustomMobs;
import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.Random;

import static listeners.CustomMobs.spawnLightning;

public class Storm implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Storm" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spawnLightning(e, 64);
		e.addScoreboardTag("Storm");
		e.getWorld().setThundering(true);
		e.getWorld().setWeatherDuration(1000000);
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "STORM, most explosive Wither in the universe, has come to smite you with his Lightning!");
		Bukkit.getLogger().info("Storm has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return (((Wither) damagee).getInvulnerabilityTicks() == 0 || type == DamageType.ABSOLUTE) && type != DamageType.IFRAME_ENVIRONMENTAL;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		Random random = new Random();
		if(random.nextBoolean()) {
			damagee.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
		}
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		CustomMobs.spawnLightning(skull, 32);
	}
}