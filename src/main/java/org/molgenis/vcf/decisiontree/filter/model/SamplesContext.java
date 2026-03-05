package org.molgenis.vcf.decisiontree.filter.model;

import java.util.Set;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SamplesContext {
  Set<SampleContext> sampleContexts;
}
