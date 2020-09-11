package org.molgenis.vcf.decisiontree.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;

@ExtendWith(MockitoExtension.class)
class DecisionTreeLoaderImplTest {
  @Mock private ConfigDecisionTreeLoader configDecisionTreeLoader;
  @Mock private DecisionTreeMapper decisionTreeMapper;
  private DecisionTreeLoaderImpl decisionTreeLoader;

  @BeforeEach
  void setUp() {
    decisionTreeLoader = new DecisionTreeLoaderImpl(configDecisionTreeLoader, decisionTreeMapper);
  }

  @Test
  void load() {
    Path treePath = Paths.get("src", "test", "resources", "tree_all_types.json");
    ConfigDecisionTree configDecisionTree = mock(ConfigDecisionTree.class);
    when(configDecisionTreeLoader.load(treePath)).thenReturn(configDecisionTree);
    DecisionTree decisionTree = mock(DecisionTree.class);
    when(decisionTreeMapper.map(configDecisionTree)).thenReturn(decisionTree);
    assertEquals(decisionTree, decisionTreeLoader.load(treePath));
  }
}
