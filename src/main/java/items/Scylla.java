package items;

import listeners.CustomItems;
import listeners.DamageType;
import misc.Plugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static listeners.CustomDamage.customMobs;

public class Scylla implements CustomItem {
	private static final int MANA_COST = 12;

	public static ItemStack getItem(int sharpnessLevel) {
		ItemStack scylla = new ItemStack(Material.NETHERITE_SWORD);

		ItemMeta data = scylla.getItemMeta();
		data.setUnbreakable(true);
		data.setDisplayName(ChatColor.LIGHT_PURPLE + "Hyperion");
		AttributeModifier attackSpeed = new AttributeModifier(UUID.fromString("d818fcd7-a2f8-434a-bfc3-6a352185714b"), "scyllaModifier", 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		AttributeModifier attackDamage = new AttributeModifier(UUID.fromString("b1bda7a7-b3eb-43b3-9e91-2473e9004f79"), "scyllaModifierDmg", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
		data.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
		data.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);


		String loreDamage;
		switch(sharpnessLevel) {
			case 1 -> loreDamage = "9";
			case 2 -> loreDamage = "9.5";
			case 3 -> loreDamage = "10";
			case 4 -> loreDamage = "10.5";
			case 5 -> loreDamage = "11";
			case 6 -> loreDamage = "11.5";
			case 7 -> loreDamage = "12";
			default -> loreDamage = "8";
		}

		List<String> lore = new ArrayList<>();
		lore.add("skyblock/combat/scylla");
		lore.add("");
		lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + loreDamage);
		lore.add("");
		lore.add(ChatColor.GOLD + "Ability: Wither Impact " + ChatColor.GREEN + ChatColor.BOLD + "RIGHT CLICK");
		lore.add(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + "10 blocks" + ChatColor.GRAY + " ahead of");
		lore.add(ChatColor.GRAY + "you.  Then implode, dealing");
		lore.add(ChatColor.RED + "51%" + ChatColor.GRAY + " of your Melee Damage to");
		lore.add(ChatColor.GRAY + "nearby enemies.  Also applies");
		lore.add(ChatColor.GRAY + "the Wither Shield Scroll Ability,");
		lore.add(ChatColor.GRAY + "reducing damage taken and");
		lore.add(ChatColor.GRAY + "granting an absorption shield");
		lore.add(ChatColor.GRAY + "for " + ChatColor.YELLOW + "5 seconds.");
		lore.add(ChatColor.DARK_GRAY + "Intelligence Cost: " + ChatColor.DARK_AQUA + MANA_COST);
		lore.add("");
		lore.add(ChatColor.LIGHT_PURPLE + String.valueOf(ChatColor.BOLD) + ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " MYTHIC SWORD " + ChatColor.MAGIC + "a");

		data.setLore(lore);
		scylla.setItemMeta(data);

		return scylla;
	}

	@Override
	public void onRightClick(Player p) {
		Location originalLocation = p.getLocation().clone();
		Location l = p.getLocation().clone();
		l.add(0, 1.62, 0);
		Vector v = l.getDirection();
		v.setX(v.getX() / 10);
		v.setY(v.getY() / 10);
		v.setZ(v.getZ() / 10);
		for(int i = 0; i < 100; i++) {
			l.add(v);
			if(l.getBlock().getType().isSolid()) {
				l = l.subtract(v).getBlock().getLocation();
				if(originalLocation.getPitch() > 0) {
					l.add(0, 1.62, 0);
				}
				l.setYaw(originalLocation.getYaw());
				l.setPitch(originalLocation.getPitch());
				l.add(0.5, 0, 0.5);
				break;
			}
		}
		l.subtract(0, 1.62, 0);
		if(!l.getBlock().isEmpty()) {
			l.add(0, 1, 0);
		}
		if(!l.getBlock().isEmpty()) {
			l.add(0, 1, 0);
		}
		p.setFallDistance(0);
		p.teleport(l);
		p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

		// implosion
		p.getWorld().spawnParticle(Particle.EXPLOSION, l, 20);
		List<Entity> entities = p.getNearbyEntities(10, 10, 10);
		List<EntityType> doNotKill = CustomItems.createList();
		double targetDamage = Objects.requireNonNull(p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getValue();
		int damaged = 0;
		double damage = 0;
		try {
			int sharpness = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getEnchantmentLevel(Enchantment.SHARPNESS);
			targetDamage += sharpness * 0.5 + 0.5;
		} catch(Exception exception) {
			// nothing
		}
		targetDamage = Math.ceil(targetDamage * 0.51);
		for(Entity entity : entities) {
			if(!doNotKill.contains(entity.getType()) && !entity.equals(p) && entity instanceof LivingEntity entity1 && entity1.getHealth() > 0) {
				if(entity instanceof Player target && (target.getGameMode().equals(GameMode.CREATIVE) || target.getGameMode().equals(GameMode.SPECTATOR))) {
					continue;
				}
				customMobs(entity1, p, targetDamage, DamageType.PLAYER_MAGIC);
				damaged += 1;
				damage += targetDamage;
			}
		}
		if(damaged > 0) {
			p.sendMessage(ChatColor.RED + "Your Implosion hit " + damaged + " enemies for " + ((int) damage) + " damage.");
		}
		p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

		// wither shield
		List<PotionEffect> temp = p.getActivePotionEffects().stream().toList();
		List<PotionEffectType> effects = new ArrayList<>();
		int absorptionLevel = -1;
		for(PotionEffect effect : temp) {
			if(effect.getType().equals(PotionEffectType.ABSORPTION)) {
				absorptionLevel = effect.getAmplifier();
			}
			effects.add(effect.getType());
		}
		if(absorptionLevel != 2) { // absorption shield
			p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 101, 2));
			p.playSound(p, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0F, 0.65F);
			Location finalL = l;
			Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> { // convert to healing after 5 seconds
				p.setHealth(Math.min(p.getHealth() + (p.getAbsorptionAmount() / 2), 20.0));
				p.playSound(finalL, Sound.ENTITY_PLAYER_LEVELUP, 2.0F, 2.0F);
			}, 101L);
		}
		if(!effects.contains(PotionEffectType.RESISTANCE)) { // reduced damage
			p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 101, 0));
		}
		if(!p.getGameMode().equals(GameMode.CREATIVE)) {
			Score score = CustomItems.currentScore();
			score.setScore(score.getScore() - manaCost());
		}
	}

	@Override
	public void onLeftClick(Player p) {
	}
	public int manaCost() {
		return MANA_COST;
	}

	@Override
	public String cooldownTag() {
		return "";
	}

	@Override
	public int cooldown() {
		return 0;
	}
}
