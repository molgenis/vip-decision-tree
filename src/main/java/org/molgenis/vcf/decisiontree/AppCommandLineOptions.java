package org.molgenis.vcf.decisiontree;

import static java.lang.String.format;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.molgenis.vcf.decisiontree.filter.model.Mode;

class AppCommandLineOptions {

  static final String OPT_INPUT = "i";
  static final String OPT_INPUT_LONG = "input";
  static final String OPT_METADATA = "m";
  static final String OPT_METADATA_LONG = "metadata";
  static final String OPT_CONFIG = "c";
  static final String OPT_CONFIG_LONG = "config";
  static final String OPT_OUTPUT = "o";
  static final String OPT_OUTPUT_LONG = "output";
  static final String OPT_LABELS = "l";
  static final String OPT_LABELS_LONG = "labels";
  static final String OPT_PATH = "p";
  static final String OPT_PATH_LONG = "path";
  static final String OPT_FORCE = "f";
  static final String OPT_FORCE_LONG = "force";
  static final String OPT_DEBUG = "d";
  static final String OPT_DEBUG_LONG = "debug";
  static final String OPT_VERSION = "v";
  static final String OPT_VERSION_LONG = "version";
  static final String OPT_STRICT = "s";
  static final String OPT_STRICT_LONG = "strict";
  static final String OPT_PROBANDS = "pb";
  static final String OPT_PROBANDS_LONG = "probands";
  static final String OPT_PED = "pd";
  static final String OPT_PED_LONG = "pedigree";
  static final String OPT_PHENOTYPES = "ph";
  static final String OPT_PHENOTYPES_LONG = "phenotypes";
  static final String OPT_TYPE = "t";
  static final String OPT_TYPE_LONG = "type";
  private static final Options APP_OPTIONS;
  private static final Options APP_VERSION_OPTIONS;

  static {
    Options appOptions = new Options();
    appOptions.addOption(
        Option.builder(OPT_INPUT)
            .hasArg(true)
            .required()
            .longOpt(OPT_INPUT_LONG)
            .desc("VEP* annotated input VCF file.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_CONFIG)
            .hasArg(true)
            .required()
            .longOpt(OPT_CONFIG_LONG)
            .desc("Input decision tree file (.json).")
            .build());
    appOptions.addOption(
        Option.builder(OPT_METADATA)
            .hasArg(true)
            .required()
            .longOpt(OPT_METADATA_LONG)
            .desc("VCF metadata file (.json).")
            .build());
    appOptions.addOption(
        Option.builder(OPT_OUTPUT)
            .hasArg(true)
            .longOpt(OPT_OUTPUT_LONG)
            .desc("Output VCF file (.vcf or .vcf.gz).")
            .build());
    appOptions.addOption(
        Option.builder(OPT_FORCE)
            .longOpt(OPT_FORCE_LONG)
            .desc("Override the output file if it already exists.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_STRICT)
            .longOpt(OPT_STRICT_LONG)
            .desc(
                "Throw exception if field from the decision tree is missing entirely in the input VCF.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_LABELS)
            .longOpt(OPT_LABELS_LONG)
            .desc("Write decision tree outcome labels to output VCF file.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_PATH)
            .longOpt(OPT_PATH_LONG)
            .desc("Write decision tree node path to output VCF file.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_DEBUG)
            .longOpt(OPT_DEBUG_LONG)
            .desc("Enable debug mode (additional logging).")
            .build());
    appOptions.addOption(
        Option.builder(OPT_PROBANDS)
            .hasArg(true)
            .longOpt(OPT_PROBANDS_LONG)
            .desc("Comma-separated list of proband names.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_PED)
            .hasArg(true)
            .longOpt(OPT_PED_LONG)
            .desc("Comma-separated list of pedigree files (.ped).")
            .build());
    appOptions.addOption(
        Option.builder(OPT_PHENOTYPES)
            .hasArg(true)
            .longOpt(OPT_PHENOTYPES_LONG)
            .desc(
                "Comma-separated list of sample-phenotypes (e.g. HP:123 or HP:123;HP:234 or sample0/HP:123,sample1/HP:234). Phenotypes are CURIE formatted (prefix:reference) and separated by a semicolon.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_TYPE)
            .hasArg(true)
            .longOpt(OPT_TYPE_LONG)
            .desc(
                "Type: 'variant' (default) or 'sample', 'sample' classifies provided probands, or all samples if no probands given.")
            .build());
    APP_OPTIONS = appOptions;
    Options appVersionOptions = new Options();
    appVersionOptions.addOption(
        Option.builder(OPT_VERSION)
            .required()
            .longOpt(OPT_VERSION_LONG)
            .desc("Print version.")
            .build());
    APP_VERSION_OPTIONS = appVersionOptions;
  }

  private AppCommandLineOptions() {}

  static Options getAppOptions() {
    return APP_OPTIONS;
  }

  static Options getAppVersionOptions() {
    return APP_VERSION_OPTIONS;
  }

  static void validateCommandLine(CommandLine commandLine) {
    validateInput(commandLine);
    validateMetadata(commandLine);
    validateConfig(commandLine);
    validateOutput(commandLine);
    validateType(commandLine);
  }

  private static void validateInput(CommandLine commandLine) {
    Path inputPath = Path.of(commandLine.getOptionValue(OPT_INPUT));
    if (!Files.exists(inputPath)) {
      throw new IllegalArgumentException(
          format("Input file '%s' does not exist.", inputPath));
    }
    if (Files.isDirectory(inputPath)) {
      throw new IllegalArgumentException(
          format("Input file '%s' is a directory.", inputPath));
    }
    if (!Files.isReadable(inputPath)) {
      throw new IllegalArgumentException(
          format("Input file '%s' is not readable.", inputPath));
    }
    String inputPathStr = inputPath.toString();
    if (!inputPathStr.endsWith(".vcf") && !inputPathStr.endsWith(".vcf.gz")) {
      throw new IllegalArgumentException(
          format("Input file '%s' is not a .vcf or .vcf.gz file.", inputPathStr));
    }
  }

  private static void validateMetadata(CommandLine commandLine) {
    Path metadataPath = Path.of(commandLine.getOptionValue(OPT_METADATA));
    if (!Files.exists(metadataPath)) {
      throw new IllegalArgumentException(
              format("Metadata file '%s' does not exist.", metadataPath));
    }
    if (Files.isDirectory(metadataPath)) {
      throw new IllegalArgumentException(
              format("Metadata file '%s' is a directory.", metadataPath));
    }
    if (!Files.isReadable(metadataPath)) {
      throw new IllegalArgumentException(
              format("Metadata file '%s' is not readable.", metadataPath));
    }
    String inputPathStr = metadataPath.toString();
    if (!inputPathStr.endsWith(".json")) {
      throw new IllegalArgumentException(
              format("Metadata file '%s' is not a .json file.", inputPathStr));
    }
  }
  private static void validateConfig(CommandLine commandLine) {
    Path configPath = Path.of(commandLine.getOptionValue(OPT_CONFIG));
    if (!Files.exists(configPath)) {
      throw new IllegalArgumentException(
          format("Config file '%s' does not exist.", configPath));
    }
    if (Files.isDirectory(configPath)) {
      throw new IllegalArgumentException(
          format("Config file '%s' is a directory.", configPath));
    }
    if (!Files.isReadable(configPath)) {
      throw new IllegalArgumentException(
          format("Config file '%s' is not readable.", configPath));
    }
    String inputPathStr = configPath.toString();
    if (!inputPathStr.endsWith(".json")) {
      throw new IllegalArgumentException(
          format("Config file '%s' is not a .json file.", inputPathStr));
    }
  }

  private static void validateOutput(CommandLine commandLine) {
    if (!commandLine.hasOption(OPT_OUTPUT)) {
      return;
    }

    Path outputPath = Path.of(commandLine.getOptionValue(OPT_OUTPUT));

    if (!commandLine.hasOption(OPT_FORCE) && Files.exists(outputPath)) {
      throw new IllegalArgumentException(
          format("Output file '%s' already exists", outputPath));
    }
  }

  private static void validateType(CommandLine commandLine) {
    if (!commandLine.hasOption(OPT_TYPE)) {
      return;
    }

    String mode = commandLine.getOptionValue(OPT_TYPE);
    List<String> modes = Arrays.stream(Mode.values()).map(Mode::toString)
        .toList();

    if (!modes.contains(mode.toUpperCase())) {
      throw new IllegalArgumentException(
          "Illegal 'type' argument '%s', only 'variant' and 'sample' are allowed.".formatted(mode));
    }
  }
}
