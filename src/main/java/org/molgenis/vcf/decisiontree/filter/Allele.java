package org.molgenis.vcf.decisiontree.filter;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class Allele {

  String bases;
  int index;
}
