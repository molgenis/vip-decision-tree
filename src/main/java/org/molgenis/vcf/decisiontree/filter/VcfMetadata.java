package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFHeader;

/**
 * {@link VCFHeader} wrapper that works with nested metadata (e.g. CSQ INFO fields).
 */
public class VcfMetadata {

  private final VCFHeader vcfHeader;

  public VcfMetadata(VCFHeader vcfHeader) {
    this.vcfHeader = requireNonNull(vcfHeader);
  }

  public VCFHeader unwrap() {
    return vcfHeader;
  }
}
