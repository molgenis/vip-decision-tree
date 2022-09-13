package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.SampleSettings;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.decisiontree.filter.model.SamplesContext;
import org.molgenis.vcf.utils.sample.model.AffectedStatus;
import org.molgenis.vcf.utils.sample.model.Sex;
import org.springframework.util.ResourceUtils;

@ExtendWith(MockitoExtension.class)
class SamplesContextFactoryTest {

  @Mock
  private VcfMetadata vcfMetadata;

  @Test
  void create() throws FileNotFoundException {
    Path pedFile = ResourceUtils.getFile("classpath:example_samples.ped").toPath();
    SampleSettings sampleSettings = new SampleSettings(List.of("Patient"), List.of(pedFile),
        "Patient/HP:123,Mother/HP:234,");
    Settings settings = Settings.builder().sampleSettings(sampleSettings).build();
    SampleContext patient = SampleContext.builder().id("Patient").index(0).familyId("FAM001")
        .fatherId("Father").motherId("Mother").phenotypes(List.of("HP:123")).proband(true).sex(
            Sex.MALE).affectedStatus(AffectedStatus.AFFECTED).build();
    SampleContext mother = SampleContext.builder().id("Mother").index(1).familyId("FAM001")
        .fatherId("0").motherId("0").phenotypes(List.of("HP:234")).proband(false).sex(
            Sex.FEMALE).affectedStatus(AffectedStatus.UNAFFECTED).build();

    SamplesContext expected = SamplesContext.builder().sampleContexts(Set.of(patient, mother))
        .build();
    HashMap<String, Integer> sampleMap = new HashMap<>(
        Map.of("Patient", Integer.valueOf(0), "Mother", Integer.valueOf(1)));
    when(vcfMetadata.getSampleNameToOffset()).thenReturn(sampleMap);

    assertEquals(expected, SamplesContextFactory.create(settings, vcfMetadata));
  }

  @Test
  void createDefaultSampleContext() {
    SampleContext expected = SampleContext.builder().id("sample").index(2)
        .phenotypes(List.of("HP3", "HP4")).proband(true).sex(
            Sex.UNKNOWN).affectedStatus(AffectedStatus.MISSING).build();
    SampleContext actual = SamplesContextFactory.createDefaultSampleContext("sample", 2,
        List.of("HP1", "HP2"), Map.of("sample", List.of("HP3", "HP4")), List.of("sample"));
    assertEquals(expected, actual);
  }

  @Test
  void createDefaultSampleContext2() {
    SampleContext expected = SampleContext.builder().id("sample2").index(2)
        .phenotypes(List.of("HP1", "HP2")).proband(false).sex(
            Sex.UNKNOWN).affectedStatus(AffectedStatus.MISSING).build();
    SampleContext actual = SamplesContextFactory.createDefaultSampleContext("sample2", 2,
        List.of("HP1", "HP2"), Map.of("sample", List.of("HP3", "HP4")), List.of("sample"));
    assertEquals(expected, actual);
  }
}