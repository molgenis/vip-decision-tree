package org.molgenis.vcf.decisiontree;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

@Value
@Builder
public class Settings {
  Path inputVcfPath;
  DecisionTree decisionTree;
  AppSettings appSettings;
  WriterSettings writerSettings;
}
