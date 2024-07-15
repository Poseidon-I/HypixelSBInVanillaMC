package listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ArrowSounds implements Listener {
	@EventHandler
	public void onArrowHitEntity(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Arrow a) {
			if(a.getShooter() instanceof Player p) {
				p.playSound(p, Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 0.79368752611448590621283707774885F);
				if(e.getEntity() instanceof LivingEntity entity) {
					entity.setNoDamageTicks(0);
				}
			}
		}
	}
}