package org.molgenis.vcf.decisiontree.filter.sample;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class SampleAnnotatorImpl implements SampleAnnotator {

  public static final String VISD = "VISD";

  @Override
  public VariantContext annotate(String decision, String sampleName, VariantContext vc) {
    VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
    GenotypesContext genotypeContext = GenotypesContext.copy(vc.getGenotypes());
    Genotype genotype = genotypeContext.get(sampleName);
    GenotypeBuilder gtBuilder = new GenotypeBuilder(genotype);
    gtBuilder.attribute(VISD, decision);
    genotypeContext.replace(gtBuilder.make());
    vcBuilder.genotypes(genotypeContext);
    return vcBuilder.make();
  }
}
