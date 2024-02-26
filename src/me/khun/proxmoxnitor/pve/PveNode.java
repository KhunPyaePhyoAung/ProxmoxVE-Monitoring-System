package me.khun.proxmoxnitor.pve;

import java.util.List;

public class PveNode {

	private String name;

	private PveStatus status;

	private List<PveRrdData> rrDataList;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public PveStatus getStatus() {
		return status;
	}

	public void setStatus(PveStatus status) {
		this.status = status;
	}

	public List<PveRrdData> getRrDataList() {
		return rrDataList;
	}

	public void setRrDataList(List<PveRrdData> rrDataList) {
		this.rrDataList = rrDataList;
	}

}
