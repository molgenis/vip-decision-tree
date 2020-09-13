package org.molgenis.vcf.decisiontree;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_DEBUG;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_DEBUG_LONG;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE_LONG;

import ch.qos.logback.classic.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.molgenis.vcf.decisiontree.runner.AppRunner;
import org.molgenis.vcf.decisiontree.runner.AppRunnerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class AppCommandLineRunner implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppCommandLineRunner.class);

  private static final int STATUS_MISC_ERROR = 1;
  private static final int STATUS_COMMAND_LINE_USAGE_ERROR = 64;

  private final String appName;
  private final String appVersion;
  private final AppCommandLineToSettingsMapper appCommandLineToSettingsMapper;
  private final AppRunnerFactory appRunnerFactory;
  private final CommandLineParser commandLineParser;

  AppCommandLineRunner(
      @Value("${app.name}") String appName,
      @Value("${app.version}") String appVersion,
      AppCommandLineToSettingsMapper appCommandLineToSettingsMapper,
      AppRunnerFactory appRunnerFactory) {
    this.appName = requireNonNull(appName);
    this.appVersion = requireNonNull(appVersion);
    this.appCommandLineToSettingsMapper = requireNonNull(appCommandLineToSettingsMapper);
    this.appRunnerFactory = requireNonNull(appRunnerFactory);

    this.commandLineParser = new DefaultParser();
  }

  @Override
  public void run(String... args) {
    if (args.length == 1
        && (args[0].equals("-" + AppCommandLineOptions.OPT_VERSION)
            || args[0].equals("--" + AppCommandLineOptions.OPT_VERSION_LONG))) {
      LOGGER.info("{} {}", appName, appVersion);
      return;
    }

    for (String arg : args) {
      if (arg.equals('-' + OPT_DEBUG) || arg.equals('-' + OPT_DEBUG_LONG)) {
        Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (!(rootLogger instanceof ch.qos.logback.classic.Logger)) {
          throw new ClassCastException("Expected root logger to be a logback logger");
        }
        ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.DEBUG);
        break;
      }
    }

    try {
      Settings settings = createSettings(args);

      @NonNull Path outputReportPath = settings.getWriterSettings().getOutputVcfPath();
      if (settings.getWriterSettings().isOverwriteOutput()) {
        Files.deleteIfExists(outputReportPath);
      } else if (Files.exists(outputReportPath)) {
        throw new IllegalArgumentException(
            String.format(
                "cannot create report '%s' because it already exists, use -%s or --%s to overwrite existing file",
                outputReportPath, OPT_FORCE, OPT_FORCE_LONG));
      }

      try (AppRunner appRunner = appRunnerFactory.create(settings)) {
        appRunner.run();
      }

    } catch (Exception e) {
      LOGGER.error(e.getLocalizedMessage(), e);
      System.exit(STATUS_MISC_ERROR);
    }
  }

  private Settings createSettings(String... args) {
    CommandLine commandLine = null;
    try {
      commandLine = commandLineParser.parse(AppCommandLineOptions.getAppOptions(), args);
    } catch (ParseException e) {
      logException(e);
      System.exit(STATUS_COMMAND_LINE_USAGE_ERROR);
    }

    AppCommandLineOptions.validateCommandLine(commandLine);
    return appCommandLineToSettingsMapper.map(commandLine, args);
  }

  @SuppressWarnings("java:S106")
  private void logException(ParseException e) {
    LOGGER.error(e.getLocalizedMessage(), e);

    // following information is only logged to system out
    System.out.println();
    HelpFormatter formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    String cmdLineSyntax = "java -jar " + appName + ".jar";
    formatter.printHelp(cmdLineSyntax, AppCommandLineOptions.getAppOptions(), true);
    System.out.println();
    formatter.printHelp(cmdLineSyntax, AppCommandLineOptions.getAppVersionOptions(), true);
  }
}
