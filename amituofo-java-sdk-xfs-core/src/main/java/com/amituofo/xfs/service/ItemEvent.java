package com.amituofo.xfs.service;

public class ItemEvent {
	public final static Integer EXEC_BEGIN = 1;
	public final static Integer EXEC_END = 2;
	public final static Integer VALUE_CHANGED = 3;

	public final static Integer ITEM_FOUND = 4;

	public final static Integer ITEM_DELETED = 5;
	public final static Integer ITEM_DELETE_FAILED = 6;
	public final static Integer ITEM_CREATED = 7;
	public final static Integer ITEM_CREATE_FAILED = 8;
	public final static Integer ITEM_MODIFIED = 9;
	public final static Integer ITEM_MODIFY_FAILED = 10;
//	FILE_FOUND, FOLDER_FOUND, 
//	FILE_DELETED, FOLDER_DELETED, 
//	FILE_RENAME, FOLDER_RENAME, 
//	FILE_MODIFIED, 
}
