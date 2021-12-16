package org.molgenis.vcf.decisiontree.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;

@ExtendWith(MockitoExtension.class)
class ConfigDecisionTreeLoaderImplTest {
  private ConfigDecisionTreeLoaderImpl configDecisionTreeLoader;
  @Mock private ConfigDecisionTreeValidator configDecisionTreeValidator;

  @BeforeEach
  void setUp() {
    configDecisionTreeLoader = new ConfigDecisionTreeLoaderImpl(configDecisionTreeValidator);
  }

  @Test
  void load() {
    Path treePath = Paths.get("src", "test", "resources", "tree_all_types.json");
    ConfigDecisionTree configDecisionTree = configDecisionTreeLoader.load(treePath);

    verify(configDecisionTreeValidator).validate(configDecisionTree);
    Assertions.assertAll(
        () -> assertEquals("my_score", configDecisionTree.getRootNode()),
        () -> assertEquals(5, configDecisionTree.getNodes().size()),
        () -> assertEquals(1, configDecisionTree.getLabels().size()));
  }
}
