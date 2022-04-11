package org.molgenis.vcf.decisiontree.runner.info;

import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

@Value
@Builder
public class VepHeaderLine {

  @NonNull Field parentField;
  @NonNull Map<String, NestedField> nestedFields;

  public Field getField(String id) {
    Field field = nestedFields.get(id);
    if (field == null) {
      field = new MissingField(id);
    }
    return field;
  }

  public boolean hasField(String id) {
    return nestedFields.containsKey(id);
  }
}
