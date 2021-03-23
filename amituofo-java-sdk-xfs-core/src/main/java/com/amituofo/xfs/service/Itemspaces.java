package com.amituofo.xfs.service;

import com.amituofo.common.ex.ServiceException;

public interface Itemspaces {
	Itemspace getDefaultItemspace();

	Itemspace getItemspace(String spaceName);

	Itemspace[] getItemspaces() throws ServiceException;

	Itemspace[] getItemspaces(ItemspaceFilter filter) throws ServiceException;

	void createItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException;

	void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException;

	void deleteItemSpace(String name) throws ServiceException;

}
