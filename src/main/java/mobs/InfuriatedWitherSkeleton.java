package mobs;

import listeners.DamageType;
import misc.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class InfuriatedWitherSkeleton implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		e = (WitherSkeleton) e.getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
		e.setCustomName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Highly Infuriated Wither Skeleton" + ChatColor.GOLD + ChatColor.BOLD + " ﴿ " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 50 + "/" + 50);
		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		Objects.requireNonNull(e.getEquipment()).setItemInMainHand(sword);
		e.getEquipment().setItemInMainHandDropChance(0.0F);
		e.getEquipment().setItemInOffHand(sword);
		e.getEquipment().setItemInOffHandDropChance(0.0F);

		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(50.0);
		e.setHealth(50.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(12.5);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.5);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(p);
		e.teleport(p);
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("InfuriatedSkeleton");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "From the ashes of the Wither Skeleton rises its reincarnation: a HIGHLY INFURIATED Wither Skeleton");
		Bukkit.getLogger().info(p.getName() + " has found a Highly Infuriated Wither Skeleton!.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		e.setPersistent(true);
		return "";
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}
}
