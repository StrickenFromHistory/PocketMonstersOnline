package org.pokenet.droplist;

import org.pokenet.items.ItemBean;

public class PokemonDrops {
private int dropChance;
private ItemBean item;

public ItemBean getItem() {
	return item;
}
public void setItem(ItemBean item) {
	this.item = item;
}
public int getDropChance() {
	return dropChance;
}
public void setDropChance(int dropChance) {
	this.dropChance = dropChance;
}
}
