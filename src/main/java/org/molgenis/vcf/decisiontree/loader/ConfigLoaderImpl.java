package org.molgenis.vcf.decisiontree.loader;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.loader.model.Config;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.springframework.stereotype.Component;

@Component
public class ConfigLoaderImpl implements ConfigLoader {
  private final ConfigDecisionTreeValidator configDecisionTreeValidator;

  ConfigLoaderImpl(ConfigDecisionTreeValidator configDecisionTreeValidator) {
    this.configDecisionTreeValidator = requireNonNull(configDecisionTreeValidator);
  }

  public Config load(Path decisionTreeConfigPath) {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(ConfigNode.class, new ConfigNodeDeserializer());
    mapper.registerModule(module);

    Config config;
    try {
      config =
          mapper.readValue(decisionTreeConfigPath.toFile(), Config.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    configDecisionTreeValidator.validate(config.getDecisionTree());

    return config;
  }
}
