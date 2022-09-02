package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.springframework.stereotype.Component;

@Component
public class VepMetadataParserImpl implements VepMetadataParser {

  private final VepMetadataMapper mapper;

  public VepMetadataParserImpl(VepMetadataMapper mapper) {
    this.mapper = requireNonNull(mapper);
  }

  @Override
  public NestedHeaderLine map(VCFHeader header) {
    for (VCFInfoHeaderLine headerLine : header.getInfoHeaderLines()) {
      if (mapper.canMap(headerLine)) {
        return mapper.map(headerLine);
      }
    }
    throw new MissingVepException();
  }

}
