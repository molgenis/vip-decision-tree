package org.molgenis.vcf.decisiontree.loader.model;

public interface ConfigNode {
  enum Type {
    BOOL,
    CATEGORICAL,
    EXISTS,
    LEAF
  }

  Type getType();

  String getDescription();
}
