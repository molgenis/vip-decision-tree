package org.molgenis.vcf.decisiontree.filter.model;

import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SamplesContext {

  @NonNull Set<SampleContext> sampleContexts;
}
