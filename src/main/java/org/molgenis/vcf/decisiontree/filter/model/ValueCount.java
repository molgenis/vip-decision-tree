package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class ValueCount {

  public enum Type {
    A,
    R,
    G,
    VARIABLE,
    FIXED
  }

  @NonNull
  Type type;
  /**
   * Returns count for FIXED values, otherwise null
   */
  Integer count;
  /**
   * True if null value (list item) is allowed
   */
  boolean nullable;
}
