package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.SampleAnnotator;
import org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl;

public class SampleAnnotatorFactory {

  private SampleAnnotatorFactory() {
  }

  public static SampleAnnotator create(Settings settings) {
    return new SampleAnnotatorImpl(settings.getWriterSettings().isWriteLabels(),
        settings.getWriterSettings().isWritePath());
  }
}
