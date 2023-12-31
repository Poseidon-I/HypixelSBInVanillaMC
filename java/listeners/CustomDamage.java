package listeners;

import misc.Plugin;
import misc.SimilarData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class CustomDamage implements Listener {

	public static double calculateFinalDamage(LivingEntity entity, double originalDamage) {
		if(entity.hasPotionEffect(PotionEffectType.WEAKNESS)) {
			originalDamage++;
		}

		double armor = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ARMOR)).getValue();
		double toughness = Math.max(Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).getValue() - 8, 0); // only toughness values of 9 or more will give damage reduction

		double resistance = 0;
		try {
			resistance = Objects.requireNonNull(entity.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)).getAmplifier() + 1;
		} catch(Exception exception) {
			// continue
		}

		// get prot levels
		double prots = 0;
		EntityEquipment eq = entity.getEquipment();
		assert eq != null;
		try {
			prots += Objects.requireNonNull(eq.getHelmet()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
		} catch(Exception exception) {
			// continue
		}

		try {
			prots += Objects.requireNonNull(eq.getChestplate()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
		} catch(Exception exception) {
			// continue
		}

		try {
			prots += Objects.requireNonNull(eq.getLeggings()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
		} catch(Exception exception) {
			// continue
		}

		try {
			prots += Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
		} catch(Exception exception) {
			// continue
		}

		//						-4% per armor					-10% per toughenss above 8			-4% per protection level			-20% per resistance level
		return originalDamage * Math.max(0.2, 1 - armor * 0.04) * Math.max(0.2, 1 - toughness * 0.1) * Math.max(0.2, 1 - prots * 0.04) * Math.max(0.0, 1 - resistance * 0.2);
	}

	@SuppressWarnings("DuplicateExpressions")
	public static void dealDamage(LivingEntity entity, Entity damager, double finalDamage, boolean kb) {
		double absorption = entity.getAbsorptionAmount();
		if(finalDamage > absorption) {
			entity.setAbsorptionAmount(0.0);
			finalDamage -= absorption;
		} else {
			entity.setAbsorptionAmount(absorption - finalDamage);
			finalDamage = 0.0;
		}
		double newHealth = entity.getHealth() - finalDamage;
		if(newHealth < 0.0) {
			entity.setHealth(0.0);
		} else if(finalDamage > 0) {
			entity.setHealth(entity.getHealth() - finalDamage);
			entity.playHurtAnimation(0.0F);
			entity.getWorld().playSound(entity, Objects.requireNonNull(entity.getHurtSound()), 1.0F, 1.0F);
			entity.setNoDamageTicks(10);
			if(entity instanceof Mob && damager instanceof LivingEntity) {
				((Mob) entity).setTarget((LivingEntity) damager);
			}

			// apply knockback
			if(kb) {
				double antiKB = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).getValue();
				double rawYaw = damager.getLocation().getYaw();
				double yaw = Math.toRadians(rawYaw);
				double factor = 0.5 * Math.max(0.5, 1 - antiKB);
				if(damager instanceof Arrow) {
					factor *= 0.25;
				}
				Vector oldVelocity = entity.getVelocity();
				double x = oldVelocity.getX();
				double y = oldVelocity.getY();
				double z = oldVelocity.getZ();
				if(rawYaw <= -90) {
					x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
					y += factor;
					z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
				} else if(rawYaw >= 90) {
					x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
					y += factor;
					z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
				} else if(rawYaw < 0) {
					x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
					y += factor;
					z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
				} else if(rawYaw >= 0) {
					x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
					y += factor;
					z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
				}
				entity.setVelocity(new Vector(x, y, z));
			}

			// change nametag health
			SimilarData.changeName(entity);
		}
	}

	public static void dealWithCustomMobs(LivingEntity entity, Entity damager, double originalDamage, double finalDamage, boolean kb) {
		try {
			// melog noris
			if(entity instanceof IronGolem golem && Objects.requireNonNull(entity.getCustomName()).contains("meloG norI")) {
				if(damager instanceof LivingEntity entity1) {
					if(finalDamage > 5.0) {
						golem.setTarget(entity1);
						if(golem.getHealth() + (originalDamage - 5.0) > 200) {
							golem.setHealth(200);
							dealDamage(entity1, entity, calculateFinalDamage(entity, originalDamage / 2), false); // damager takes 50% of their original damage
							damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The meloG norI is at full health and has REFLECTED " + originalDamage / 2 + " + Damage back to you!");
						} else {
							golem.setHealth(golem.getHealth() + (originalDamage - 5.0));
							damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "You have done too much damage to the meloG norI!  It has HEALED ITSELF!");
						}
					} else {
						dealDamage(entity, damager, finalDamage, kb);
					}
				}

				// tarantula broodfathers
			} else if(entity instanceof Spider spider && Objects.requireNonNull(entity.getCustomName()).contains("Tarantula Broodfather")) {
				Random random = new Random();
				dealDamage(entity, damager, 2.0, kb);
				Location l = spider.getLocation();
				Location l2 = spider.getLocation();
				Vector added = new Vector(random.nextInt(17) - 8, 0, random.nextInt(17) - 8);
				l.add(added);
				l2.add(added);
				while(true) {
					if(l.getY() > 319 || l2.getY() < -63) {
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather could not find a spot to teleport to!");
						break;
					} else if(l.getBlock().isEmpty()) {
						spider.teleport(l);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather has scurried away!");
						break;
					} else if(l2.getBlock().isEmpty()) {
						spider.teleport(l2);
						damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The Tarantula Broodfather has scurried away!");
						break;
					}
					l.add(0, 1, 0);
					l2.add(0, -1, 0);
				}
				spider.getWorld().playSound(spider.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);

				// chickzillas
			} else if(entity instanceof Chicken && Objects.requireNonNull(entity.getCustomName()).contains("Chickzilla")) {
				String message = ChatColor.RED + String.valueOf(ChatColor.BOLD) + "Chickzilla has REFLECTED " + originalDamage / 2 + " + Damage back to you!";
				if(damager instanceof LivingEntity damager1) {
					damager.sendMessage(message);
					dealDamage(damager1, entity, calculateFinalDamage(damager1, originalDamage / 2), false); // damager takes 50% of their original damage
					dealDamage(entity, damager, finalDamage, kb); // damage the chicken
				} else if(damager instanceof Arrow arrow) {
					if(arrow.getShooter() instanceof Player p) {
						p.sendMessage(message);
						dealDamage(p, entity, calculateFinalDamage(p, originalDamage / 2), false); // damager takes 50% of their original damage
						dealDamage(entity, damager, finalDamage, kb); // damage the chicken
					}
				}

				// apply general damage
			} else {
				dealDamage(entity, damager, finalDamage, kb);
			}
		} catch(NullPointerException exception) {
			dealDamage(entity, damager, finalDamage, kb);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		// make armor less unintuitive
		if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			if(entity.getNoDamageTicks() == 0) {
				double originalDamage = e.getDamage();
				double finalDamage = calculateFinalDamage(entity, e.getDamage());

				// apply intelligence to players
				if(e.getDamager() instanceof Player p) {
					try {
						Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
						if(score.getScore() < 1000) {
							score.setScore(score.getScore() + 1);
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/1000", ChatColor.AQUA.asBungee()));
						} else {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Intelligence: " + score.getScore() + "/1000 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE", ChatColor.AQUA.asBungee()));
						}
					} catch(Exception exception) {
						Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
						Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
						return;
					}
				}

				Entity damager = e.getDamager();
				// apply custom damage to special mobs before going through with general damage
				dealWithCustomMobs(entity, damager, originalDamage, finalDamage, true);
			}
		}
	}
}