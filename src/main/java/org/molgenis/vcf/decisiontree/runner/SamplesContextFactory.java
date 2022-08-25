package org.molgenis.vcf.decisiontree.runner;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.molgenis.vcf.decisiontree.SampleSettings;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.decisiontree.filter.model.SamplesContext;
import org.molgenis.vcf.decisiontree.ped.InvalidSamplePhenotypesException;
import org.molgenis.vcf.decisiontree.ped.MixedPhenotypesException;
import org.molgenis.vcf.decisiontree.ped.PedIndividual;
import org.molgenis.vcf.decisiontree.ped.PedIndividual.AffectionStatus;
import org.molgenis.vcf.decisiontree.ped.PedReader;
import org.molgenis.vcf.decisiontree.ped.model.AffectedStatus;
import org.molgenis.vcf.decisiontree.ped.model.Sex;

public class SamplesContextFactory {

  private SamplesContextFactory() {
  }

  public static final String SAMPLE_PHENOTYPE_SEPARATOR = "/";
  public static final String PHENOTYPE_SEPARATOR = ";";

  public static SamplesContext create(Settings settings, VcfMetadata vcfMetadata) {
    SampleSettings sampleSettings = settings.getSampleSettings();
    Map<String, Integer> vcfSampleNames = vcfMetadata.getSampleNameToOffset();
    List<String> probands = sampleSettings.getProbandNames();
    String phenotypesString = sampleSettings.getPhenotypeString();
    Map<String, List<String>> phenotypesPerSample = new HashMap<>();
    List<String> defaultPhenotypes = new ArrayList<>();
    if (phenotypesString.contains(SAMPLE_PHENOTYPE_SEPARATOR)) {
      phenotypesPerSample.putAll(parseSamplePhenotypes(phenotypesString));
    } else {
      defaultPhenotypes.addAll(Arrays.asList(phenotypesString.split(PHENOTYPE_SEPARATOR)));
    }

    List<SampleContext> sampleContexts = new ArrayList<>();
    Set<String> processedSamples = new LinkedHashSet<>();
    for (Path pedigreePath : sampleSettings.getPedigreePaths()) {
      try (PedReader reader = new PedReader(new FileReader(pedigreePath.toFile()))) {
        Map<String, SampleContext> sampleContextMap = parse(reader, probands, phenotypesPerSample,
            defaultPhenotypes,
            vcfSampleNames);
        sampleContexts.addAll(sampleContextMap.values());
        processedSamples.addAll(sampleContextMap.keySet());
      } catch (IOException e) {
        // this should never happen since the files were validated in the AppCommandLineOptions
        throw new IllegalStateException(e);
      }
    }

    if (!vcfSampleNames.keySet().containsAll(probands)) {
      List<String> unmatchedProbands = probands.stream()
          .filter(proband -> !vcfSampleNames.containsKey(proband)).toList();
      throw new MissingProbandsException(unmatchedProbands);
    }

    vcfSampleNames.keySet().stream().filter(sampleId -> !processedSamples.contains(sampleId))
        .forEach(sampleId -> sampleContexts.add(
            createDefaultSampleContext(sampleId, vcfSampleNames.get(sampleId),
                defaultPhenotypes, phenotypesPerSample, probands)));

    return SamplesContext.builder().sampleContexts(
            sampleContexts.stream().filter(sampleContext -> sampleContext.getIndex() != -1).collect(
                Collectors.toSet()))
        .build();
  }

  private static Map<String, List<String>> parseSamplePhenotypes(String phenotypesString) {
    Map<String, List<String>> result = new HashMap<>();
    for (String samplePhenotypes : phenotypesString.split(",")) {
      if (samplePhenotypes.contains("/")) {
        String[] split = samplePhenotypes.split("/");
        if (split.length == 2) {
          String sampleId = split[0];
          String[] phenotypes = split[1].split(";");
          result.put(sampleId, Arrays.asList(phenotypes));
        } else {
          throw new InvalidSamplePhenotypesException(samplePhenotypes);
        }
      } else {
        throw new MixedPhenotypesException();
      }
    }
    return result;
  }

  public static SampleContext createDefaultSampleContext(String sampleId, Integer sampleIndex,
      List<String> defaultPhenotypes, Map<String, List<String>> phenotypesPerSample,
      List<String> probands) {
    return SampleContext.builder().id(sampleId)
        .index(sampleIndex)
        .affectedStatus(AffectedStatus.MISSING)
        .sex(Sex.UNKNOWN)
        .familyId(null)
        .fatherId(null)
        .motherId(null)
        .proband(probands.contains(sampleId))
        .phenotypes(getSamplePhenotypes(sampleId, phenotypesPerSample, defaultPhenotypes))
        .build();
  }

  private static List<String> getSamplePhenotypes(String sampleId,
      Map<String, List<String>> phenotypesPerSample, List<String> defaultPhenotypes) {
    if (phenotypesPerSample.containsKey(sampleId)) {
      return phenotypesPerSample.get(sampleId);
    }
    return defaultPhenotypes;
  }

  private static Map<String, SampleContext> parse(PedReader reader, List<String> probands,
      Map<String, List<String>> phenotypesPerSample, List<String> defaultPhenotypes,
      Map<String, Integer> vcfSampleNames) {
    final Map<String, SampleContext> samplesContextMap = new HashMap<>();
    StreamSupport.stream(Spliterators.spliteratorUnknownSize(reader.iterator(), 0), false)
        .filter(pedIndividual -> vcfSampleNames.containsKey(pedIndividual.getId()))
        .map(individual -> map(individual, probands,
            getSamplePhenotypes(individual.getId(), phenotypesPerSample, defaultPhenotypes),
            vcfSampleNames.get(individual.getId())))
        .filter(person -> person.getIndex() != -1).forEach(person -> samplesContextMap
            .put(person.getId(), person));
    return samplesContextMap;
  }

  private static SampleContext map(PedIndividual pedIndividual, List<String> probands,
      List<String> phenotypes, int index) {
    return SampleContext.builder().id(pedIndividual.getId())
        .index(index)
        .affectedStatus(map(pedIndividual.getAffectionStatus()))
        .sex(map(pedIndividual.getSex()))
        .familyId(pedIndividual.getFamilyId())
        .fatherId(pedIndividual.getPaternalId())
        .motherId(pedIndividual.getMaternalId())
        .proband(probands.contains(pedIndividual.getId()))
        .phenotypes(phenotypes).build();
  }

  private static Sex map(PedIndividual.Sex sex) {
    switch (sex) {
      case MALE:
        return Sex.MALE;
      case FEMALE:
        return Sex.FEMALE;
      default:
        return Sex.UNKNOWN;
    }
  }

  private static AffectedStatus map(AffectionStatus affectionStatus) {
    switch (affectionStatus) {
      case AFFECTED:
        return AffectedStatus.AFFECTED;
      case UNAFFECTED:
        return AffectedStatus.UNAFFECTED;
      case UNKNOWN:
        return AffectedStatus.MISSING;
      default:
        return AffectedStatus.UNRECOGNIZED;
    }
  }
}
