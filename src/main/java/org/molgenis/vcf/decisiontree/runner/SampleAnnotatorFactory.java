package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.filter.sample.SampleAnnotator;
import org.molgenis.vcf.decisiontree.filter.sample.SampleAnnotatorImpl;

public class SampleAnnotatorFactory {

  private SampleAnnotatorFactory() {
  }

  public static SampleAnnotator create() {
    return new SampleAnnotatorImpl();
  }
}
