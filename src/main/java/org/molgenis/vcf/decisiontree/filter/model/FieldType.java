package org.molgenis.vcf.decisiontree.filter.model;

public enum FieldType {
  COMMON,
  INFO,
  /**
   * INFO field with nested information (VEP CSQ)
   */
  INFO_VEP,
  FORMAT,
  /**
   * FORMAT field with nested information (GENOTYPE info from htsjdk)
   */
  FORMAT_GENOTYPE,
  SAMPLE
}
