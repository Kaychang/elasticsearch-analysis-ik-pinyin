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
 * 
 */
package org.wltea.analyzer.core;

import java.util.Stack;
import java.util.TreeSet;

/**
 * IK分词歧义裁决器
 */
class IKArbitratorP {

	IKArbitratorP(){
		
	}
	
	/**
	 * 分词歧义处理
//	 * @param orgLexemes
	 * @param useSmart
	 */
	void process(AnalyzeContextP context , boolean useSmart){
		QuickSortSetP orgLexemes = context.getOrgLexemes();
		LexemeP orgLexeme = orgLexemes.pollFirst();
		
		LexemePathP crossPath = new LexemePathP();
		while(orgLexeme != null){
			if(!crossPath.addCrossLexeme(orgLexeme)){
				//找到与crossPath不相交的下一个crossPath	
				if(crossPath.size() == 1 || !useSmart){
					//crossPath没有歧义 或者 不做歧义处理
					//直接输出当前crossPath
					context.addLexemePath(crossPath);
				}else{
					//对当前的crossPath进行歧义处理
					QuickSortSetP.Cell headCell = crossPath.getHead();
					LexemePathP judgeResult = this.judge(headCell, crossPath.getPathLength());
					//输出歧义处理结果judgeResult
					context.addLexemePath(judgeResult);
				}
				
				//把orgLexeme加入新的crossPath中
				crossPath = new LexemePathP();
				crossPath.addCrossLexeme(orgLexeme);
			}
			orgLexeme = orgLexemes.pollFirst();
		}
		
		
		//处理最后的path
		if(crossPath.size() == 1 || !useSmart){
			//crossPath没有歧义 或者 不做歧义处理
			//直接输出当前crossPath
			context.addLexemePath(crossPath);
		}else{
			//对当前的crossPath进行歧义处理
			QuickSortSetP.Cell headCell = crossPath.getHead();
			LexemePathP judgeResult = this.judge(headCell, crossPath.getPathLength());
			//输出歧义处理结果judgeResult
			context.addLexemePath(judgeResult);
		}
	}
	
	/**
	 * 歧义识别
	 * @param lexemeCell 歧义路径链表头
	 * @param fullTextLength 歧义路径文本长度
	 * @return
	 */
	private LexemePathP judge(QuickSortSetP.Cell lexemeCell , int fullTextLength){
		//候选路径集合
		TreeSet<LexemePathP> pathOptions = new TreeSet<LexemePathP>();
		//候选结果路径
		LexemePathP option = new LexemePathP();
		
		//对crossPath进行一次遍历,同时返回本次遍历中有冲突的Lexeme栈
		Stack<QuickSortSetP.Cell> lexemeStack = this.forwardPath(lexemeCell , option);
		
		//当前词元链并非最理想的，加入候选路径集合
		pathOptions.add(option.copy());
		
		//存在歧义词，处理
		QuickSortSetP.Cell c = null;
		while(!lexemeStack.isEmpty()){
			c = lexemeStack.pop();
			//回滚词元链
			this.backPath(c.getLexeme() , option);
			//从歧义词位置开始，递归，生成可选方案
			this.forwardPath(c , option);
			pathOptions.add(option.copy());
		}
		
		//返回集合中的最优方案
		return pathOptions.first();

	}
	
	/**
	 * 向前遍历，添加词元，构造一个无歧义词元组合
//	 * @param LexemePath path
	 * @return
	 */
	private Stack<QuickSortSetP.Cell> forwardPath(QuickSortSetP.Cell lexemeCell , LexemePathP option){
		//发生冲突的Lexeme栈
		Stack<QuickSortSetP.Cell> conflictStack = new Stack<QuickSortSetP.Cell>();
		QuickSortSetP.Cell c = lexemeCell;
		//迭代遍历Lexeme链表
		while(c != null && c.getLexeme() != null){
			if(!option.addNotCrossLexeme(c.getLexeme())){
				//词元交叉，添加失败则加入lexemeStack栈
				conflictStack.push(c);
			}
			c = c.getNext();
		}
		return conflictStack;
	}
	
	/**
	 * 回滚词元链，直到它能够接受指定的词元
//	 * @param lexeme
	 * @param l
	 */
	private void backPath(LexemeP l  , LexemePathP option){
		while(option.checkCross(l)){
			option.removeTail();
		}
		
	}
	
}
