package org.molgenis.vcf.decisiontree;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_CONFIG;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_INPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_LABELS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_MODE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_OUTPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PATH;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PROBANDS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_STRICT;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
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
    Mode mode = getMode(commandLine);
    Set<String> probands = getProbands(commandLine);
    return Settings.builder()
        .mode(mode)
        .inputVcfPath(inputPath)
        .configDecisionTree(configDecisionTree)
        .appSettings(appSettings)
        .writerSettings(writerSettings)
        .strict(strict)
        .probands(probands)
        .build();
  }

  private Set<String> getProbands(CommandLine commandLine) {
    Set<String> probands;
    if (commandLine.hasOption(OPT_PROBANDS)) {
      probands = Set.of(commandLine.getOptionValue(OPT_PROBANDS).split(","));
    } else {
      probands = Collections.emptySet();
    }
    return probands;
  }

  private Mode getMode(CommandLine commandLine) {
    Mode mode;
    if (commandLine.hasOption(OPT_MODE)) {
      mode = Mode.valueOf(commandLine.getOptionValue(OPT_MODE).toUpperCase());
    } else {
      mode = Mode.VARIANT;
    }
    return mode;
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
