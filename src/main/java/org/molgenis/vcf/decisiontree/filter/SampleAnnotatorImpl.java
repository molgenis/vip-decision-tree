package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class SampleAnnotatorImpl implements SampleAnnotator {

  public static final String VIPC_S = "VIPC_S";
  public static final String VIPP_S = "VIPP_S";
  public static final String VIPL_S = "VIPL_S";
  private final boolean writeLabels;
  private final boolean writePaths;

  public SampleAnnotatorImpl(boolean writeLabels, boolean writePaths) {
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
  }

  @Override
  public void annotate(
          Integer sampleIndex,
      VariantContextBuilder vcBuilder) {
    GenotypesContext genotypeContext = GenotypesContext.copy(vcBuilder.getGenotypes());

    vcBuilder.genotypes(genotypeContext);
  }
}
