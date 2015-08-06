package org.elasticsearch.index.analysis;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import org.wltea.analyzer.cfg.ConfigurationP;
import org.wltea.analyzer.dic.DictionaryP;
import org.wltea.analyzer.lucene.IKAnalyzerP;

public class IkAnalyzerProviderP extends AbstractIndexAnalyzerProvider<IKAnalyzerP> {
    private final IKAnalyzerP analyzer;

    @Inject
    public IkAnalyzerProviderP(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        DictionaryP.initial(new ConfigurationP(env));
        analyzer=new IKAnalyzerP(indexSettings, settings, env);
    }

    @Override public IKAnalyzerP get() {
        return this.analyzer;
    }
}
