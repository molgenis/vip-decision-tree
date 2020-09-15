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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

@ExtendWith(MockitoExtension.class)
class VcfMetadataTest {

  @Mock
  VCFHeader vcfHeader;
  private VcfMetadata vcfMetadata;

  @BeforeEach
  void setUp() {
    vcfMetadata = new VcfMetadata(vcfHeader);
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

  @Test
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
  void getFieldCommonAlt() {
    String fieldId = "ALT";
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
  void getFieldCommonFilter() {
    String fieldId = "FILTER";
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
  void getFieldFormatUnknown() {
    assertThrows(UnknownFieldException.class, () -> vcfMetadata.getField("FORMAT/unknown"));
  }

  @Test
  void unwrap() {
    assertEquals(vcfHeader, vcfMetadata.unwrap());
  }
}