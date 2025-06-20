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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

@ExtendWith(MockitoExtension.class)
class VcfMetadataTest {

    @Mock
    VCFHeader vcfHeader;
    private VcfMetadata vcfMetadata;
    private VcfMetadata vcfMetadataStrict;

    @BeforeEach
    void setUp() {
        Map<String, NestedField> vepNestedMetadata = new HashMap<>();
        vepNestedMetadata.put("Allele", createNestedField("Allele"));
        vepNestedMetadata.put("PICK", createNestedField("PICK"));
        vepNestedMetadata.put("consequence", createNestedField("consequence"));
        Field vepField = FieldImpl.builder().id("VEP").fieldType(FieldType.INFO)
                .valueType(ValueType.STRING).valueCount(ValueCount.builder()
                        .type(ValueCount.Type.VARIABLE).build()).build();
        NestedHeaderLine nestedVepHeaderLine = NestedHeaderLine.builder()
                .nestedFields(vepNestedMetadata).parentField(vepField).build();
        NestedHeaderLine nestedGtHeaderLine = NestedHeaderLine.builder()
                .nestedFields(Map.of()).parentField(vepField).build();
        vcfMetadata = new VcfMetadata(vcfHeader, nestedVepHeaderLine, nestedGtHeaderLine, null,false);
        vcfMetadataStrict = new VcfMetadata(vcfHeader, nestedVepHeaderLine, nestedGtHeaderLine,null, true);
    }

    @Test
    void getFieldCommonChrom() {
        String fieldId = "#CHROM";
        assertEquals(
                FieldImpl.builder()
                        .id(fieldId)
                        .fieldType(FieldType.COMMON)
                        .valueType(ValueType.STRING)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build())
                        .build(),
                vcfMetadata.getField(fieldId));
    }

    @Test
    void getFieldCommonPos() {
        String fieldId = "POS";
        assertEquals(
                FieldImpl.builder()
                        .id(fieldId)
                        .fieldType(FieldType.COMMON)
                        .valueType(ValueType.INTEGER)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build())
                        .build(),
                vcfMetadata.getField(fieldId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ID", "ALT", "FILTER"})
    void getFieldCommonId() {
        String fieldId = "ID";
        assertEquals(
                FieldImpl.builder()
                        .id(fieldId)
                        .fieldType(FieldType.COMMON)
                        .valueType(ValueType.STRING)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.VARIABLE).nullable(true).build())
                        .build(),
                vcfMetadata.getField(fieldId));
    }

    @Test
    void getFieldCommonRef() {
        String fieldId = "REF";
        assertEquals(
                FieldImpl.builder()
                        .id(fieldId)
                        .fieldType(FieldType.COMMON)
                        .valueType(ValueType.STRING)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build())
                        .build(),
                vcfMetadata.getField(fieldId));
    }

    @Test
    void getFieldCommonQual() {
        String fieldId = "QUAL";
        assertEquals(
                FieldImpl.builder()
                        .id(fieldId)
                        .fieldType(FieldType.COMMON)
                        .valueType(ValueType.FLOAT)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).nullable(true).build())
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
                FieldImpl.builder()
                        .id("my_field")
                        .fieldType(FieldType.INFO)
                        .valueType(ValueType.INTEGER)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.G).nullable(true).build())
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
                FieldImpl.builder()
                        .id("my_field")
                        .fieldType(FieldType.INFO)
                        .valueType(ValueType.FLOAT)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.R).nullable(true).build())
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
                FieldImpl.builder()
                        .id("my_field")
                        .fieldType(FieldType.INFO)
                        .valueType(ValueType.STRING)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.A).nullable(true).build())
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
                FieldImpl.builder()
                        .id("my_field")
                        .fieldType(FieldType.INFO)
                        .valueType(ValueType.STRING)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.VARIABLE).nullable(true).build())
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
                FieldImpl.builder()
                        .id("my_field")
                        .fieldType(FieldType.INFO)
                        .valueType(ValueType.CHARACTER)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.R).nullable(true).build())
                        .build(),
                vcfMetadata.getField(fieldId));
    }

    @Test
    void getFieldInfoFlag() {
        VCFInfoHeaderLine vcfInfoHeaderLine = mock(VCFInfoHeaderLine.class);
        when(vcfInfoHeaderLine.getType()).thenReturn(VCFHeaderLineType.Flag);
        when(vcfInfoHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.INTEGER);
        when(vcfInfoHeaderLine.getCount()).thenReturn(0);
        when(vcfHeader.getInfoHeaderLine("my_field")).thenReturn(vcfInfoHeaderLine);
        String fieldId = "INFO/my_field";
        assertEquals(
                FieldImpl.builder()
                        .id("my_field")
                        .fieldType(FieldType.INFO)
                        .valueType(ValueType.FLAG)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(0).build())
                        .build(),
                vcfMetadata.getField(fieldId));
    }

    @Test
    void getFieldInfoUnknown() {
        assertThrows(UnknownFieldException.class, () -> vcfMetadataStrict.getField("INFO/unknown"));
    }

    @Test
    void getFieldFormatInfo() {
        VCFFormatHeaderLine vcfFormatHeaderLine = mock(VCFFormatHeaderLine.class);
        when(vcfFormatHeaderLine.getType()).thenReturn(VCFHeaderLineType.Integer);
        when(vcfFormatHeaderLine.getCountType()).thenReturn(VCFHeaderLineCount.G);
        when(vcfHeader.getFormatHeaderLine("my_field")).thenReturn(vcfFormatHeaderLine);
        String fieldId = "FORMAT/my_field";
        assertEquals(
                FieldImpl.builder()
                        .id("my_field")
                        .fieldType(FieldType.FORMAT)
                        .valueType(ValueType.INTEGER)
                        .valueCount(ValueCount.builder().type(ValueCount.Type.G).nullable(true).build())
                        .build(),
                vcfMetadata.getField(fieldId));
    }

    @Test
    void getNestedFieldInfoUnknown() {
        Field actual = vcfMetadata.getField("INFO/VEP/unknown");
        assertEquals(new MissingField("unknown"), actual);
    }

    @Test
    void getNestedFieldInfoUnknownParent() {
        Field actual = vcfMetadata.getField("INFO/VOP/consequence");
        assertEquals(new MissingField("VOP"), actual);
    }

    @Test
    void getNestedFieldInfoUnknownStrict() {
        assertThrows(UnknownFieldException.class, () -> vcfMetadataStrict.getField("INFO/VEP/unknown"));
    }

    @Test
    void getNestedFieldInfoUnknownParentStrict() {
        assertThrows(UnsupportedNestedFieldException.class,
                () -> vcfMetadataStrict.getField("INFO/VOP/consequence"));
    }

    @Test
    void getNestedFieldInfoString() {
        String fieldId = "INFO/VEP/consequence";
        assertEquals(createNestedField("consequence"),
                vcfMetadata.getField(fieldId));
    }

    @Test
    void getFieldFormatUnknown() {
        assertEquals(new MissingField("unknown"), vcfMetadata.getField("FORMAT/unknown"));
    }

    @Test
    void getFieldFormatUnknownStrict() {
        assertThrows(UnknownFieldException.class, () -> vcfMetadataStrict.getField("FORMAT/unknown"));
    }

    @Test
    void getFieldSampleId() {
        Field field = FieldImpl.builder().id("ID").fieldType(FieldType.SAMPLE).valueType(ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/ID"));
    }

    @Test
    void getFieldSampleProband() {
        Field field = FieldImpl.builder().id("PROBAND").fieldType(FieldType.SAMPLE).valueType(ValueType.FLAG).valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/PROBAND"));
    }

    @Test
    void getFieldSampleAffectedStatus() {
        Field field = FieldImpl.builder().id("AFFECTED_STATUS").fieldType(FieldType.SAMPLE).valueType(ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/AFFECTED_STATUS"));
    }

    @Test
    void getFieldSampleSex() {
        Field field = FieldImpl.builder().id("SEX").fieldType(FieldType.SAMPLE).valueType(ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/SEX"));
    }

    @Test
    void getFieldSampleFatherId() {
        Field field = FieldImpl.builder().id("FATHER_ID").fieldType(FieldType.SAMPLE).valueType(ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/FATHER_ID"));
    }

    @Test
    void getFieldSampleMotherId() {
        Field field = FieldImpl.builder().id("MOTHER_ID").fieldType(FieldType.SAMPLE).valueType(ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/MOTHER_ID"));
    }

    @Test
    void getFieldSampleFamilyId() {
        Field field = FieldImpl.builder().id("FAMILY_ID").fieldType(FieldType.SAMPLE).valueType(ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.FIXED).count(1).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/FAMILY_ID"));
    }

    @Test
    void getFieldSamplePhenotypes() {
        Field field = FieldImpl.builder().id("PHENOTYPES").fieldType(FieldType.SAMPLE).valueType(ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.VARIABLE).nullable(true).build()).build();
        assertEquals(field, vcfMetadata.getField("SAMPLE/PHENOTYPES"));
    }

    @Test
    void getFieldSampleInvalid() {
        assertThrows(UnsupportedFieldException.class, () -> vcfMetadataStrict.getField("SAMPLE/unknown"));
    }

    @Test
    void unwrap() {
        assertEquals(vcfHeader, vcfMetadata.unwrap());
    }

    private NestedField createNestedField(String field) {
        ValueCount valueCount = ValueCount.builder().type(ValueCount.Type.VARIABLE).build();
        FieldImpl parent = FieldImpl.builder().id("VEP").fieldType(FieldType.INFO)
                .valueType(ValueType.STRING).valueCount(valueCount).separator('|').build();
        return NestedField.nestedBuilder().id(field).parent(parent).fieldType(FieldType.INFO_VEP)
                .valueType(ValueType.STRING).valueCount(valueCount).build();
    }
}
