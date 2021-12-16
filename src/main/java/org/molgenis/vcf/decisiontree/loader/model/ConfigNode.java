package org.molgenis.vcf.decisiontree.loader.model;

public interface ConfigNode {
  enum Type {
    BOOL,
    BOOL_MULTI,
    CATEGORICAL,
    EXISTS,
    LEAF
  }

  Type getType();

  String getDescription();
}
