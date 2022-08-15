package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.decisiontree.ped.model.AffectedStatus;
import org.molgenis.vcf.decisiontree.ped.model.Sex;

@Value
@Builder
public class SampleContext {
  @NonNull String id;
  @NonFinal
  @Setter
  @Default
  Integer index = -1;
  @NonNull Sex sex;
  @NonNull AffectedStatus affectedStatus;
  String father;
  String mother;
  String family;
  @NonNull Boolean proband;
  @NonNull List<String> phenotypes;
}
