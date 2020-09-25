package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

@ExtendWith(MockitoExtension.class)
class SnpEffInfoMetadataMapperTest {

  private SnpEffInfoMetadataMapper snpEffInfoMetadataMapper;
  @Mock
  VCFInfoHeaderLine headerLine;
  private Field snpEffField;
  private SnpEffInfoSelector selector;

  @BeforeEach
  void setUp() {
    selector = new SnpEffInfoSelector();
    snpEffInfoMetadataMapper = new SnpEffInfoMetadataMapper(selector);

    snpEffField = Field.builder()
        .id("ANN")
        .fieldType(FieldType.INFO)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(VARIABLE).build())
        .separator('|')
        .build();
  }

  @Test
  void canMap() {
    when(headerLine.getDescription()).thenReturn(
        "Functional annotations: 'Allele | Annotation | Annotation_Impact | Gene_Name | Gene_ID | Feature_Type | Feature_ID | Transcript_BioType | Rank | HGVS.c | HGVS.p | cDNA.pos / cDNA.length | CDS.pos / CDS.length | AA.pos / AA.length | Distance | ERRORS / WARNINGS / INFO' ");
    when(headerLine.getID()).thenReturn("ANN");
    assertTrue(snpEffInfoMetadataMapper.canMap(headerLine));
  }

  @Test
  void cantMapId() {
    when(headerLine.getID()).thenReturn("EFF");
    assertFalse(snpEffInfoMetadataMapper.canMap(headerLine));
  }

  @Test
  void cantMapDesc() {
    when(headerLine.getID()).thenReturn("ANN");
    when(headerLine.getDescription()).thenReturn(
        "Not Functional annotations: 'Allele | Annotation | Annotation_Impact | Gene_Name | Gene_ID | Feature_Type | Feature_ID | Transcript_BioType | Rank | HGVS.c | HGVS.p | cDNA.pos / cDNA.length | CDS.pos / CDS.length | AA.pos / AA.length | Distance | ERRORS / WARNINGS / INFO' ");
    assertFalse(snpEffInfoMetadataMapper.canMap(headerLine));
  }

  @Test
  void map() {
    when(headerLine.getID()).thenReturn("ANN");
    when(headerLine.getDescription()).thenReturn(
        "Functional annotations: 'Allele | cDNA.pos / cDNA.length | Distance | ERRORS / WARNINGS / INFO' ");

    Map<String, NestedField> actual = snpEffInfoMetadataMapper
        .map(headerLine);

    Map<String, NestedField> expected = new HashMap<>();
    expected.put("Allele", getFixedStringField("Allele", 0));
    expected.put("cDNA.pos/cDNA.length", getFixeTwoIntegerField("cDNA.pos/cDNA.length", 1));
    expected.put("Distance", getFixedOneIntegerField("Distance", 2));
    expected.put("ERRORS/WARNINGS/INFO", getVariableFixedThreeStringField("ERRORS/WARNINGS/INFO", 3));
    assertAll(
        () -> assertTrue(expected.keySet().containsAll(actual.keySet())),
        () -> assertEquals(expected.size(), actual.size()),
        () -> assertEquals(expected.get("Allele"), actual.get("Allele")),
        () -> assertEquals(expected.get("cDNA.pos/cDNA.length"), actual.get("cDNA.pos/cDNA.length")),
        () -> assertEquals(expected.get("Distance"), actual.get("Distance")),
        () -> assertEquals(expected.get("ERRORS/WARNINGS/INFO"), actual.get("ERRORS/WARNINGS/INFO")));
  }

  private NestedField getVariableFixedThreeStringField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(snpEffField)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(3).build())
        .separator('/')
        .nestedInfoSelector(selector)
        .valueType(ValueType.STRING).build();
  }

  private NestedField getFixeTwoIntegerField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(snpEffField)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(2).build())
        .separator('/')
        .nestedInfoSelector(selector)
        .valueType(ValueType.INTEGER).build();
  }

  private NestedField getFixedOneIntegerField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(snpEffField)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .nestedInfoSelector(selector)
        .valueType(ValueType.INTEGER).build();
  }

  private NestedField getFixedStringField(String id, int index) {
    return NestedField.nestedBuilder().id(id).index(index).parent(snpEffField)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .nestedInfoSelector(selector)
        .valueType(ValueType.STRING).build();
  }
}