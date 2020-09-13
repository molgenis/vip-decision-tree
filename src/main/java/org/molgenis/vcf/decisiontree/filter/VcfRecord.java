package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * {@link VariantContext} wrapper that works with nested data (e.g. CSQ INFO fields)..
 */
public class VcfRecord {

  private final VariantContext variantContext;

  public VcfRecord(VariantContext variantContext) {
    this.variantContext = requireNonNull(variantContext);
  }

  public int getNrAltAllelles() {
    return variantContext.getNAlleles() - 1;
  }

  public VariantContext unwrap() {
    return variantContext;
  }
}
