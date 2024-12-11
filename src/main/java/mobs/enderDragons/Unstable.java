package mobs.enderDragons;

import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;

import java.util.Random;

import static listeners.CustomDamage.calculateFinalDamage;
import static listeners.CustomMobs.spawnLightning;

public class Unstable implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {
		Random random = new Random();
		if(random.nextBoolean()) {
			spawnLightning(fireball, 64);
		}
	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Unstable Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		spawnLightning(e, 128);
		e.addScoreboardTag("UnstableDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The UNSTABLE DRAGON has arrived to cause chaos!");
		Bukkit.getLogger().info("The Unstable Dragon has been summoned!");
		return name;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(type == DamageType.RANGED) {
			calculateFinalDamage(damagee, damager, originalDamage / 3, DamageType.RANGED);
			return false;
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}
}
