package com.jumao.java.bean;

import java.io.Serializable;

public class Categories implements Serializable {

	private static final long serialVersionUID = 3397086764130547540L;
	private String cateId;// 分类ID
	private String cateName;// 分类名称
	private String pId;// 父分类ID
	private String pName;// 父分类名称
	private int level;// 级别

	public String getCateId() {
		return cateId;
	}

	public void setCateId(String cateId) {
		this.cateId = cateId;
	}

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}


}
