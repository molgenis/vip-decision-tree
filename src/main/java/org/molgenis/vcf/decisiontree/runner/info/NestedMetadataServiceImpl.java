package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.springframework.stereotype.Component;

@Component
public class NestedMetadataServiceImpl implements NestedMetadataService {

  private final List<NestedMetadataMapper> mappers;

  public NestedMetadataServiceImpl(
      List<NestedMetadataMapper> mappers) {
    this.mappers = mappers;
  }

  @Override
  public Map<String, Map<String, NestedField>> map(VCFHeader header) {
    Map<String, Map<String, NestedField>> nestedMetadata = new HashMap<>();
    for(VCFInfoHeaderLine headerLine : header.getInfoHeaderLines()){
      for(NestedMetadataMapper mapper : mappers) {
        if (mapper.canMap(headerLine)) {
          String id = headerLine.getID();
          nestedMetadata.put(id, mapper.map(headerLine));
        }
      }
    }
    return nestedMetadata;
  }

}
