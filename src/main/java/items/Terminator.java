package items;

import listeners.NonEntityDamage;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Terminator implements CustomItem {
	private static final String COOLDOWN_TAG = "TerminatorCooldown";
	private static final int COOLDOWN = 4;

	public static ItemStack getItem(int powerLevel) {
		ItemStack term = new ItemStack(Material.BOW);

		ItemMeta data = term.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Terminator");
		data.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

		String loreDamage;
		switch(powerLevel) {
			case 1 -> loreDamage = "2.2";
			case 2 -> loreDamage = "2.4";
			case 3 -> loreDamage = "2.6";
			case 4 -> loreDamage = "2.8";
			case 5 -> loreDamage = "3";
			case 6 -> loreDamage = "3.2";
			case 7 -> loreDamage = "3.5";
			default -> loreDamage = "2";
		}

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/terminator");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + loreDamage);
		lore.add(ChatColor.GRAY + "Shot Cooldown: " + ChatColor.GREEN + "0.25s");
		lore.add("");
		lore.add(ChatColor.GOLD + "Shortbow: Instantly Shoots!");
		lore.add(ChatColor.GRAY + "Shoots " + ChatColor.AQUA + "3" + ChatColor.GRAY + " arrows at once.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Salvation " + ChatColor.GREEN + ChatColor.BOLD + "LEFT CLICK");
		lore.add(ChatColor.GRAY + "Shoot a beam, penetrating up to");
		lore.add(ChatColor.YELLOW + "5" + ChatColor.GRAY + " foes and dealing " + ChatColor.RED + "2");
		lore.add(ChatColor.GRAY + "damage to each enemy.");
		lore.add(ChatColor.GRAY + "Cooldown: " + ChatColor.GREEN + "1s");
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC BOW " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		term.setItemMeta(data);

		return term;
	}

	@Override
	public void onRightClick(Player p) {
		// you don't need arrows
		p.getInventory().remove(Material.ARROW);
		p.getInventory().remove(Material.TIPPED_ARROW);
		p.getInventory().remove(Material.SPECTRAL_ARROW);

		// setting the three arrows
		Vector v = p.getLocation().getDirection();
		Location lLeft = p.getLocation();
		lLeft.add(v);
		lLeft.setYaw(lLeft.getYaw() - 5);
		lLeft.setY(lLeft.getY() + 1.62);

		Location l = p.getLocation();
		l.add(v);
		l.setY(l.getY() + 1.62);

		Location lRight = p.getLocation();
		lRight.add(v);
		lRight.setYaw(lRight.getYaw() + 5);
		lRight.setY(lRight.getY() + 1.62);

		// calculate power and strength bonus
		double powerBonus;
		try {
			int power = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getEnchantmentLevel(Enchantment.POWER);
			powerBonus = power * 0.2;
			if(power == 7) {
				powerBonus += 0.1;
			}
		} catch(Exception exception) {
			powerBonus = 0;
		}

		double strengthBonus;
		try {
			strengthBonus = 0.5 * p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
		} catch(Exception exception) {
			strengthBonus = 0;
		}

		// shoot the three arrows
		double add = powerBonus + strengthBonus;
		Arrow left = p.getWorld().spawnArrow(l, lLeft.getDirection(), 3, 0.1F);
		Arrow middle = p.getWorld().spawnArrow(l, l.getDirection(), 3, 0.1F);
		Arrow right = p.getWorld().spawnArrow(l, lRight.getDirection(), 3, 0.1F);

		left.setDamage(2 + add);
		left.setPierceLevel(4);
		left.setShooter(p);
		left.addScoreboardTag("TerminatorArrow");

		middle.setDamage(2 + add);
		middle.setPierceLevel(4);
		middle.setShooter(p);
		middle.addScoreboardTag("TerminatorArrow");

		right.setDamage(2 + add);
		right.setPierceLevel(4);
		right.setShooter(p);
		right.addScoreboardTag("TerminatorArrow");

		p.playSound(p, Sound.ENTITY_ARROW_SHOOT, 1, 1);
		p.addScoreboardTag("TerminatorCooldown");
		Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("TerminatorCooldown"), 4L);
	}

	@Override
	public void onLeftClick(Player p) {
		if(!p.getScoreboardTags().contains("SalvationCooldown")) {double powerBonus;
			try {
				int power = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getEnchantmentLevel(Enchantment.POWER);
				powerBonus = power * 0.5;
			} catch(Exception exception) {
				powerBonus = 0;
			}

			double strengthBonus;
			try {
				strengthBonus = p.getPotionEffect(PotionEffectType.STRENGTH).getAmplifier();
			} catch(Exception exception) {
				strengthBonus = 0;
			}

			// shoot the three arrows
			double add = powerBonus + strengthBonus;
			NonEntityDamage.shootBeam(p, p, Color.RED, 64, 5, 4 + add);
			p.playSound(p.getLocation(), Sound.ENTITY_GUARDIAN_DEATH, 1.0F, 2.0F);
			p.addScoreboardTag("SalvationCooldown");
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> p.removeScoreboardTag("SalvationCooldown"), 39L);
		}
	}

	@Override
	public int manaCost() {
		return 0;
	}

	@Override
	public String cooldownTag() {
		return COOLDOWN_TAG;
	}

	@Override
	public int cooldown() {
		return COOLDOWN;
	}
}