package org.wltea.analyzer.core;

public final class PinyinTokensHolder {
	
   	private static ThreadLocal<String> pinyins = new ThreadLocal<String>() {
   		@Override
   		public String initialValue() {
   			return "";
   		}
   	};
   	
   	private static ThreadLocal<IKSegmenter> ikSegmenter = new ThreadLocal<IKSegmenter>();
   	
   	public String getPinyins() {
   		return pinyins.get();
   	}
   	
   	public void setPinyins(String pinyinTokens) {
   		pinyins.set(pinyinTokens);
   	}
   	
   	public IKSegmenter getIkSegmenter() {
   		return ikSegmenter.get();
   	}
   	
   	public void setIkSegmenter(IKSegmenter iksg) {
   		ikSegmenter.set(iksg);
   	}

}
