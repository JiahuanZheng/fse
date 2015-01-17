package edu.fudan.se.undergraduate.dbObject;

public class AgentInfo {
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private String guid;
	private String address;
	private boolean isOnline;
	private String location;

	public AgentInfo(String guid) {
		this.guid = guid;
		this.address = null;
		this.isOnline = false;
		this.location = null;
	}

	public AgentInfo(String guid, boolean isOnline) {
		this.guid = guid;
		this.address = null;
		this.isOnline = isOnline;
		this.location = null;
	}

	public AgentInfo(String guid, String address, boolean isOnline,
			String location) {
		this.guid = guid;
		this.address = address;
		this.isOnline = isOnline;
		this.location = location;
	}
}
