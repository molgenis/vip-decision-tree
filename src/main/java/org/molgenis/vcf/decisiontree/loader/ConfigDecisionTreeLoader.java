package org.molgenis.vcf.decisiontree.loader;

import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;

public interface ConfigDecisionTreeLoader {
  ConfigDecisionTree load(Path decisionTreeConfigPath);
}
