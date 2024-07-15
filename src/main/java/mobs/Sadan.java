package mobs;

import listeners.CustomDamage;
import listeners.DamageType;
import listeners.NonEntityDamage;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class Sadan implements CustomMob {
	@Override
	public String onSpawn(Player p, Mob e) {
		String newName = ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "﴾ " + ChatColor.RED + ChatColor.BOLD + "Sadan" + ChatColor.GOLD + ChatColor.BOLD + " ﴿";
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);

		EntityEquipment equipment = e.getEquipment();
		equipment.setItemInMainHand(sword);
		equipment.setItem(EquipmentSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
		equipment.setItem(EquipmentSlot.CHEST, new ItemStack(Material.DIAMOND_CHESTPLATE));
		equipment.setItem(EquipmentSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
		equipment.setItem(EquipmentSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));

		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(600.0);
		e.setHealth(600.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ARMOR)).setBaseValue(-7.5);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(0.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(20.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1.0);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.125);
		Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(16.0);
		e.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255));
		e.setTarget(Plugin.getNearestPlayer(e));
		e.setCustomNameVisible(true);
		e.addScoreboardTag("SkyblockBoss");
		e.addScoreboardTag("Sadan");
		p.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan has arrived from the bowels of The Catacombs to deestroy you!");
		Bukkit.getLogger().info(p.getName() + " has summoned Sadan.");
		p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
		((Ageable) e).setAdult();
		e.setPersistent(true);

		Objects.requireNonNull(Plugin.getInstance().getServer().getBossBar(new NamespacedKey(Plugin.getInstance(), "sadan"))).addPlayer(p);
		Objects.requireNonNull(Plugin.getInstance().getServer().getBossBar(new NamespacedKey(Plugin.getInstance(), "sadan"))).setProgress(1.0);
		Objects.requireNonNull(Plugin.getInstance().getServer().getBossBar(new NamespacedKey(Plugin.getInstance(), "sadan"))).setTitle(newName + " " + ChatColor.RED + "❤ " + ChatColor.YELLOW + 600 + "/" + 600);
		return newName;
	}

	@Override
	public boolean whenDamaged(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(!(damager instanceof FallingBlock)) {
			Random random = new Random();
			if(damager instanceof LivingEntity entity && random.nextDouble() < 0.5) {
				switch(random.nextInt(4)) {
					case 0 -> {
						damager.teleport(damager.getLocation().subtract(0, 3, 0));
						CustomDamage.calculateFinalDamage(entity, damagee, 10, DamageType.MAGIC);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan has stomped you into the ground!");
						damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 0.5F);
					}
					case 1 -> {
						NonEntityDamage.shootBeam(damagee, damager, Color.RED, 32, 1, 20);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan has shot you with Laser Eyes!");
						damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 2.0F);
					}
					case 2 -> {
						damagee.swingMainHand();
						CustomDamage.calculateFinalDamage(entity, damagee, 20, DamageType.MELEE);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan attacks you violently with his Diamond Sword!");
						damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
					}
					case 3 -> {
						Block b = damager.getLocation().add(0, 20, 0).getBlock();
						if(b.getType().equals(Material.AIR)) {
							b.setType(Material.DAMAGED_ANVIL);
						}
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Sadan rains boulders on top of your head!!");
						damager.getWorld().playSound(damager.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean whenDamaging(LivingEntity damagee) {
		return true;
	}
}
