package org.wltea.analyzer.core;

public final class PinyinTokensHolder {
	
   	private static ThreadLocal<BeginMapping> beginmapping = new ThreadLocal<BeginMapping>() {
   		@Override
   		public BeginMapping initialValue() {
   			BeginMapping beginMapping = new BeginMapping();
   			return beginMapping;
   		}
   	};
   	
   	public BeginMapping getBeginmapping() {
   		return beginmapping.get();
   	}
   	
   	public void setBeginmapping(BeginMapping mapping) {
   		beginmapping.set(mapping);
   	}

}
