package com.amituofo.xfs.service;

public class Bookmark {

	private String spacename;
	private String path;
	private String title;
	private Object data;
	// private URI uri;

	public Bookmark(String spacename, String path) {
		this.title = path;
		this.spacename = spacename;
		this.path = path;
	}

	public Bookmark(String title, String rootitem, String path) {
		this.title = title;
		this.spacename = rootitem;
		this.path = path;
	}

	// public Bookmark(URI uri) {
	// this.title = uri.getPath();
	// this.uri = uri;
	// }
	//
	// public Bookmark(String title, URI uri) {
	// this.title = title;
	// this.uri = uri;
	// }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	// public URI getUri() {
	// return uri;
	// }
	//
	// public void setUri(URI uri) {
	// this.uri = uri;
	// }

	public String getSpacename() {
		return spacename;
	}

//	public void setSection(String rootitem) {
//		this.spacename = rootitem;
//	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((spacename == null) ? 0 : spacename.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bookmark other = (Bookmark) obj;
//		if (data == null) {
//			if (other.data != null)
//				return false;
//		} else if (!data.equals(other.data))
//			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (spacename == null) {
			if (other.spacename != null)
				return false;
		} else if (!spacename.equals(other.spacename))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
