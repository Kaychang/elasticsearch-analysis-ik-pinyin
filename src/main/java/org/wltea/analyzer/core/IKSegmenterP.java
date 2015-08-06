/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 */
package org.wltea.analyzer.core;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.wltea.analyzer.cfg.ConfigurationP;
import org.wltea.analyzer.dic.DictionaryP;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IK分词器主类
 *
 */
public final class IKSegmenterP {
	
	//字符窜reader
	private Reader input;
	//分词器配置项
	private ConfigurationP cfg;
	//分词器上下文
	private AnalyzeContextP context;
	//分词处理器列表
	private List<ISegmenterP> segmenters;
	//分词歧义裁决器
	private IKArbitratorP arbitrator;
    private  boolean useSmart = false;
	

    /**
	 * IK分词器构造函数
	 * @param input
     */
	public IKSegmenterP(Reader input , Settings settings, Environment environment){
		this.input = input;
		this.cfg = new ConfigurationP(environment);
        this.useSmart = settings.get("use_smart", "false").equals("true");
        this.init();
	}
	
	public IKSegmenterP(Reader input){
		new IKSegmenterP(input, null,null);
	}
	
//	/**
//	 * IK分词器构造函数
//	 * @param input
//	 * @param cfg 使用自定义的Configuration构造分词器
//	 *
//	 */
//	public IKSegmenter(Reader input , Configuration cfg){
//		this.input = input;
//		this.cfg = cfg;
//		this.init();
//	}
	
	/**
	 * 初始化
	 */
	private void init(){
		//初始化词典单例
		DictionaryP.initial(this.cfg);
		//初始化分词上下文
		this.context = new AnalyzeContextP(useSmart);
		//加载子分词器
		this.segmenters = this.loadSegmenters();
		//加载歧义裁决器
		this.arbitrator = new IKArbitratorP();
	}
	
	/**
	 * 初始化词典，加载子分词器实现
	 * @return List<ISegmenter>
	 */
	private List<ISegmenterP> loadSegmenters(){
		List<ISegmenterP> segmenters = new ArrayList<ISegmenterP>(4);
		//处理字母的子分词器
		segmenters.add(new LetterSegmenterP()); 
		//处理中文数量词的子分词器
		segmenters.add(new CN_QuantifierSegmenterP());
		//处理中文词的子分词器
		segmenters.add(new CJKSegmenterP());
		return segmenters;
	}
	
	/**
	 * 分词，获取下一个词元
	 * @return Lexeme 词元对象
	 * @throws java.io.IOException
	 */
	public synchronized LexemeP next()throws IOException{
		LexemeP l = null;
		while((l = context.getNextLexeme()) == null ){
			/*
			 * 从reader中读取数据，填充buffer
			 * 如果reader是分次读入buffer的，那么buffer要  进行移位处理
			 * 移位处理上次读入的但未处理的数据
			 */
			int available = context.fillBuffer(this.input);
			if(available <= 0){
				//reader已经读完
				context.reset();
				return null;
				
			}else{
				//初始化指针
				context.initCursor();
				do{
        			//遍历子分词器
        			for(ISegmenterP segmenter : segmenters){
        				segmenter.analyze(context);
        			}
        			//字符缓冲区接近读完，需要读入新的字符
        			if(context.needRefillBuffer()){
        				break;
        			}
   				//向前移动指针
				}while(context.moveCursor());
				//重置子分词器，为下轮循环进行初始化
				for(ISegmenterP segmenter : segmenters){
					segmenter.reset();
				}
			}
			//对分词进行歧义处理
			this.arbitrator.process(context, useSmart);
			//将分词结果输出到结果集，并处理未切分的单个CJK字符
			context.outputToResult();
			//记录本次分词的缓冲区位移
			context.markBufferOffset();			
		}
		
		// 设置拼音token
		if (l != null) {
			String pinyin = PinyinHelper.convertToPinyinString(l.getLexemeText(),
					"", PinyinFormat.WITHOUT_TONE);
			
			PinyinTokensHolder holder = new PinyinTokensHolder();
			BeginMapping bm = holder.getBeginmapping();
			Map<Integer, Integer> map = bm.getMapping();
			Integer begin = l.getBegin();
			if (!map.containsKey(begin)) {
				int nextPinyinBegin = bm.getNextPinyinBegin();
				map.put(begin, nextPinyinBegin);
				bm.setMapping(map);
				bm.setNextPinyinBegin(nextPinyinBegin + pinyin.length());
				holder.setBeginmapping(bm);
			}
			
			l.setBegin(map.get(begin));
			l.setLength(pinyin.length());
			l.setLexemeText(pinyin);
			l.setLexemeType(LexemeP.TYPE_ENGLISH);
		}
		
		return l;
	}

	/**
     * 重置分词器到初始状态
     * @param input
     */
	public synchronized void reset(Reader input) {
		this.input = input;
		context.reset();
		for(ISegmenterP segmenter : segmenters){
			segmenter.reset();
		}
	}
}
