package org.molgenis.vcf.decisiontree.filter.model;

public enum FieldType {
  COMMON,
  INFO,
  /**
   * INFO field with nested information (e.g. VEP CSQ or SnpEff ANN)
   */
  INFO_NESTED,
  FORMAT
}
