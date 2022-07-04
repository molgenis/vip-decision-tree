package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.Decision;

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
  public VariantContext annotate(List<Decision> decisions, Integer sampleIndex, VariantContext vc) {
    VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
    GenotypesContext genotypeContext = GenotypesContext.copy(vc.getGenotypes());
    Genotype genotype = genotypeContext.get(sampleIndex);
    GenotypeBuilder gtBuilder = new GenotypeBuilder(genotype);
    gtBuilder.attribute(VIPC_S,
        decisions.stream().map(DecisionUtils::getDecisionClass).toList());
    if (writePaths) {
      gtBuilder.attribute(VIPP_S,
          decisions.stream().map(DecisionUtils::getDecisionsPath).toList());
    }
    if (writeLabels) {
      gtBuilder.attribute(VIPL_S,
          decisions.stream().map(DecisionUtils::getDecisionLabelsString).toList());
    }
    genotypeContext.replace(gtBuilder.make());
    vcBuilder.genotypes(genotypeContext);
    return vcBuilder.make();
  }
}
