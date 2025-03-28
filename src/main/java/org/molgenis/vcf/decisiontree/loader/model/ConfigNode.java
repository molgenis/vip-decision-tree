package org.molgenis.vcf.decisiontree.loader.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public interface ConfigNode {

  enum Type {
    BOOL,
    BOOL_MULTI,
    CATEGORICAL,
    EXISTS,
    LEAF
  }

  @JsonPropertyDescription
  Type getType();

  @JsonPropertyDescription
  String getDescription();

  @JsonPropertyDescription
  String getLabel();
}
