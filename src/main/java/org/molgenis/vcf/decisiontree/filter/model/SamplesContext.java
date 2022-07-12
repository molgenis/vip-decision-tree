package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SamplesContext {

  @NonNull List<SampleContext> sampleContexts;
}
