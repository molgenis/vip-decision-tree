package org.molgenis.vcf.decisiontree.filter.model;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SampleMeta {

  @Builder.Default
  List<String> samplePhenotypes = Collections.emptyList();
  String sampleName;
  @Builder.Default
  boolean proband = true;
}
