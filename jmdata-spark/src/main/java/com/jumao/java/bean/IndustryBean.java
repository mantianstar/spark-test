package com.jumao.java.bean;

import java.io.Serializable;

public class IndustryBean implements Serializable {

	private static final long serialVersionUID = -5103213313112202001L;
	private String id;
	private String rank;
	private String parentId;
	private String parentCode;
	private String industryCode;
	private String industryName;
	private String isBulk;
	private String alias;

	public IndustryBean(String id, String rank, String parentId,
                        String parentCode, String industryCode, String industryName,
                        String isBulk, String alias) {
		this.id = id;
		this.rank = rank;
		this.parentId = parentId;
		this.parentCode = parentCode;
		this.industryCode = industryCode;
		this.industryName = industryName;
		this.isBulk = isBulk;
		this.alias = alias;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getIsBulk() {
		return isBulk;
	}

	public void setIsBulk(String isBulk) {
		this.isBulk = isBulk;
	}

}
