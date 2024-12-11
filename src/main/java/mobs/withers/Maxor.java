package mobs.withers;

import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

import static listeners.CustomDamage.teleport;

public class Maxor implements CustomWither {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Maxor" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(2.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_FLYING_SPEED)).setBaseValue(2.0);
		e.addScoreboardTag("Maxor");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "MAXOR, the fastest Wither in the universe, has come to destroy you 0.01 seconds faster than all other Withers!");
		Bukkit.getLogger().info("Maxor has been summoned!");
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(((Wither) damagee).getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
			return false;
		}
		Random random = new Random();
		if(!type.equals(DamageType.RANGED) && random.nextDouble() < 0.1 || type.equals(DamageType.RANGED) && random.nextDouble() < 0.05) {
			teleport(damagee, damager, 8);
		}
		return true;

	}

	@Override
	public boolean whenDamaging(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}

	@Override
	public void whenShootingSkull(WitherSkull skull) {
		Vector zoooooooooooom = skull.getVelocity();
		zoooooooooooom = zoooooooooooom.add(zoooooooooooom).add(zoooooooooooom);
		skull.setVelocity(zoooooooooooom);
	}
}
