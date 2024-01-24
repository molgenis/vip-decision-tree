package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import static java.util.Objects.requireNonNull;

public class VepMetadataParserImpl implements VepMetadataParser {

  private final VepMetadataMapper vepMetadataMapper;

  public VepMetadataParserImpl(VepMetadataMapper vepMetadataMapper) {
    this.vepMetadataMapper = requireNonNull(vepMetadataMapper);
  }

  @Override
  public NestedHeaderLine map(VCFHeader header) {
    for (VCFInfoHeaderLine headerLine : header.getInfoHeaderLines()) {
      if (vepMetadataMapper.canMap(headerLine)) {
        return vepMetadataMapper.map(headerLine);
      }
    }
    throw new MissingVepException();
  }

}
