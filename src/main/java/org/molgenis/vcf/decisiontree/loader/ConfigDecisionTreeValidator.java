package org.molgenis.vcf.decisiontree.loader;

import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;

public interface ConfigDecisionTreeValidator {

  /**
   * validate config decision tree.
   *
   * @throws RuntimeException if tree is invalid
   */
  void validate(ConfigDecisionTree configDecisionTree);
}
