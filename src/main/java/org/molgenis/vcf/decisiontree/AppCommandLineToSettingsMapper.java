package org.molgenis.vcf.decisiontree;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_CONFIG;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_INPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_LABELS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_OUTPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PATH;

import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.loader.DecisionTreeLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppCommandLineToSettingsMapper {

  private final String appName;
  private final String appVersion;
  private final DecisionTreeLoader decisionTreeLoader;

  AppCommandLineToSettingsMapper(
      @Value("${app.name}") String appName,
      @Value("${app.version}") String appVersion,
      DecisionTreeLoader decisionTreeLoader) {
    this.appName = appName;
    this.appVersion = appVersion;
    this.decisionTreeLoader = requireNonNull(decisionTreeLoader);
  }

  Settings map(CommandLine commandLine, String... args) {
    AppSettings appSettings = createAppSettings(args);
    Path inputPath = Path.of(commandLine.getOptionValue(OPT_INPUT));
    DecisionTree decisionTree = createDecisionTree(commandLine);
    WriterSettings writerSettings = createWriterSettings(commandLine);
    return Settings.builder()
        .inputVcfPath(inputPath)
        .decisionTree(decisionTree)
        .appSettings(appSettings)
        .writerSettings(writerSettings)
        .build();
  }

  private DecisionTree createDecisionTree(CommandLine commandLine) {
    Path configPath = Path.of(commandLine.getOptionValue(OPT_CONFIG));
    return decisionTreeLoader.load(configPath);
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
