package org.molgenis.vcf.decisiontree.runner.info;

import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

@Value
@Builder
public class NestedInfoHeaderLine {
  @NonNull Map<String, NestedField> nestedFields;

  public NestedField getField(String id){
    return nestedFields.get(id);
  }

  public boolean hasField(String id) {
    return nestedFields.containsKey(id);
  }
}
