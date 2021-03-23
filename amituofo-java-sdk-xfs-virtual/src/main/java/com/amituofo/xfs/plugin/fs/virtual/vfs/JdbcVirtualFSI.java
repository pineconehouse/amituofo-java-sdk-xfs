package com.amituofo.xfs.plugin.fs.virtual.vfs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.util.DigestUtils;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.item.VirtualFileItem;
import com.amituofo.xfs.plugin.fs.virtual.item.VirtualFolderItem;
import com.amituofo.xfs.plugin.fs.virtual.item.VirtualItemspace;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.DefaultVirtualFSI;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFSI;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemException;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemRootspace;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;

public abstract class JdbcVirtualFSI extends DefaultVirtualFSI {
	private boolean droped;
	protected String TABLE_NAME;
	private final DataSource ds;
	private String dbname;

	// public JdbcVirtualFSI(String fsid, VirtualFileSystemEntry fileSystemEntry, String folderPath, String dbname) {
	// super(fsid, fileSystemEntry, folderPath);
	// if (StringUtils.isNotEmpty(dbname)) {
	// this.TABLE_NAME = dbname + ".FT_" + DigestUtils.calcMD5ToHex(folderPath);
	// } else {
	// this.TABLE_NAME = "FT_" + DigestUtils.calcMD5ToHex(folderPath);
	// }
	//
	// ds = null;
	// }

	public JdbcVirtualFSI(String fsid, VirtualFileSystemEntry fileSystemEntry, String folderPath, String dbname, DataSource ds) {
		super(fsid, fileSystemEntry, folderPath);
		this.dbname = dbname;
		if (StringUtils.isNotEmpty(dbname)) {
			this.TABLE_NAME = dbname + ".FT_" + DigestUtils.calcMD5ToHex(folderPath);
		} else {
			this.TABLE_NAME = "FT_" + DigestUtils.calcMD5ToHex(folderPath);
		}

		this.ds = ds;
		this.droped = false;
	}

	public String getDbname() {
		return dbname;
	}

	protected Connection getConnection() throws SQLException {
		lock();

		return ds.getConnection();
	}

	@Override
	public void init() throws VirtualFileSystemException {
		createTable();
	}

	@Override
	public void close() {
		// if (lock()) {
		// if (conn != null) {
		// try {
		// this.conn.close();
		// // this.unlock();
		// } catch (Exception e) {
		// }
		// }
		// this.conn = null;
		// // System.out.println("Closed " + dblocation);
		// unlock();
		// } else {
		// // System.out.println("Unable close " + dblocation);
		// }
	}

	@Override
	public VirtualFolderItem getFolderItem() {
		return thisFolderItem;
	}

	protected void createTable() throws VirtualFileSystemException {
		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			// stat.executeUpdate("DROP TABLE IF EXISTS "+tableName+"");
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS "
					+ TABLE_NAME
					+ "("
					+ "ID1 BIGINT NOT NULL, "
					+ "ID2 BIGINT NOT NULL, "
					+ "FNAME VARCHAR(2000) NOT NULL, "
					+ "FTYPE CHAR(1) , "
					+ "SIZE BIGINT, "
					+ "CREATION_TIME BIGINT, "
					+ "LAST_MODIFIED_TIME BIGINT, "
					+ "CONTENT VARCHAR(100), "
					+ "PRIMARY KEY (ID1,ID2)"
					+ ")");

			droped = false;
		} catch (SQLException e) {
			throw new VirtualFileSystemException(e);
		} finally {
			closeAndCommit(conn, stat);
		}
	}

	// @Override
	// public void clear() throws VirtualFileSystemException {
	// lastUpdatetime = System.currentTimeMillis();
	// Statement stat = null;
	// try {
	// stat = getConnection().createStatement();
	// stat.executeUpdate("TRUNCATE TABLE " + TABLE_NAME + "");
	// } catch (SQLException e) {
	// throw new VirtualFileSystemException(e);
	// } finally {
	// closeAndCommit(stat);
	// }
	// }

	private String whereId(String filename) {
		long id1 = DigestUtils.APHash(filename);
		long id2 = DigestUtils.SDBMHash(filename);
		return " ID1=" + id1 + " AND ID2=" + id2;
	}

	private String insertId(String filename) {
		long id1 = DigestUtils.APHash(filename);
		long id2 = DigestUtils.SDBMHash(filename);
		return " ID1=" + id1 + " AND ID2=" + id2;
	}

	@Override
	public synchronized boolean delete(String filename, ItemType type) throws VirtualFileSystemException {
		if (droped) {
			return false;
		}
		lastUpdatetime = System.currentTimeMillis();

		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();

			char tp = (type == ItemType.Directory ? 'D' : 'F');
			String sql = "DELETE FROM " + TABLE_NAME + " WHERE FTYPE='" + tp + "' AND " + whereId(filename);

			int c = stat.executeUpdate(sql);

			if (type == ItemType.Directory) {
				String folderPath = URLUtils.catPath(thisFolderPath, filename, fileSystemEntry.getSeparatorChar());
				// System.out.println("-Debug- delete folder " + folderPath);
				virtualFileSystem.cleanAndDeleteVirtualFSI(folderPath);
			}

			return c == 1;
		} catch (SQLException e) {
			throw new VirtualFileSystemException(e);
		} finally {
			closeAndCommit(conn, stat);
		}
	}

	@Override
	public synchronized void newFile(String filename, long length, long createTime) throws VirtualFileSystemException {
		if (droped) {
			return;
		}
		lastUpdatetime = System.currentTimeMillis();

		int c = 0;
		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();

			StringBuilder buf = new StringBuilder();

			// System.out.println(super.getId()+" "+fileItem.getPath()+" id="+calcItemId(folderHash, folderLen, fileItem)+">> parent="+parent+"
			// folderHash="+folderHash+" folderLen="+folderLen+" fileNameHash="+fileItem.getName().toLowerCase().hashCode());
			long id1 = DigestUtils.APHash(filename);
			long id2 = DigestUtils.SDBMHash(filename);

			buf.append("INSERT INTO " + TABLE_NAME + " VALUES(");
			buf.append(id1);
			buf.append(", ");
			buf.append(id2);
			buf.append(", '");
			buf.append(StringUtils.encodeBase64String(filename)); // FNAME
			buf.append("', 'F', ");
			buf.append(length); // SIZE
			buf.append(", ");
			buf.append(createTime); // LAST_MODIFIED_TIME
			buf.append(", ");
			buf.append(createTime); // LAST_MODIFIED_TIME
			buf.append(", '");
			buf.append(""); // CONTENT
			buf.append("'");
			buf.append(")");

			// System.out.println(thisFolderPath + "/" + filename + "\t" + buf.toString());
			// System.out.println(thisFolderPath + "/" + filename + "\t" + buf.toString());

			c = stat.executeUpdate(buf.toString());

			if (c == 0) {
				throw new VirtualFileSystemException("Failed to create file " + filename + " in " + super.thisFolderPath);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new VirtualFileSystemException("Failed to create file " + filename + " in " + super.thisFolderPath, e);
		} finally {
			closeAndCommit(conn, stat);
		}
	}

	@Override
	public synchronized void newDirectory(String filename) throws VirtualFileSystemException {
		if (droped) {
			return;
		}

		if (exist(filename, ItemType.Directory)) {
			return;
		}

		lastUpdatetime = System.currentTimeMillis();

		int c = 0;
		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();

			StringBuilder buf = new StringBuilder();

			// System.out.println(super.getId()+" "+fileItem.getPath()+" id="+calcItemId(folderHash, folderLen, fileItem)+">> parent="+parent+"
			// folderHash="+folderHash+" folderLen="+folderLen+" fileNameHash="+fileItem.getName().toLowerCase().hashCode());
			long id1 = DigestUtils.APHash(filename);
			long id2 = DigestUtils.SDBMHash(filename);

			buf.append("INSERT INTO " + TABLE_NAME + " VALUES(");
			buf.append(id1);
			buf.append(", ");
			buf.append(id2);
			buf.append(", '");
			buf.append(StringUtils.encodeBase64String(filename)); // FNAME
			buf.append("', 'D', ");
			buf.append(0); // SIZE
			buf.append(", ");
			buf.append(System.currentTimeMillis()); // LAST_MODIFIED_TIME
			buf.append(", ");
			buf.append(System.currentTimeMillis()); // LAST_MODIFIED_TIME
			buf.append(", '");
			buf.append(""); // CONTENT
			buf.append("'");
			buf.append(")");

			// System.out.println(thisFolderPath + "/" + filename + "\t" + buf.toString());

			c = stat.executeUpdate(buf.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new VirtualFileSystemException("Failed to create folder " + filename + " in " + thisFolderPath, e);
		} finally {
			closeAndCommit(conn, stat);

			if (c == 0) {
				throw new VirtualFileSystemException("Failed to create folder " + filename + " in " + super.thisFolderPath);
			}
		}
	}

	@Override
	public synchronized boolean renameTo(String newFoldername) {
		if (droped) {
			return false;
		}

		// this.close();
		// File dbfile = new File(dblocation + ".mv.db");
		// String newFolderPath = URLUtils.catPath(thisFolderItem.getParent().getPath(), newFoldername, memFileSystemEntry.getSeparatorChar());
		// File newdbfile = new File(dbfile.getParent() + File.separator + virtualFileSystem.toDbid(newFolderPath) + ".mv.db");
		// return dbfile.renameTo(newdbfile);
		return false;
	}

	@Override
	public synchronized boolean rename(String filename, String newfilename, ItemType type) throws VirtualFileSystemException {
		if (droped) {
			return false;
		}

		if (StringUtils.isEmpty(newfilename) || StringUtils.isEmpty(filename)) {
			return false;
		}

		if (filename.equals(newfilename)) {
			return true;
		}

		lastUpdatetime = System.currentTimeMillis();

		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();

			char tp = (type == ItemType.Directory ? 'D' : 'F');
			String sql = "UPDATE " + TABLE_NAME + " SET FNAME='" + StringUtils.encodeBase64String(newfilename) + "' WHERE  FTYPE='" + tp + "' AND " + whereId(filename);

			int c = stat.executeUpdate(sql);

			if (c == 1) {
				// 目录重命名后需要关闭数据库变更db名称等
				if (type == ItemType.Directory) {
					String folderPath = URLUtils.catPath(thisFolderPath, filename, fileSystemEntry.getSeparatorChar());
					VirtualFSI oldFSI = virtualFileSystem.getVirtualFSI(folderPath, ItemType.Directory);

					boolean fsiRenamed = oldFSI.renameTo(newfilename);

					if (fsiRenamed) {
						virtualFileSystem.removeVirtualFSICache(folderPath);
						conn.commit();
						return true;
					} else {
						conn.rollback();
						return false;
					}
				} else {
					conn.commit();
					// 文件重命名后直接ok
					return true;
				}
			}

			conn.rollback();
			return false;
		} catch (SQLException e) {
			if (e.getMessage().contains("Unique index or primary key violation")) {
				throw new VirtualFileSystemException("Already has a file named " + newfilename);
			}
			throw new VirtualFileSystemException(e);
		} finally {
			close(conn, stat);
		}
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) throws VirtualFileSystemException {
		list(filter, handler, null);
	}

	@Override
	public void listFolders(ItemFilter filter, ItemHandler handler) throws VirtualFileSystemException {
		list(filter, handler, ItemType.Directory);
	}

	@Override
	public int count() throws VirtualFileSystemException {
		return count(null);
	}

	private int count(ItemType itemtype) throws VirtualFileSystemException {
		if (droped) {
			return 0;
		}

		Statement stat = null;
		ResultSet result = null;
		Connection conn = null;
		try {
			String sql = "SELECT COUNT(1) FROM " + TABLE_NAME;
			if (itemtype == ItemType.Directory) {
				sql += " WHERE FTYPE='D'";
			} else if (itemtype == ItemType.File) {
				sql += " WHERE FTYPE='F'";
			}

			conn = getConnection();
			stat = conn.createStatement();
			result = stat.executeQuery(sql);

			boolean foundRecords = result.next();

			if (!foundRecords) {
				return 0;
			}

			return result.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VirtualFileSystemException(e);
		} finally {
			close(conn, stat, result);
		}
	}

	private void list(ItemFilter filter, ItemHandler handler, ItemType itemtype) throws VirtualFileSystemException {
		if (droped) {
			return;
		}

		Statement stat = null;
		ResultSet result = null;
		Connection conn = null;
		try {
			String sql = "SELECT FNAME, FTYPE, SIZE, CREATION_TIME, LAST_MODIFIED_TIME FROM " + TABLE_NAME;
			if (itemtype == ItemType.Directory) {
				sql += " WHERE FTYPE='D'";
			} else if (itemtype == ItemType.File) {
				sql += " WHERE FTYPE='F'";
			}

			conn = getConnection();
			stat = conn.createStatement();
			result = stat.executeQuery(sql);

			boolean foundRecords = result.next();

			if (!foundRecords) {
				return;
			}

			VirtualFileSystemRootspace vfs = fileSystemEntry.getVirtualFileSystem();

			do {
				// 左侧文件名
				String fileName = StringUtils.decodeBase64String(result.getString(1));
				// Type
				char type = result.getString(2).charAt(0);
				// 左侧文件创建时间
				long fileCreateTime = result.getLong(4);
				// 左侧文件修改时间
				long fileLastUpdateTime = result.getLong(5);

				// System.out.println(path);

				VirtualItemspace itemspace = (VirtualItemspace) fileSystemEntry.getDefaultItemspace();

				Item item;
				if (type == 'D') {
					String path = URLUtils.catPath(thisFolderPath, fileName, fileSystemEntry.getSeparatorChar());
					item = new VirtualFolderItem(itemspace, vfs.getVirtualFSI(path, ItemType.Directory));
				} else {
					// 左侧文件大小
					long fileSize = result.getLong(3);
					// 左侧文件
					// String fileContentHash = result.getString(6);

					item = new VirtualFileItem(itemspace, this, fileName);
					((ItemHiddenFunction) item).setSize(fileSize);
				}

				((ItemHiddenFunction) item).setName(fileName);
				((ItemHiddenFunction) item).setCreateTime(fileCreateTime);
				((ItemHiddenFunction) item).setLastUpdateTime(fileLastUpdateTime);

				HandleFeedback feedback = null;
				if (filter != null) {
					if (filter.accept(item)) {
						feedback = handler.handle(ItemEvent.ITEM_FOUND, item);
					}
				} else {
					feedback = handler.handle(ItemEvent.ITEM_FOUND, item);
				}

				if (feedback == HandleFeedback.interrupted) {
					break;
				}

			} while (result.next());

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VirtualFileSystemException(e);
		} finally {
			close(conn, stat, result);
		}
	}

	@Override
	public String getFolderName() {
		return thisFolderName;
	}

	@Override
	public String getFolderPath() {
		return thisFolderPath;
	}

	@Override
	public boolean exist(String filename, ItemType type) throws VirtualFileSystemException {
		if (droped) {
			return false;
		}
		// if (type == ItemType.Directory && "/".equals(filename)) {
		// return true;
		// }

		Statement stat = null;
		ResultSet result = null;
		Connection conn = null;
		try {
			char tp = (type == ItemType.Directory ? 'D' : 'F');
			String sql = "SELECT 1 FROM " + TABLE_NAME + " WHERE FTYPE='" + tp + "' AND " + whereId(filename);

			conn = getConnection();
			stat = conn.createStatement();
			result = stat.executeQuery(sql);

			boolean foundRecords = result.next();

			return foundRecords;
		} catch (Exception e) {
			e.printStackTrace();
			throw new VirtualFileSystemException(e);
		} finally {
			close(conn, stat, result);
		}
	}

	@Override
	public synchronized void setFileLength(String filename, long length) throws VirtualFileSystemException {
		if (droped) {
			return;
		}
		lastUpdatetime = System.currentTimeMillis();

		int c = 0;
		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();

			String sql = "UPDATE " + TABLE_NAME + " SET SIZE=" + length + ",LAST_MODIFIED_TIME=" + System.currentTimeMillis() + " WHERE FTYPE='F'" + " AND " + whereId(filename);

			c = stat.executeUpdate(sql);
		} catch (SQLException e) {
			throw new VirtualFileSystemException(e);
		} finally {
			closeAndCommit(conn, stat);

			if (c == 0) {
				this.newFile(filename, length, System.currentTimeMillis());
			}
		}
	}

	@Override
	public synchronized boolean remove() throws VirtualFileSystemException {
		if (droped) {
			return true;
		}

		if (!isDoesnotHaveFolder()) {
			return false;
		}

		boolean deleted = ((VirtualFolderItem) this.thisFolderItem.getParent()).getVirtualFSI().delete(this.thisFolderItem.getName(), ItemType.Directory);
		if (deleted) {
			return drop();
		}
		// if (removeSubs(this.getFolderItem())) {
		// return drop();
		// }
		//
		return false;
	}

	private boolean isDoesnotHaveFolder() {
		try {
			return count(ItemType.Directory) <= 0;
		} catch (VirtualFileSystemException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected synchronized boolean drop() throws VirtualFileSystemException {
		if (droped) {
			return false;
		}

		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();

			String sql = "DROP TABLE " + TABLE_NAME;
			// System.out.println(sql);

			stat.execute(sql);

			droped = true;
			return droped;
		} catch (SQLException e) {
			throw new VirtualFileSystemException(e);
		} finally {
			closeAndCommit(conn, stat);
		}
	}

	// private boolean removeSubs(VirtualFolderItem folder) throws VirtualFileSystemException {
	// final List<VirtualFolderItem> subfolders = new ArrayList<VirtualFolderItem>();
	// folder.getVirtualFSI().listFolders(null, new ItemHandler() {
	//
	// @Override
	// public void exceptionCaught(Item data, Throwable e) {
	// }
	//
	// @Override
	// public HandleFeedback handle(Integer meta, Item obj) {
	// if (obj != null) {
	// VirtualFolderItem subfolder = (VirtualFolderItem) obj;
	// // subfolders.add(subfolder);
	// try {
	// if (!removeSubs(subfolder)) {
	// return HandleFeedback.interrupted;
	// }
	// } catch (VirtualFileSystemException e) {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }
	// });
	//
	// int c = 0;
	// for (VirtualFolderItem virtualFolderItem : subfolders) {
	// System.out.println("-Debug- delete folder " + folder.getPath());
	// if (((JdbcVirtualFSI) virtualFolderItem.getVirtualFSI()).drop()) {
	// c++;
	// }
	// }
	// return c == subfolders.size();
	// // return folder.delete();
	// // System.out.println("-Debug- delete folder "+folder.getPath());
	// // return ((JdbcVirtualFSI)folder.getVirtualFSI()).drop();
	// }

	public synchronized void write(String filename, String content) throws VirtualFileSystemException {
		if (droped) {
			return;
		}
		lastUpdatetime = System.currentTimeMillis();

		Statement stat = null;
		Connection conn = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();

			String sql = "UPDATE "
					+ TABLE_NAME
					+ " SET CONTENT='"
					+ StringUtils.encodeBase64String(content)
					+ "', LAST_MODIFIED_TIME="
					+ System.currentTimeMillis()
					+ " WHERE  FTYPE='F'"
					+ " AND "
					+ whereId(filename);

			int c = stat.executeUpdate(sql);
		} catch (SQLException e) {
			throw new VirtualFileSystemException(e);
		} finally {
			closeAndCommit(conn, stat);
		}
	}

	@Override
	public String read(String filename) throws VirtualFileSystemException {
		if (droped) {
			return null;
		}

		Statement stat = null;
		ResultSet result = null;
		Connection conn = null;
		try {
			String sql = "SELECT CONTENT FROM " + TABLE_NAME + " WHERE  FTYPE='F'" + " AND " + whereId(filename);

			conn = getConnection();
			stat = conn.createStatement();
			result = stat.executeQuery(sql);

			boolean foundRecords = result.next();

			if (!foundRecords) {
				return null;
			}

			String val = result.getString(1);
			return StringUtils.decodeBase64String(val);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VirtualFileSystemException(e);
		} finally {
			close(conn, stat, result);
		}
	}

	// @Override
	// public Object getAttribute(String filename, String attrname) throws VirtualFileSystemException {
	// // Statement stat = null;
	// // ResultSet result = null;
	// // try {
	// // String sql = "SELECT " + attrname + " FROM " + TABLE_NAME + " WHERE FTYPE='F'" + " AND FNAME='" +
	// // StringUtils.encodeBase64String(filename) + "'";
	// //
	// // stat = getConnection().createStatement();
	// // result = stat.executeQuery(sql);
	// //
	// // boolean foundRecords = result.next();
	// //
	// // if (!foundRecords) {
	// // return null;
	// // }
	// //
	// // Object val = result.getObject(1);
	// // return val;
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // throw new VirtualFileSystemException(e);
	// // } finally {
	// // if (result != null)
	// // try {
	// // result.close();
	// // } catch (SQLException e) {
	// // }
	// // if (stat != null)
	// // try {
	// // stat.close();
	// // } catch (SQLException e) {
	// // }
	// // }
	// return null;
	// }

	@Override
	public Long[] getSystemAttribute(String filename, ItemType type) throws VirtualFileSystemException {
		if (droped) {
			return null;
		}

		Statement stat = null;
		ResultSet result = null;
		Connection conn = null;
		try {
			char tp = (type == ItemType.Directory ? 'D' : 'F');
			String sql = "SELECT SIZE, CREATION_TIME, LAST_MODIFIED_TIME FROM " + TABLE_NAME + " WHERE  FTYPE='" + tp + "'" + " AND " + whereId(filename);

			conn = getConnection();
			stat = conn.createStatement();
			result = stat.executeQuery(sql);

			boolean foundRecords = result.next();

			if (!foundRecords) {
				return null;
			}

			Long size = result.getLong(1);
			Long createTime = result.getLong(2);
			Long lastModifiedTime = result.getLong(3);
			return new Long[] { size, createTime, lastModifiedTime };
		} catch (Exception e) {
			e.printStackTrace();
			throw new VirtualFileSystemException(e);
		} finally {
			close(conn, stat, result);
		}
	}

	protected void closeAndCommit(Connection conn, Statement stat) {
		try {
			if (stat != null) {
				stat.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			unlock();
		}
	}

	protected void close(Connection conn, Statement stat) {
		try {
			if (stat != null) {
				stat.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			unlock();
		}
	}

	private void close(Connection conn, Statement stat, ResultSet result) {
		if (result != null)
			try {
				result.close();
			} catch (SQLException e) {
			}

		close(conn, stat);
	}
}
