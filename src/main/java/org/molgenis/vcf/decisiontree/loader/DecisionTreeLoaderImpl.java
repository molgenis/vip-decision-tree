package org.molgenis.vcf.decisiontree.loader;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.springframework.stereotype.Component;

@Component
public class DecisionTreeLoaderImpl implements DecisionTreeLoader {

  private final ConfigDecisionTreeLoader configDecisionTreeLoader;
  private final DecisionTreeMapper decisionTreeMapper;

  DecisionTreeLoaderImpl(
      ConfigDecisionTreeLoader configDecisionTreeLoader, DecisionTreeMapper decisionTreeMapper) {
    this.configDecisionTreeLoader = requireNonNull(configDecisionTreeLoader);
    this.decisionTreeMapper = requireNonNull(decisionTreeMapper);
  }

  @Override
  public DecisionTree load(Path decisionTreeConfigPath) {
    ConfigDecisionTree configDecisionTree = configDecisionTreeLoader.load(decisionTreeConfigPath);
    return decisionTreeMapper.map(configDecisionTree);
  }
}
