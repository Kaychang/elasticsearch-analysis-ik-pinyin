package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import org.wltea.analyzer.cfg.ConfigurationP;
import org.wltea.analyzer.dic.DictionaryP;
import org.wltea.analyzer.lucene.IKTokenizerP;

import java.io.Reader;

public class IkTokenizerFactoryP extends AbstractTokenizerFactory {
  private Environment environment;
  private Settings settings;

  @Inject
  public IkTokenizerFactoryP(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
	  super(index, indexSettings, name, settings);
	  this.environment = env;
	  this.settings = settings;
	  DictionaryP.initial(new ConfigurationP(env));
  }

  @Override
  public Tokenizer create(Reader reader) {
	  return new IKTokenizerP(reader, settings, environment);
  }

}
