package org.molgenis.vcf.decisiontree;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.SampleMeta;

@Value
@Builder
public class SampleInfo {

  List<String> probands;
  @Builder.Default
  List<String> samplePhenotypes = Collections.emptyList();
  Map<String, SampleMeta> sampleMetaMap;
}
