package listeners;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.*;

import static listeners.CustomDamage.customMobs;

public class NonEntityDamage implements Listener {
	private final Map<LivingEntity, Long> noDamageTimes = new WeakHashMap<>();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof LivingEntity entity) {
			e.setCancelled(true);
			DamageType type;
			switch(e.getCause()) {
				case THORNS -> type = DamageType.MELEE;
				case POISON, WITHER -> type = DamageType.MAGIC;
				case CONTACT, DROWNING, DRYOUT, FIRE, FIRE_TICK, FREEZE, HOT_FLOOR, LAVA, MELTING, STARVATION, SUFFOCATION ->
						type = DamageType.ENVIRONMENTAL;
				case CUSTOM -> type = DamageType.IFRAME_ENVIRONMENTAL;
				case FALL, FLY_INTO_WALL -> type = DamageType.FALL;
				case CRAMMING, KILL, SUICIDE, VOID, WORLD_BORDER -> type = DamageType.ABSOLUTE;
				default -> {
					return;
				}
			}

			CustomDamage.setEvent(e);

			long currentTime = System.currentTimeMillis();
			boolean hasLastDamageTime = noDamageTimes.containsKey(entity);
			long lastDamageTime = noDamageTimes.computeIfAbsent(entity, entity2 -> currentTime);

			if(hasLastDamageTime && currentTime - lastDamageTime > 490 || type == DamageType.ABSOLUTE) {
				customMobs(entity, null, e.getDamage(), type);
				noDamageTimes.put(entity, currentTime);
			}

			if(entity.isDead()) {
				entity.remove();
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		noDamageTimes.remove(e.getEntity());
	}
}