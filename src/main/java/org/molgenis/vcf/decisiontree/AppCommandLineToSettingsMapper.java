package org.molgenis.vcf.decisiontree;

import org.apache.commons.cli.CommandLine;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeLoader;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.*;

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
    Path metadataPath = Path.of(commandLine.getOptionValue(OPT_METADATA));
    ConfigDecisionTree configDecisionTree = createDecisionTree(commandLine);
    WriterSettings writerSettings = createWriterSettings(commandLine);
    boolean strict = commandLine.hasOption(OPT_STRICT);
    Mode mode = getType(commandLine);
    SampleSettings sampleSettings = createSampleSettings(commandLine);

    return Settings.builder()
        .mode(mode)
        .inputVcfPath(inputPath)
        .metadataPath(metadataPath)
        .configDecisionTree(configDecisionTree)
        .appSettings(appSettings)
        .writerSettings(writerSettings)
        .strict(strict)
        .sampleSettings(sampleSettings)
        .build();
  }

  private Mode getType(CommandLine commandLine) {
    Mode mode;
    if (commandLine.hasOption(OPT_TYPE)) {
      mode = Mode.valueOf(commandLine.getOptionValue(OPT_TYPE).toUpperCase());
    } else {
      mode = Mode.VARIANT;
    }
    return mode;
  }

  private SampleSettings createSampleSettings(CommandLine commandLine) {
    String phenotypesString = "";
    if (commandLine.hasOption(OPT_PHENOTYPES)) {
      phenotypesString = commandLine.getOptionValue(OPT_PHENOTYPES);
    }

    List<String> probandNames = List.of();
    if (commandLine.hasOption(OPT_PROBANDS)) {
      probandNames = Arrays.asList(commandLine.getOptionValue(OPT_PROBANDS).split(","));
    }

    List<Path> pedPaths = List.of();
    if (commandLine.hasOption(OPT_PED)) {
      pedPaths = parsePaths(commandLine.getOptionValue(OPT_PED));
    }

    return new SampleSettings(probandNames, pedPaths, phenotypesString);
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

  private static List<Path> parsePaths(String optionValue) {
    List<Path> result = new ArrayList<>();
    String[] paths = optionValue.split(",");
    for (String path : paths) {
      result.add(Path.of(path));
    }
    return result;
  }
}
