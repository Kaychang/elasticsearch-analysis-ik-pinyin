package org.elasticsearch.index.analysis;


public class IkAnalysisBinderProcessorP extends AnalysisModule.AnalysisBinderProcessor {

    @Override public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {

    }


    @Override public void processAnalyzers(AnalyzersBindings analyzersBindings) {
        analyzersBindings.processAnalyzer("ikpinyin", IkAnalyzerProviderP.class);
        super.processAnalyzers(analyzersBindings);
    }


    @Override
    public void processTokenizers(TokenizersBindings tokenizersBindings) {
      tokenizersBindings.processTokenizer("ikpinyin", IkTokenizerFactoryP.class);
      super.processTokenizers(tokenizersBindings);
    }
}
