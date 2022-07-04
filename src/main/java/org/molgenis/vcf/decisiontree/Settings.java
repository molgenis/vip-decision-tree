package org.molgenis.vcf.decisiontree;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;

@Value
@Builder
public class Settings {

  Mode mode;
  Path inputVcfPath;
  ConfigDecisionTree configDecisionTree;
  AppSettings appSettings;
  WriterSettings writerSettings;
  SampleInfo sampleInfo;
  boolean strict;
}
