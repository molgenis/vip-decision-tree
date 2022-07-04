package org.molgenis.vcf.decisiontree;

import lombok.NonNull;
import lombok.Value;

@Value
public class SamplePhenotype {

  @NonNull PhenotypeMode mode;

  String subjectId;

  @NonNull String[] phenotypes;
}
