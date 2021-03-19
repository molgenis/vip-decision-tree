package org.molgenis.vcf.decisiontree;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_CONFIG;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_INPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_LABELS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_OUTPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PATH;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_STRICT;

import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeLoader;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class AppCommandLineToSettingsMapper {

  private final String appName;
  private final String appVersion;
  private final ConfigDecisionTreeLoader configDecisionTreeLoader;

  AppCommandLineToSettingsMapper(
      @Value("${app.name}") String appName,
      @Value("${app.version}") String appVersion,
      ConfigDecisionTreeLoader configDecisionTreeLoader) {
    this.appName = appName;
    this.appVersion = appVersion;
    this.configDecisionTreeLoader = requireNonNull(configDecisionTreeLoader);
  }

  Settings map(CommandLine commandLine, String... args) {
    AppSettings appSettings = createAppSettings(args);
    Path inputPath = Path.of(commandLine.getOptionValue(OPT_INPUT));
    ConfigDecisionTree configDecisionTree = createDecisionTree(commandLine);
    WriterSettings writerSettings = createWriterSettings(commandLine);
    boolean strict = commandLine.hasOption(OPT_STRICT);
    return Settings.builder()
        .inputVcfPath(inputPath)
        .configDecisionTree(configDecisionTree)
        .appSettings(appSettings)
        .writerSettings(writerSettings)
        .strict(strict)
        .build();
  }

  private ConfigDecisionTree createDecisionTree(CommandLine commandLine) {
    Path configPath = Path.of(commandLine.getOptionValue(OPT_CONFIG));
    return configDecisionTreeLoader.load(configPath);
  }

  private AppSettings createAppSettings(String... args) {
    return AppSettings.builder().name(appName).version(appVersion).args(asList(args)).build();
  }

  private WriterSettings createWriterSettings(CommandLine commandLine) {
    Path outputPath;
    if (commandLine.hasOption(OPT_OUTPUT)) {
      outputPath = Path.of(commandLine.getOptionValue(OPT_OUTPUT));
    } else {
      outputPath = Path.of(commandLine.getOptionValue(OPT_INPUT).replace(".vcf", ".out.vcf"));
    }

    boolean overwriteOutput = commandLine.hasOption(OPT_FORCE);
    boolean writeLabels = commandLine.hasOption(OPT_LABELS);
    boolean writePath = commandLine.hasOption(OPT_PATH);

    return WriterSettings.builder()
        .outputVcfPath(outputPath)
        .overwriteOutput(overwriteOutput)
        .writeLabels(writeLabels)
        .writePath(writePath)
        .build();
  }
}
