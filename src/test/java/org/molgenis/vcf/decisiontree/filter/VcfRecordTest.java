package org.molgenis.vcf.decisiontree.filter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
class VcfRecordTest {

  @Mock
  private VariantContext variantContext;
  @Mock
  private VcfMetadata vcfMetadata;
  private VcfRecord vcfRecord;

  @BeforeEach
  void setUp() {
    vcfRecord = new VcfRecord(variantContext, vcfMetadata);
  }

  @Test
  void getNrAltAlleles() {
    when(variantContext.getNAlleles()).thenReturn(3);
    assertEquals(2, vcfRecord.getNrAltAlleles());
  }

  @Test
  void getAltAllele() {
    Allele allele = mock(Allele.class);
    when(allele.getBaseString()).thenReturn("ACTG");
    when(variantContext.getAlternateAllele(2)).thenReturn(allele);
    assertEquals("ACTG", vcfRecord.getAltAllele(2));
  }

  @Test
  void unwrap() {
    assertEquals(variantContext, vcfRecord.unwrap());
  }

  @Test
  void toDisplayString() {
    when(variantContext.getContig()).thenReturn("1");
    when(variantContext.getStart()).thenReturn(123);
    Allele refAllele = when(mock(Allele.class).getBaseString()).thenReturn("A").getMock();
    when(variantContext.getReference()).thenReturn(refAllele);
    assertEquals("1:123 A", vcfRecord.toDisplayString());
  }

  @Test
  void getValueCommonChrom() {
    String contig = "1";
    when(variantContext.getContig()).thenReturn(contig);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("#CHROM");
    assertEquals(contig, vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonPos() {
    int pos = 123;
    when(variantContext.getStart()).thenReturn(pos);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("POS");
    assertEquals(pos, vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonIds() {
    String idStr = "rs123;rs456";
    when(variantContext.hasID()).thenReturn(true);
    when(variantContext.getID()).thenReturn(idStr);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("ID");
    assertEquals(List.of("rs123", "rs456"), vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonIdsMissing() {
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("ID");
    assertEquals(emptyList(), vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonRef() {
    String ref = "ACTG";
    Allele allele = mock(Allele.class);
    when(allele.getBaseString()).thenReturn(ref);
    when(variantContext.getReference()).thenReturn(allele);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("REF");
    assertEquals(ref, vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonAlt() {
    String alt = "ACTG";
    Allele allele = mock(Allele.class);
    when(allele.getBaseString()).thenReturn(alt);
    when(variantContext.getAlternateAllele(0)).thenReturn(allele);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("ALT");
    assertEquals(alt, vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonQual() {
    when(variantContext.hasLog10PError()).thenReturn(true);
    when(variantContext.getPhredScaledQual()).thenReturn(1.23);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("QUAL");
    assertEquals(1.23, (Double) vcfRecord.getValue(field, 1), 1E-6);
  }

  @Test
  void getValueCommonQualMissing() {
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("QUAL");
    assertNull(vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonFilters() {
    Set<String> filters = new LinkedHashSet<>();
    filters.add("filter0");
    filters.add("filter1");
    when(variantContext.getFiltersMaybeNull()).thenReturn(filters);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("FILTER");
    assertEquals(new ArrayList<>(filters), vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonFiltersMissing() {
    when(variantContext.getFiltersMaybeNull()).thenReturn(null);
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("FILTER");
    assertEquals(emptyList(), vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueCommonFiltersPass() {
    when(variantContext.getFiltersMaybeNull()).thenReturn(emptySet());
    Field field = when(mock(Field.class).getFieldType()).thenReturn(FieldType.COMMON).getMock();
    when(field.getId()).thenReturn("FILTER");
    assertEquals(singletonList("PASS"), vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueInfoAltInteger() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.A).build())
            .valueType(ValueType.INTEGER)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2));
    assertEquals(2, vcfRecord.getValue(field, alleleIndex));
  }

  @Test
  void getValueInfoAltFloat() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.A).build())
            .valueType(ValueType.FLOAT)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1.2, 2.3));
    assertEquals(2.3, (Double) vcfRecord.getValue(field, alleleIndex), 1E-6);
  }

  @Test
  void getValueInfoAltString() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.A).build())
            .valueType(ValueType.STRING)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList("str0", "str1"));
    assertEquals("str1", vcfRecord.getValue(field, alleleIndex));
  }

  @Test
  void getValueInfoRef() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.R).build())
            .valueType(ValueType.INTEGER)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2, 3));
    assertEquals(3, vcfRecord.getValue(field, alleleIndex));
  }

  @Test
  void getValueInfoVariable() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.INTEGER)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2));
    assertEquals(asList(1, 2), vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueInfoVariableFlag() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.FLAG)
            .build();
    assertThrows(FlagListException.class, () -> vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueInfoFixed() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(2).build())
            .valueType(ValueType.INTEGER)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2));
    assertEquals(asList(1, 2), vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueInfoFixedOneInteger() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.INTEGER)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(1);
    assertEquals(1, vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueInfoFixedOneFloat() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.FLOAT)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(1.2);
    assertEquals(1.2, (Double) vcfRecord.getValue(field, 1), 1E-6);
  }

  @Test
  void getValueInfoFixedOneFlag() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.FLAG)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(true);
    assertTrue((Boolean) vcfRecord.getValue(field, 1));
  }

  @Test
  void getValueInfoFixedOneString() {
    Field field =
        Field.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.STRING)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn("str0");
    assertEquals("str0", vcfRecord.getValue(field, 1));
  }
}
