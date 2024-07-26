package org.molgenis.vcf.decisiontree.loader.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConfigOperator {
  @JsonProperty("==")
  EQUALS,
  @JsonProperty("!=")
  NOT_EQUALS,
  @JsonProperty("eq_seq")
  EQUALS_SEQUENCE,
  @JsonProperty("!eq_seq")
  NOT_EQUALS_SEQUENCE,
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
  NOT_IN,
  @JsonProperty("contains")
  CONTAINS,
  @JsonProperty("!contains")
  NOT_CONTAINS,
  @JsonProperty("contains_any")
  CONTAINS_ANY,
  @JsonProperty("contains_all")
  CONTAINS_ALL,
  @JsonProperty("range_overlaps")
  RANGE_OVERLAPS,
  @JsonProperty("range_below")
  RANGE_BELOW,
  @JsonProperty("range_above")
  RANGE_ABOVE, @JsonProperty("contains_none")
  CONTAINS_NONE
}
