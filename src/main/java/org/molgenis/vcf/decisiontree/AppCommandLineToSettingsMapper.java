package org.molgenis.vcf.decisiontree;

import static java.util.Arrays.asList;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_INPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_LABELS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_MODE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_OUTPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PATH;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PED;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PHENOTYPES;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PROBANDS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_STRICT;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class AppCommandLineToSettingsMapper {

  private final String appName;
  private final String appVersion;


  AppCommandLineToSettingsMapper(
      @Value("${app.name}") String appName,
      @Value("${app.version}") String appVersion) {
    this.appName = appName;
    this.appVersion = appVersion;
  }

  Settings map(CommandLine commandLine, String... args) {
    AppSettings appSettings = createAppSettings(args);
    Path inputPath = Path.of(commandLine.getOptionValue(OPT_INPUT));
    WriterSettings writerSettings = createWriterSettings(commandLine);
    boolean strict = commandLine.hasOption(OPT_STRICT);
    Mode mode = getMode(commandLine);
    SampleSettings sampleSettings = createSampleSettings(commandLine);

    return Settings.builder()
        .mode(mode)
        .inputVcfPath(inputPath)
        .appSettings(appSettings)
        .writerSettings(writerSettings)
        .strict(strict)
        .sampleSettings(sampleSettings)
        .build();
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
