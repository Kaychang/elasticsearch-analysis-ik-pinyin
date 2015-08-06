# IK Analysis for ElasticSearch Along with Pinyin

基于 [elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik) 扩展了拼音分词的功能，可使ik分词后所对应的汉语拼音被index到ES中。


## 基本说明请参考以下工程
[elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik)


## 使用方法
1. git clone 本工程。
2. 进入工程根目录下，执行 mvn clean package。
3. 将打包生成的 target/releases/elasticsearch-analysis-ik-pinyin-${version}.zip文件解压，
   把里面的jar文件拷贝至 ${ELASTICSEARCH_HOME}/plugins/analysis-ik-pinyin 下。
   _ 注意：如果已经配置了analysis-ik插件，则不需要拷贝commons-*.jar和http*.jar，因为对于同一个类Class Loader只会加载一次。 _
4. 参考 [elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik) 
   配置好 ${ELASTICSEARCH_HOME}/config/ik 。
5. 在 elasticsearch.yml 中添加配置：

### elasticsearch.yml
```yml
index:
  analysis:
    analyzer:
      ikpinyin:
        type: org.elasticsearch.index.analysis.IkAnalyzerProviderP
```

## 拼音分词效果
元文本：
> 将打包生成的target/releases

分词后的token：
> dabao shengcheng target releases
