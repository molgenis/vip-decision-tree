package org.molgenis.vcf.decisiontree;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_CONFIG;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_FORCE;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_INPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_LABELS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_OUTPUT;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PATH;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PHENOTYPES;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_PROBANDS;
import static org.molgenis.vcf.decisiontree.AppCommandLineOptions.OPT_STRICT;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.molgenis.vcf.decisiontree.filter.model.SampleMeta;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeLoader;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class AppCommandLineToSettingsMapper {

  private final String appName;
  private final String appVersion;
  private final ConfigDecisionTreeLoader configDecisionTreeLoader;
  public static final String SAMPLE_PHENOTYPE_SEPARATOR = "/";
  public static final String PHENOTYPE_SEPARATOR = ";";


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
    SampleInfo sampleInfo = createSampleInfo(commandLine);
    return Settings.builder()
        .inputVcfPath(inputPath)
        .configDecisionTree(configDecisionTree)
        .appSettings(appSettings)
        .writerSettings(writerSettings)
        .sampleInfo(sampleInfo)
        .strict(strict)
        .build();
  }

  private SampleInfo createSampleInfo(CommandLine commandLine) {
    String phenotypes;
    if (commandLine.hasOption(OPT_PROBANDS)) {
      phenotypes = commandLine.getOptionValue(OPT_PHENOTYPES);
    } else {
      phenotypes = "";
    }
    List<String> probandNames;
    if (commandLine.hasOption(OPT_PROBANDS)) {
      probandNames = Arrays.asList(commandLine.getOptionValue(OPT_PROBANDS).split(","));
    } else {
      probandNames = List.of();
    }
    return mapPhenotypes(phenotypes, probandNames);
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

  public SampleInfo mapPhenotypes(String phenotypes, List<String> probands) {
    List<SamplePhenotype> phenotypeList = parse(phenotypes);
    Map<String, SampleMeta> sampleMetaMap = new HashMap<>();
    for (SamplePhenotype samplePhenotype : phenotypeList) {
      PhenotypeMode mode = samplePhenotype.getMode();
      switch (mode) {
        case STRING:
          return SampleInfo.builder().probands(probands).samplePhenotypes(List.of(
              samplePhenotype.getPhenotypes())).build();
        case PER_SAMPLE_STRING:
          sampleMetaMap.put(samplePhenotype.getSubjectId(), mapPhenotypes(samplePhenotype));
          break;
        default:
          throw new UnexpectedEnumException(mode);
      }
    }
    return SampleInfo.builder().probands(probands).sampleMetaMap(sampleMetaMap).build();
  }

  private SampleMeta mapPhenotypes(SamplePhenotype samplePhenotype) {
    return SampleMeta.builder().samplePhenotypes(List.of(samplePhenotype.getPhenotypes())).build();
  }

  private List<SamplePhenotype> parse(String phenotypesString) {
    if (phenotypesString.contains(SAMPLE_PHENOTYPE_SEPARATOR)) {
      return parseSamplePhenotypes(phenotypesString);
    } else {
      String[] phenotypes = phenotypesString.split(PHENOTYPE_SEPARATOR);
      return Collections.singletonList(new SamplePhenotype(PhenotypeMode.STRING, null, phenotypes));
    }
  }

  private List<SamplePhenotype> parseSamplePhenotypes(String phenotypesString) {
    List<SamplePhenotype> result = new ArrayList<>();
    for (String samplePhenotypes : phenotypesString.split(",")) {
      if (samplePhenotypes.contains("/")) {
        String[] split = samplePhenotypes.split("/");
        if (split.length == 2) {
          String sampleId = split[0];
          String[] phenotypes = split[1].split(";");
          result.add(new SamplePhenotype(PhenotypeMode.PER_SAMPLE_STRING, sampleId, phenotypes));
        } else {
          throw new InvalidSamplePhenotypesException(samplePhenotypes);
        }
      } else {
        throw new MixedPhenotypesException();
      }
    }
    return result;
  }
}
