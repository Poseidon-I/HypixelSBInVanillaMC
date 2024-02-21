package listeners;

public enum DamageType {
	MELEE,
	/*
	 * Melee Damage Type
	 * ------------------
	 * Used for all damage via Melee Weapons and Non-Environmental Abilities (e.g. Atoned Horror TNT)
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: Normal
	 */

	RANGED,
	/*
	 * Ranged Damage Type
	 * ------------------
	 * Used for all damage via Ranged Weapons (e.g. Bows, Tridents, Wither Skulls)
	 * DOES NOT PROC INVULNERABILITY TICKS
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: Reduced
	 */
	MAGIC,
	/*
	 * Magic Damage Type
	 * ------------------
	 * Used for all damage via Potions and Status Effects
	 * Affected by: Toughness, Protection, Resistance
	 * Knockback: None
	 */

	PLAYER_MAGIC,
	/*
	 * Player Magic Damage Type
	 * ------------------
	 * Used for all damage via Player Abilities (e.g. Wither Impact, Ice Spray) and Potion Effects
	 * Affected by: Toughness, Protection, Resistance
	 * Knockback: None
	 */

	ENVIRONMENTAL,
	/*
	 * Environmental Damage Type
	 * ------------------
	 * Used for all damage via other methods (e.g. Fire)
	 * Affected by: Armor, Toughness, Protection, Resistance
	 * Knockback: None
	 */

	FALL,
	/*
	 * Fall Damage Type
	 * ------------------
	 * Used for all damage via other methods (e.g. Fall, Fire)
	 * Affected by: Feather Falling, Toughness, Protection, Resistance
	 * Knockback: None
	 */

	ABSOLUTE
	/*
	 * ABSOLUTE Damage Type
	 * ------------------
	 * Used for damage that will always deal full damage
	 * Affected by: NOTHING
	 * Knockback: None
	 */
}