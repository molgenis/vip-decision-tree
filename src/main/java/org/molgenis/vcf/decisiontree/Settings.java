package org.molgenis.vcf.decisiontree;

import java.nio.file.Path;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNestedMetadata;

@Value
@Builder
public class Settings {
  @NonNull Path inputVcfPath;
  @NonNull ConfigDecisionTree configDecisionTree;
  @NonNull Map<String,ConfigNestedMetadata> configNestedMetadata;
  @NonNull AppSettings appSettings;
  @NonNull WriterSettings writerSettings;
}
