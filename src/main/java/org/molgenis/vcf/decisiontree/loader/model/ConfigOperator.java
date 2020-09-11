package org.molgenis.vcf.decisiontree.loader.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConfigOperator {
  @JsonProperty("==")
  EQUALS,
  @JsonProperty("!=")
  NOT_EQUALS,
  @JsonProperty("<")
  LESS,
  @JsonProperty("<=")
  LESS_OR_EQUAL,
  @JsonProperty(">")
  GREATER,
  @JsonProperty(">=")
  GREATER_OR_EQUAL,
  @JsonProperty("in")
  IN,
  @JsonProperty("!in")
  NOT_IN
}
