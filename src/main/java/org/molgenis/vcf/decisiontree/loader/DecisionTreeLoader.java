package org.molgenis.vcf.decisiontree.loader;

import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

public interface DecisionTreeLoader {
  DecisionTree load(Path decisionTreeConfigPath);
}
