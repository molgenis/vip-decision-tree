package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotatorImpl;

public class ConsequenceAnnotatorFactory {

  private ConsequenceAnnotatorFactory() {
  }

  public static ConsequenceAnnotator create(Settings settings) {
    return new ConsequenceAnnotatorImpl(settings.getWriterSettings().isWriteLabels(),
        settings.getWriterSettings().isWritePath());
  }
}
