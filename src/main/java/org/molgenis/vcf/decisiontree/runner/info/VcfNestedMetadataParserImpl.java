package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class VcfNestedMetadataParserImpl implements VcfNestedMetadataParser {

  private final List<NestedMetadataMapper> mappers;

  public VcfNestedMetadataParserImpl(
      List<NestedMetadataMapper> mappers) {
    this.mappers = requireNonNull(mappers);
  }

  @Override
  public VcfNestedMetadata map(VCFHeader header) {
    Map<String, NestedInfoHeaderLine> nestedMetadata = new HashMap<>();
    for(VCFInfoHeaderLine headerLine : header.getInfoHeaderLines()){
      for(NestedMetadataMapper mapper : mappers) {
        if (mapper.canMap(headerLine)) {
          String id = headerLine.getID();
          nestedMetadata.put(id, mapper.map(headerLine));
        }
      }
    }
    return VcfNestedMetadata.builder().nestedLines(nestedMetadata).build();
  }

}
