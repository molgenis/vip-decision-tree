package org.molgenis.vcf.decisiontree.filter.model;

public interface Field {

  String getId();

  FieldType getFieldType();

  ValueType getValueType();

  ValueCount getValueCount();

  Integer getCount();

  Character getSeparator();
}
