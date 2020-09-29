package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.info.NestedInfoHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VcfNestedMetadata;

@ExtendWith(MockitoExtension.class)
class VcfMetadataTest {

  @Mock
  VCFHeader vcfHeader;
  private VcfMetadata vcfMetadata;

  @BeforeEach
  void setUp() {
    Map<String, NestedField> vepNestedMetadata = new HashMap<>();
    vepNestedMetadata.put("Allele", createNestedField("Allele"));
    vepNestedMetadata.put("PICK", createNestedField("PICK"));
    vepNestedMetadata.put("consequence", createNestedField("consequence"));
    NestedInfoHeaderLine nestedInfoHeaderLine = NestedInfoHeaderLine.builder().nestedFields(vepNestedMetadata).build();
    vcfMetadata = new VcfMetadata(vcfHeader, VcfNestedMetadata.builder().nestedLines(Collections.singletonMap("VEP", nestedInfoHeaderLine)).build());
  }

@Test
  void getFieldCommonChrom() {
    String fieldId = "#CHROM";
    assertEquals(
        Field.builder()
            .id(fieldId)
            .fieldType(FieldType.COMMON)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldCommonPos() {
    String fieldId = "POS";
    assertEquals(
        Field.builder()
            .id(fieldId)
            .fieldType(FieldType.COMMON)
            .valueType(ValueType.INTEGER)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ID", "ALT", "FILTER"})
  void getFieldCommonId() {
    String fieldId = "ID";
    assertEquals(
        Field.builder()
            .id(fieldId)
            .fieldType(FieldType.COMMON)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.VARIABLE).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldCommonRef() {
    String fieldId = "REF";
    assertEquals(
        Field.builder()
            .id(fieldId)
            .fieldType(FieldType.COMMON)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldCommonQual() {
    String fieldId = "QUAL";
    assertEquals(
        Field.builder()
            .id(fieldId)
            .fieldType(FieldType.COMMON)
            .valueType(ValueType.FLOAT)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldCommonInvalid() {
    assertThrows(UnsupportedFieldException.class, () -> vcfMetadata.getField("ïnvalid"));
  }

  @Test
  void getFieldInfoInteger() {
    VCFInfoHeaderLine vcfInfoHeaderLine = mock(VCFInfoHeaderLine.class);
    when(vcfInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.Integer);
    when(vcfInfoHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.G);
    when(vcfHeader.getInfoHeaderLine("my_field")).thenReturn(vcfInfoHeaderLine);
    String fieldId = "INFO/my_field";
    assertEquals(
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueType(ValueType.INTEGER)
            .valueCount(ValueCount.builder().type(Type.G).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldInfoFloat() {
    VCFInfoHeaderLine vcfInfoHeaderLine = mock(VCFInfoHeaderLine.class);
    when(vcfInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.Float);
    when(vcfInfoHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.R);
    when(vcfHeader.getInfoHeaderLine("my_field")).thenReturn(vcfInfoHeaderLine);
    String fieldId = "INFO/my_field";
    assertEquals(
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueType(ValueType.FLOAT)
            .valueCount(ValueCount.builder().type(Type.R).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldInfoString() {
    VCFInfoHeaderLine vcfInfoHeaderLine = mock(VCFInfoHeaderLine.class);
    when(vcfInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.String);
    when(vcfInfoHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.A);
    when(vcfHeader.getInfoHeaderLine("my_field")).thenReturn(vcfInfoHeaderLine);
    String fieldId = "INFO/my_field";
    assertEquals(
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.A).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldInfoStringUnbounded() {
    VCFInfoHeaderLine vcfInfoHeaderLine = mock(VCFInfoHeaderLine.class);
    when(vcfInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.String);
    when(vcfInfoHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.UNBOUNDED);
    when(vcfHeader.getInfoHeaderLine("my_field")).thenReturn(vcfInfoHeaderLine);
    String fieldId = "INFO/my_field";
    assertEquals(
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.VARIABLE).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldInfoCharacter() {
    VCFInfoHeaderLine vcfInfoHeaderLine = mock(VCFInfoHeaderLine.class);
    when(vcfInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.Character);
    when(vcfInfoHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.R);
    when(vcfHeader.getInfoHeaderLine("my_field")).thenReturn(vcfInfoHeaderLine);
    String fieldId = "INFO/my_field";
    assertEquals(
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueType(ValueType.CHARACTER)
            .valueCount(ValueCount.builder().type(Type.R).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldInfoFlag() {
    VCFInfoHeaderLine vcfInfoHeaderLine = mock(VCFInfoHeaderLine.class);
    when(vcfInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.Flag);
    when(vcfInfoHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.INTEGER);
    when(vcfInfoHeaderLine.getCount()).thenReturn(1);
    when(vcfHeader.getInfoHeaderLine("my_field")).thenReturn(vcfInfoHeaderLine);
    String fieldId = "INFO/my_field";
    assertEquals(
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueType(ValueType.FLAG)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldInfoUnknown() {
    assertThrows(UnknownFieldException.class, () -> vcfMetadata.getField("INFO/unknown"));
  }

  @Test
  void getFieldFormatInfo() {
    VCFFormatHeaderLine vcfFormatHeaderLine = mock(VCFFormatHeaderLine.class);
    when(vcfFormatHeaderLine.getType()).thenReturn(VCFHeaderLineType.Integer);
    when(vcfFormatHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.G);
    when(vcfHeader.getFormatHeaderLine("my_field")).thenReturn(vcfFormatHeaderLine);
    String fieldId = "FORMAT/my_field";
    assertEquals(
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.INTEGER)
            .valueCount(ValueCount.builder().type(Type.G).nullable(true).build())
            .build(),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getNestedFieldInfoUnknown() {
    assertThrows(UnknownFieldException.class, () -> vcfMetadata.getField("INFO/VEP/unknown"));
  }

  @Test
  void getNestedFieldInfoUnknownParent() {
    assertThrows(UnknownFieldException.class, () -> vcfMetadata.getField("INFO/VOP/consequence"));
  }

  @Test
  void getNestedFieldInfoString() {
    String fieldId = "INFO/VEP/consequence";
    assertEquals(createNestedField("consequence"),
        vcfMetadata.getField(fieldId));
  }

  @Test
  void getFieldFormatUnknown() {
    assertThrows(UnknownFieldException.class, () -> vcfMetadata.getField("FORMAT/unknown"));
  }

  @Test
  void unwrap() {
    assertEquals(vcfHeader, vcfMetadata.unwrap());
  }

  private NestedField createNestedField(String field) {
    ValueCount valueCount = ValueCount.builder().type(Type.VARIABLE).build();
    Field parent = Field.builder().id("VEP").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(valueCount).separator('|').build();
    return NestedField.nestedBuilder().id(field).parent(parent).fieldType(FieldType.INFO_NESTED).valueType(ValueType.STRING).valueCount(valueCount).build();
  }
}
