package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionClass;
import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionLabelsString;
import static org.molgenis.vcf.decisiontree.filter.DecisionUtils.getDecisionsPath;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.List;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.model.Decision;

public class SampleAnnotatorImpl implements SampleAnnotator {

  public static final String VISD = "VISD";
  public static final String VISDP = "VISDP";
  public static final String VISDL = "VISDL";
  private final boolean writeLabels;
  private final boolean writePaths;

  public SampleAnnotatorImpl(boolean writeLabels, boolean writePaths) {
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
  }

  @Override
  public VariantContext annotate(List<Decision> decisions, String sampleName, VariantContext vc) {
    VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
    GenotypesContext genotypeContext = GenotypesContext.copy(vc.getGenotypes());
    Genotype genotype = genotypeContext.get(sampleName);
    GenotypeBuilder gtBuilder = new GenotypeBuilder(genotype);
    gtBuilder.attribute(VISD,
        decisions.stream().map(DecisionUtils::getDecisionClass).toList());
    if (writePaths) {
      gtBuilder.attribute(VISDP,
          decisions.stream().map(DecisionUtils::getDecisionsPath).toList());
    }
    if (writeLabels) {
      gtBuilder.attribute(VISDL,
          decisions.stream().map(DecisionUtils::getDecisionLabelsString).toList());
    }
    genotypeContext.replace(gtBuilder.make());
    vcBuilder.genotypes(genotypeContext);
    return vcBuilder.make();
  }
}
