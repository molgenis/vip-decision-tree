package org.molgenis.vcf.decisiontree.runner.info;

import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class VcfNestedMetadata {
  @NonNull Map<String, NestedInfoHeaderLine> nestedLines;

  public NestedInfoHeaderLine getNestedInfoHeaderLine(String id) {
    return nestedLines.get(id);
  }
}
