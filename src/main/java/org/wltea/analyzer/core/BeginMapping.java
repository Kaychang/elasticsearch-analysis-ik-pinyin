package org.wltea.analyzer.core;

import java.util.HashMap;
import java.util.Map;

public class BeginMapping {

	private Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
	private int nextPinyinBegin = 0;

	public Map<Integer, Integer> getMapping() {
		return mapping;
	}

	public void setMapping(Map<Integer, Integer> mapping) {
		this.mapping = mapping;
	}

	public int getNextPinyinBegin() {
		return nextPinyinBegin;
	}

	public void setNextPinyinBegin(int nextPinyinBegin) {
		this.nextPinyinBegin = nextPinyinBegin;
	}

}
