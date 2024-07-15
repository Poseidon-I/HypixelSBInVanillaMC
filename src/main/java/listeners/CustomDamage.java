package listeners;

import misc.Plugin;
import misc.PluginUtils;
import mobs.CustomMob;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class CustomDamage implements Listener {
	private static EntityDamageEvent e;

	public static void customMobs(LivingEntity damagee, Entity damager, double originalDamage, DamageType type) {
		if(damager instanceof Projectile projectile) {
			if(projectile instanceof SpectralArrow && (damagee instanceof Player p && !p.isBlocking() || !(damagee instanceof Player))) {
				damagee.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
			}
			if(projectile.getShooter() instanceof LivingEntity temp) {
				damager = temp;
			}
		}

		// apply custom damage to special mobs before going through with general damage
		Random random = new Random();
		boolean doContinue = true;
		try {
			CustomMob damageeMob = CustomMob.getMob(damagee);
			CustomMob damagerMob = CustomMob.getMob(damager);

			// this section controls when bosses are damaged
			if(damageeMob != null) {
				doContinue = damageeMob.whenDamaged(damagee, damager, originalDamage, type);
			}

			// this section controls when bosses deal damage
			if(damagerMob != null) {
				doContinue = damagerMob.whenDamaging(damagee);
			}

			if(damager instanceof Wither wither) {
				if(wither.getCustomName().contains("Storm") && random.nextBoolean()) {
					wither.getWorld().spawnEntity(damagee.getLocation(), EntityType.LIGHTNING_BOLT);
				} else if(wither.getCustomName().contains("Necron")) {
					calculateFinalDamage(damagee, damager, originalDamage + 4, DamageType.RANGED);
					return;
				}
			} else if(damager instanceof EnderDragon dragon) {
				if(dragon.getCustomName().contains("Strong Dragon")) {
					calculateFinalDamage(damagee, damager, originalDamage + 6, DamageType.RANGED);
					return;
				} else if(dragon.getCustomName().contains("Superior Dragon") && random.nextBoolean()) {
					calculateFinalDamage(damagee, damager, originalDamage + 3, DamageType.RANGED);
					return;
				}
			} else if(damagee instanceof Wither wither) {
				if(wither.getInvulnerabilityTicks() != 0 && type != DamageType.ABSOLUTE || type == DamageType.IFRAME_ENVIRONMENTAL) {
					return;
				}
				if(Objects.requireNonNull(wither.getCustomName()).contains("Maxor") && random.nextDouble() < 0.1) {
					teleport(wither, damagee, 8);
				}
			} else if(damagee instanceof EnderDragon dragon) {
				if(Objects.requireNonNull(dragon.getCustomName()).contains("Young Dragon") && random.nextDouble() < 0.1) {
					teleport(dragon, damagee, 32);
				}
				if(type == DamageType.RANGED) {
					calculateFinalDamage(damagee, damager, originalDamage / 2, DamageType.RANGED);
					return;
				}
			}
			if(damagee instanceof Player p && !p.isBlocking() || !(damagee instanceof Player)) {
				switch(damager) {
					case Wither ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
					case CaveSpider ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0));
					case WitherSkeleton ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
					case Husk ignored -> damagee.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0));
					case Shulker ignored ->
							damagee.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 0));
					case null, default -> {
					}
				}
			}
		} catch(NullPointerException exception) {
			// continue
		}
		if(doContinue) {
			calculateFinalDamage(damagee, damager, originalDamage, type);
		}
	}

	public static void calculateFinalDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type) {
		if(type != DamageType.ABSOLUTE) {
			// ice spray logic
			if(damagee.getScoreboardTags().contains("IceSprayed")) {
				finalDamage++;
			}

			if(damager instanceof LivingEntity entity1) {
				if(entity1.getScoreboardTags().contains("IceSprayed")) {
					finalDamage *= 0.8;
				}
			}

			// shield logic (for weirdos)
			if(damagee instanceof Player p && p.isBlocking() && (type == DamageType.MELEE || type == DamageType.RANGED || type == DamageType.PLAYER_MAGIC)) {
				finalDamage *= 0.5;
			}

			if(type == DamageType.MELEE || type == DamageType.RANGED || type == DamageType.PLAYER_MAGIC || type == DamageType.ENVIRONMENTAL || type == DamageType.IFRAME_ENVIRONMENTAL) {
				double armor = Objects.requireNonNull(damagee.getAttribute(Attribute.GENERIC_ARMOR)).getValue();
				finalDamage *= Math.max(0.25, 1 - armor * 0.0375);
			}

			double toughness = Math.max(Objects.requireNonNull(damagee.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).getValue() - 8, 0); // only toughness values of 9 or more will give damage reduction
			finalDamage *= Math.max(0.2, 1 - toughness * 0.1);

			double resistance = 0;
			try {
				resistance = Objects.requireNonNull(damagee.getPotionEffect(PotionEffectType.RESISTANCE)).getAmplifier() + 1;
			} catch(Exception exception) {
				// continue
			}
			finalDamage *= Math.max(0.0, 1 - resistance * 0.2);

			// get prot levels
			double prots = 0;
			EntityEquipment eq = damagee.getEquipment();
			assert eq != null;
			try {
				prots += Objects.requireNonNull(eq.getHelmet()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getChestplate()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getLeggings()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}

			try {
				prots += Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.PROTECTION);
			} catch(Exception exception) {
				// continue
			}
			finalDamage *= Math.max(0.6, 1 - prots * 0.03);

			if(type == DamageType.FALL) {
				try {
					double featherFalling = Objects.requireNonNull(eq.getBoots()).getEnchantmentLevel(Enchantment.FEATHER_FALLING);
					finalDamage *= Math.max(0.5, 1 - featherFalling * 0.1);
				} catch(Exception exception) {
					// continue
				}
			}
		}
		dealDamage(damagee, damager, finalDamage, type);
	}

	@SuppressWarnings("DuplicateExpressions")
	public static void dealDamage(LivingEntity damagee, Entity damager, double finalDamage, DamageType type) {
		if(finalDamage > 0) {
			damagee.playHurtAnimation(0.0F);
			damagee.getWorld().playSound(damagee, Objects.requireNonNull(damagee.getHurtSound()), 1.0F, 1.0F);

			double absorption = damagee.getAbsorptionAmount();
			double oldHealth = damagee.getHealth();
			boolean doesDie = finalDamage >= oldHealth + absorption;

			if(doesDie) {
				if(damagee instanceof EnderDragon) {
					damagee.teleport(new Location(damagee.getWorld(), 0.5, 70.0, 0.5));
				}
				if(type == DamageType.PLAYER_MAGIC || damagee instanceof Player p && p.isBlocking()) {
					damagee.setHealth(0.0);
				} else {
					e.setCancelled(false);
					damagee.setHealth(0.1);
					e.setDamage(10);
				}
				CustomDrops.loot(damagee, damager);
			} else {
				if(finalDamage > absorption) {
					damagee.setAbsorptionAmount(0.0);
					finalDamage -= absorption;
				} else {
					damagee.setAbsorptionAmount(absorption - finalDamage);
					finalDamage = 0.0;
				}

				damagee.setHealth(oldHealth - finalDamage);
				if(type == DamageType.MELEE || type == DamageType.IFRAME_ENVIRONMENTAL) {
					damagee.setNoDamageTicks(9);
				}
				if(damagee instanceof Mob && damager instanceof LivingEntity) {
					((Mob) damagee).setTarget((LivingEntity) damager);
				}

				// apply knockback
				if((type == DamageType.MELEE || type == DamageType.RANGED) && damager != null) {
					double antiKB = Objects.requireNonNull(damagee.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).getValue();
					double factor = 0.33 * (1 - antiKB);
					Vector oldVelocity = damagee.getVelocity();
					double x = oldVelocity.getX();
					double y = oldVelocity.getY();
					double z = oldVelocity.getZ();
					if(type == DamageType.RANGED) {
						factor *= 0.25;
					}

					if(damagee instanceof Player p && p.isBlocking()) {
						factor *= 0.5;
					}

					if(damagee instanceof Player && !(damager instanceof Player)) {
						double rawYaw = damagee.getLocation().getYaw();
						double yaw = Math.toRadians(rawYaw);
						if(rawYaw <= -90) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 90) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw < 0) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 0) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						}
					} else {
						double rawYaw = damager.getLocation().getYaw();
						double yaw = Math.toRadians(rawYaw);
						if(rawYaw <= -90) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 90) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + -1 * factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw < 0) {
							x += factor * damager.getVelocity().getX() + factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						} else if(rawYaw >= 0) {
							x += factor * damager.getVelocity().getX() + -1 * factor * Math.abs(Math.sin(yaw));
							y += 1.33 * factor;
							z += factor * damager.getVelocity().getZ() + factor * Math.abs(Math.cos(yaw));
						}
					}
					damagee.setVelocity(new Vector(x, y, z));
				}

				// change nametag health
				PluginUtils.changeName(damagee);

				// update bossbar if applicable
				try {
					if(Objects.requireNonNull(damagee.getCustomName()).contains("Sadan")) {
						Objects.requireNonNull(Plugin.getInstance().getServer().getBossBar(new NamespacedKey(Plugin.getInstance(), "sadan"))).setProgress(damagee.getHealth() / 600);
						Objects.requireNonNull(Plugin.getInstance().getServer().getBossBar(new NamespacedKey(Plugin.getInstance(), "sadan"))).setTitle(damagee.getCustomName());
					}
				} catch(Exception exception) {
					// nothing here lol
				}

				// stop stupidly annoying arrows
				if(damager instanceof Arrow arrow) {
					if(arrow.getPierceLevel() == 0) {
						arrow.remove();
					} else {
						arrow.setPierceLevel(arrow.getPierceLevel() - 1);
						Vector arrowSpeed = arrow.getVelocity();
						Bukkit.getScheduler().runTaskLater(Plugin.getInstance(), () -> arrow.setVelocity(arrowSpeed), 1L);
					}
				}
			}
		}
	}

	public static void teleport(Entity e, Entity damager, int radius) {
		Random random = new Random();
		Location l = e.getLocation();
		Location l2 = e.getLocation();
		Vector added = new Vector(random.nextInt(radius * 2 + 1) - radius, 0, random.nextInt(radius * 2 + 1) - radius);
		l.add(added);
		l2.add(added);
		while(true) {
			if(l.getY() > 319 || l2.getY() < -63) {
				damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The " + e.getName() + " could not find a spot to teleport to!");
				break;
			} else if(l.getBlock().isEmpty()) {
				e.teleport(l);
				damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The " + e.getName() + " has teleported away!");
				break;
			} else if(l2.getBlock().isEmpty()) {
				e.teleport(l2);
				damager.sendMessage(ChatColor.RED + String.valueOf(ChatColor.BOLD) + "The " + e.getName() + " has teleported away!");
				break;
			}
			l.add(0, 1, 0);
			l2.add(0, -1, 0);
		}
		e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
	}

	public static void setEvent(EntityDamageEvent e) {
		CustomDamage.e = e;
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		CustomDamage.e = e;
		if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			if(entity.getHealth() > 0) {
				DamageType type;
				switch(e.getCause()) {
					case BLOCK_EXPLOSION, ENTITY_ATTACK, ENTITY_EXPLOSION, THORNS -> type = DamageType.MELEE;
					case PROJECTILE, SONIC_BOOM -> type = DamageType.RANGED;
					case DRAGON_BREATH, MAGIC -> type = DamageType.MAGIC;
					case FALLING_BLOCK -> type = DamageType.ENVIRONMENTAL;
					case LIGHTNING -> type = DamageType.IFRAME_ENVIRONMENTAL;
					default -> {
						return;
					}
				}

				if(entity.getNoDamageTicks() == 0 || e.getDamager() instanceof Arrow) {
					double originalDamage;
					if(e.getDamager() instanceof Arrow arrow && arrow.getScoreboardTags().contains("TerminatorArrow")) {
						originalDamage = arrow.getDamage();
					} else {
						originalDamage = e.getDamage();
					}

					// apply intelligence to players
					if(e.getDamager() instanceof Player p) {
						if(e.getEntity() instanceof Monster || e.getEntity().getScoreboardTags().contains("SkyblockBoss")) {
							try {
								Score score = Objects.requireNonNull(Objects.requireNonNull(Plugin.getInstance().getServer().getScoreboardManager()).getMainScoreboard().getObjective("Intelligence")).getScore(p.getName());
								if(score.getScore() < 2500) {
									score.setScore(score.getScore() + 1);
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Intelligence: " + score.getScore() + "/2500", ChatColor.AQUA.asBungee()));
								} else {
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("Intelligence: " + score.getScore() + "/2500 " + ChatColor.RED + ChatColor.BOLD + "MAX INTELLIGENCE", ChatColor.AQUA.asBungee()));
								}
							} catch(Exception exception) {
								Plugin.getInstance().getLogger().info("Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
								Bukkit.broadcastMessage(ChatColor.RED + "Could not find Intelligence objective!  Please do not delete the objective - it breaks the plugin");
								return;
							}
						}
					}

					Entity damager = e.getDamager();
					customMobs(entity, damager, originalDamage, type);
				}
			}
		}
	}
}