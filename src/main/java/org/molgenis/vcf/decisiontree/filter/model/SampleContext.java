package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.utils.sample.model.AffectedStatus;
import org.molgenis.vcf.utils.sample.model.Sex;

@Value
@Builder
public class SampleContext {

  String id;
  @NonFinal @Setter Integer index;
  Sex sex;
  AffectedStatus affectedStatus;
  @Nullable String fatherId;
  @Nullable String motherId;
  @Nullable String familyId;
  Boolean proband;
  List<String> phenotypes;
}
