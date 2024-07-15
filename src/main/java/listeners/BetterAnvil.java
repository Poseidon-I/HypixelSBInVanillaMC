package listeners;

/*
 * Code obtained from EnchBook plugin
 * Github: https://github.com/ShaneBeee/EnchBook
 */

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BetterAnvil implements Listener {
	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent e) {
		List<HumanEntity> viewers = e.getViewers();
		if(viewers.isEmpty()) return;

		AnvilInventory inventory = e.getInventory();
		inventory.setMaximumRepairCost(50);
		ItemStack FIRST_ITEM = inventory.getItem(0);
		ItemStack SECOND_ITEM = inventory.getItem(1);

		if((FIRST_ITEM == null) || (SECOND_ITEM == null)) return;

		if(SECOND_ITEM.getType() == Material.ENCHANTED_BOOK) {
			if(FIRST_ITEM.getType() != Material.ENCHANTED_BOOK) {
				ItemStack result = FIRST_ITEM.clone();
				ItemMeta bookMeta = SECOND_ITEM.getItemMeta();
				assert bookMeta != null;
				for(Enchantment enchantment : ((EnchantmentStorageMeta) bookMeta).getStoredEnchants().keySet()) {
					if(canEnchant(FIRST_ITEM, enchantment)) {
						int bookLevel = ((EnchantmentStorageMeta) bookMeta).getStoredEnchantLevel(enchantment);
						int itemLevel = FIRST_ITEM.getEnchantmentLevel(enchantment);
						if(itemLevel < bookLevel) {
							result.addUnsafeEnchantment(enchantment, bookLevel);
						}
					}
				}
				e.setResult(result);
				if(inventory.getRepairCost() > 50) {
					inventory.setRepairCost(50);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean canEnchantAboveMax(int i, Enchantment enchantment, Player player) {
		return true;
	}

	public boolean canEnchant(ItemStack itemStack, Enchantment enchantment) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		AtomicBoolean canEnchant = new AtomicBoolean(enchantment.canEnchantItem(itemStack));

		if(itemMeta != null) {
			if(itemStack.getType() == Material.ELYTRA && enchantment == Enchantment.PROTECTION) {
				canEnchant.set(true);
			}
			itemMeta.getEnchants().keySet().forEach(ench -> {
				if(ench != enchantment && ench.conflictsWith(enchantment)) {
					canEnchant.set(false);
				}
			});
		}
		return canEnchant.get();
	}
}