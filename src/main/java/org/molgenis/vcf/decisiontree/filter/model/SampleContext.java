package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.utils.sample.model.AffectedStatus;
import org.molgenis.vcf.utils.sample.model.Sex;

@Value
@Builder
public class SampleContext {

  @NonNull String id;
  @NonFinal
  @Setter
  Integer index;
  @NonNull Sex sex;
  @NonNull AffectedStatus affectedStatus;
  String fatherId;
  String motherId;
  String familyId;
  @NonNull Boolean proband;
  @NonNull List<String> phenotypes;
}
