package org.molgenis.vcf.decisiontree.loader.model;

public interface ConfigNode {
  enum Type {
    BOOL,
    CATEGORICAL,
    LEAF
  }

  Type getType();

  String getDescription();
}
