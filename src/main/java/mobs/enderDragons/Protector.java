package mobs.enderDragons;

import listeners.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.Objects;

import static listeners.CustomDamage.calculateFinalDamage;

public class Protector implements CustomDragon {
	@Override
	public void whenShootingFireball(DragonFireball fireball) {

	}

	@Override
	public String onSpawn(Player p, Mob e) {
		String name = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Protector Dragon" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(13.0);
		e.addScoreboardTag("ProtectorDragon");
		Bukkit.broadcastMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The PROTECTOR DRAGON has arrived to protect the End from Nons!");
		Bukkit.getLogger().info("The Protector Dragon has been summoned!");
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
