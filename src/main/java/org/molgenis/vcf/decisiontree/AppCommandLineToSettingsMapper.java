package org.molgenis.vcf.decisiontree;

import static java.util.Arrays.asList;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_INPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_MODE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_OUTPUT;

import java.nio.file.Path;
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
    Mode mode = getMode(commandLine);

    return Settings.builder()
        .mode(mode)
        .inputVcfPath(inputPath)
        .appSettings(appSettings)
        .writerSettings(writerSettings)
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

    return WriterSettings.builder()
        .outputVcfPath(outputPath)
        .overwriteOutput(overwriteOutput)
        .build();
  }
}
