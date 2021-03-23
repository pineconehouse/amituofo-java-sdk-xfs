package com.amituofo.xfs.service;

import java.io.Serializable;

public class HandleableItemCouple implements Serializable {
	private Item sourceItem;
	private int actionId;
	private Item targetItem;

	public HandleableItemCouple(Item sourceItem) {
		this.sourceItem = sourceItem;
	}

	public HandleableItemCouple(Item sourceItem, Item targetItem) {
		this.sourceItem = sourceItem;
		this.targetItem = targetItem;
	}

	public HandleableItemCouple(Item sourceItem, int actionId, Item targetItem) {
		this.sourceItem = sourceItem;
		this.actionId = actionId;
		this.targetItem = targetItem;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public int getActionId() {
		return actionId;
	}

	public Item getSourceItem() {
		return sourceItem;
	}

	public void setSourceItem(Item sourceItem) {
		this.sourceItem = sourceItem;
	}

	public Item getTargetItem() {
		return targetItem;
	}

	public void setTargetItem(Item targetItem) {
		this.targetItem = targetItem;
	}

}
