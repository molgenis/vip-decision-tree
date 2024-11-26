package org.molgenis.vcf.decisiontree.filter.model;

import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

public interface Field {

  String getId();

  FieldType getFieldType();

  ValueType getValueType();

  ValueCount getValueCount();

  Integer getCount();

  Character getSeparator();
}
