package com.amituofo.xfs.service;

import java.util.List;

public class ItemNode {
	private FolderItem item;
	private List<FolderItem> children;
	private String name;

	public ItemNode(FolderItem item) {
		this(item, null);
	}

	public ItemNode(FolderItem item, String name) {
		super();
		this.name = name;
		this.item = item;
	}

	public String getPath() {
		return item.getPath();
	}

	public String toString() {
		if (name == null) {
			return item.getName();
		}

		return name;
	}

	public FolderItem getItem() {
		return item;
	}

	public List<FolderItem> getChildren() {
		return children;
	}

	public void setChildren(List<FolderItem> children) {
		this.children = children;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemNode)) {
			return false;
		}

		ItemNode a = this;
		ItemNode b = (ItemNode) obj;

		return a.getPath().equals(b.getPath());
	}

}