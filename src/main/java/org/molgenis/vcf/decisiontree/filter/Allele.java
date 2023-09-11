package org.molgenis.vcf.decisiontree.filter;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class Allele {

  @NonNull String bases;
  int index;
}
