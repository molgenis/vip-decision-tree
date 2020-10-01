package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator.EQUALS;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;
import static org.molgenis.vcf.decisiontree.runner.info.NestedValueSelector.SELECTED_ALLELE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

@ExtendWith(MockitoExtension.class)
class SnpEffInfoMetadataMapperTest {

  public static final String ANN = "ANN";
  private SnpEffInfoMetadataMapper snpEffInfoMetadataMapper;
  @Mock
  VCFInfoHeaderLine headerLine;
  private Field.FieldBuilder snpEffField;
  @Mock
  private NestedValueSelector selector;
  @Mock
  private NestedValueSelectorFactory nestedValueSelectorFactory;

  @BeforeEach
  void setUp() {
    snpEffInfoMetadataMapper = new SnpEffInfoMetadataMapper(nestedValueSelectorFactory);
    snpEffField = Field.builder()
        .id(ANN)
        .fieldType(FieldType.INFO)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(VARIABLE).build())
        .separator('|');
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
    when(nestedValueSelectorFactory.create(Collections.singletonList(
        BoolQuery.builder().field(Field.builder()
            .id("Allele")
            .index(0)
            .parentId(ANN)
            .fieldType(FieldType.INFO_NESTED)
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.STRING).build()).operator(EQUALS).value(SELECTED_ALLELE).build()), '|'))
        .thenReturn(selector);
    when(headerLine.getID()).thenReturn("ANN");
    when(headerLine.getDescription()).thenReturn(
        "Functional annotations: 'Allele | cDNA.pos / cDNA.length | Distance | ERRORS / WARNINGS / INFO' ");

    snpEffInfoMetadataMapper = new SnpEffInfoMetadataMapper(nestedValueSelectorFactory);
    Field actual = snpEffInfoMetadataMapper
        .map(headerLine);

    Map<String, Field> expectedMap = new HashMap<>();
    expectedMap.put("Allele", getFixedStringField("Allele", 0));
    expectedMap.put("cDNA.pos/cDNA.length", getFixeTwoIntegerField("cDNA.pos/cDNA.length", 1));
    expectedMap.put("Distance", getFixedOneIntegerField("Distance", 2));
    expectedMap
        .put("ERRORS/WARNINGS/INFO", getVariableFixedThreeStringField("ERRORS/WARNINGS/INFO", 3));
    assertEquals(snpEffField.children(expectedMap).build(), actual);
  }

  private Field getVariableFixedThreeStringField(String id, int index) {
    return Field.builder().id(id).index(index).parentId(ANN)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(3).build())
        .separator('/')
        .nestedValueSelector(selector)
        .valueType(ValueType.STRING).build();
  }

  private Field getFixeTwoIntegerField(String id, int index) {
    return Field.builder().id(id).index(index).parentId(ANN)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(2).build())
        .separator('/')
        .nestedValueSelector(selector)
        .valueType(ValueType.INTEGER).build();
  }

  private Field getFixedOneIntegerField(String id, int index) {
    return Field.builder().id(id).index(index).parentId(ANN)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .nestedValueSelector(selector)
        .valueType(ValueType.INTEGER).build();
  }

  private Field getFixedStringField(String id, int index) {
    return Field.builder().id(id).index(index).parentId(ANN)
        .fieldType(FieldType.INFO_NESTED)
        .valueCount(ValueCount.builder().type(FIXED).count(1).build())
        .nestedValueSelector(selector)
        .valueType(ValueType.STRING).build();
  }
}