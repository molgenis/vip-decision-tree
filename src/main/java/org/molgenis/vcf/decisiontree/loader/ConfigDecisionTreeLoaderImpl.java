package org.molgenis.vcf.decisiontree.loader;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.springframework.stereotype.Component;

@Component
public class ConfigDecisionTreeLoaderImpl implements ConfigDecisionTreeLoader {
  private final ConfigDecisionTreeValidator configDecisionTreeValidator;

  ConfigDecisionTreeLoaderImpl(ConfigDecisionTreeValidator configDecisionTreeValidator) {
    this.configDecisionTreeValidator = requireNonNull(configDecisionTreeValidator);
  }

  public ConfigDecisionTree load(Path decisionTreeConfigPath) {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ConfigNode.class, new ConfigNodeDeserializer());
    module.addDeserializer(Path.class, new PathDeserializer(decisionTreeConfigPath));
    mapper.registerModule(module);

    ConfigDecisionTree configDecisionTree;
    try {
      configDecisionTree =
          mapper.readValue(decisionTreeConfigPath.toFile(), ConfigDecisionTree.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    configDecisionTreeValidator.validate(configDecisionTree);

    return configDecisionTree;
  }
}
