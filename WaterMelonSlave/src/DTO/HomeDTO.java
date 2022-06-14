package DTO;

public class HomeDTO {
	private String UUID;
	private String command;
	private Double X;
	private Double Y;
	private Double Z;
	private String world;
	
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Double getX() {
		return X;
	}
	public void setX(Double x) {
		X = x;
	}
	public Double getY() {
		return Y;
	}
	public void setY(Double y) {
		Y = y;
	}
	public Double getZ() {
		return Z;
	}
	public void setZ(Double z) {
		Z = z;
	}
	
}
