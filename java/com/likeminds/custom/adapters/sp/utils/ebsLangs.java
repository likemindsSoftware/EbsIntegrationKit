package com.likeminds.custom.adapters.sp.utils;

import java.util.List;

import oracle.apps.fnd.common.LangInfo;

public class ebsLangs {

	List<LangInfo> langsInstalled = null;

	private static ebsLangs instance;

	public synchronized static ebsLangs getInstance() {
		if (instance == null) {
			instance = new ebsLangs();
		}
		return instance;
	}

	public void setLangs(List<LangInfo> list) {
		langsInstalled = list;
	}

	public List<LangInfo> getLangs() {
		return langsInstalled;
	}

}
