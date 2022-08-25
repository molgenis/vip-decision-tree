package org.molgenis.vcf.decisiontree.filter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.ped.model.Sex.MALE;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.ped.model.AffectedStatus;

@ExtendWith(MockitoExtension.class)
class VcfRecordTest {

  @Mock
  private VariantContext variantContext;
  private VcfRecord vcfRecord;
  private SampleContext sampleContext;

  @BeforeEach
  void setUp() {
    vcfRecord = new VcfRecord(variantContext);
    sampleContext = SampleContext.builder().index(0).proband(true).sex(MALE).fatherId("DAD")
        .motherId("MOM").familyId("FAM")
        .affectedStatus(AffectedStatus.AFFECTED).id("test").phenotypes(List.of("HP1", "HP2"))
        .build();
  }

  @Test
  void getNrAltAlleles() {
    when(variantContext.getNAlleles()).thenReturn(3);
    assertEquals(2, vcfRecord.getNrAltAlleles());
  }

  @Test
  void getAltAllele() {
    htsjdk.variant.variantcontext.Allele allele = mock(htsjdk.variant.variantcontext.Allele.class);
    when(allele.getBaseString()).thenReturn("ACTG");
    when(variantContext.getAlternateAllele(2)).thenReturn(allele);
    assertEquals(Allele.builder().bases("ACTG").index(3).build(), vcfRecord.getAltAllele(2));
  }

  @Test
  void unwrap() {
    assertEquals(variantContext, vcfRecord.unwrap());
  }

  @Test
  void toDisplayString() {
    when(variantContext.getContig()).thenReturn("1");
    when(variantContext.getStart()).thenReturn(123);
    htsjdk.variant.variantcontext.Allele refAllele =
        when(mock(htsjdk.variant.variantcontext.Allele.class).getBaseString())
            .thenReturn("A")
            .getMock();
    when(variantContext.getReference()).thenReturn(refAllele);
    assertEquals("1:123 A", vcfRecord.toDisplayString());
  }

  @Test
  void getValueCommonChrom() {
    String contig = "1";
    when(variantContext.getContig()).thenReturn(contig);
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("#CHROM");
    assertEquals(contig, vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonPos() {
    int pos = 123;
    when(variantContext.getStart()).thenReturn(pos);
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("POS");
    assertEquals(pos, vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonIds() {
    String idStr = "rs123;rs456";
    when(variantContext.hasID()).thenReturn(true);
    when(variantContext.getID()).thenReturn(idStr);
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("ID");
    assertEquals(List.of("rs123", "rs456"), vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonIdsMissing() {
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("ID");
    assertEquals(emptyList(), vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonRef() {
    String ref = "ACTG";
    htsjdk.variant.variantcontext.Allele allele = mock(htsjdk.variant.variantcontext.Allele.class);
    when(allele.getBaseString()).thenReturn(ref);
    when(variantContext.getReference()).thenReturn(allele);
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("REF");
    assertEquals(ref, vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonAlt() {
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("ALT");
    assertEquals("A", vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonQual() {
    when(variantContext.hasLog10PError()).thenReturn(true);
    when(variantContext.getPhredScaledQual()).thenReturn(1.23);
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("QUAL");
    assertEquals(1.23, (Double) vcfRecord.getValue(field, createAllele()), 1E-6);
  }

  @Test
  void getValueCommonQualMissing() {
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("QUAL");
    assertNull(vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonFilters() {
    Set<String> filters = new LinkedHashSet<>();
    filters.add("filter0");
    filters.add("filter1");
    when(variantContext.getFiltersMaybeNull()).thenReturn(filters);
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("FILTER");
    assertEquals(new ArrayList<>(filters), vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonFiltersMissing() {
    when(variantContext.getFiltersMaybeNull()).thenReturn(null);
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("FILTER");
    assertEquals(emptyList(), vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueCommonFiltersPass() {
    when(variantContext.getFiltersMaybeNull()).thenReturn(emptySet());
    FieldImpl field = when(mock(FieldImpl.class).getFieldType()).thenReturn(FieldType.COMMON)
        .getMock();
    when(field.getId()).thenReturn("FILTER");
    assertEquals(List.of("PASS"), vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueInfoAltInteger() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.A).build())
            .valueType(ValueType.INTEGER)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2));
    assertEquals(2, vcfRecord.getValue(field, createAllele(alleleIndex)));
  }

  @Test
  void getValueInfoAltFloat() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.A).build())
            .valueType(ValueType.FLOAT)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1.2, 2.3));
    assertEquals(2.3, (Double) vcfRecord.getValue(field, createAllele(alleleIndex)), 1E-6);
  }

  @Test
  void getValueInfoAltString() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.A).build())
            .valueType(ValueType.STRING)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList("str0", "str1"));
    assertEquals("str1", vcfRecord.getValue(field, createAllele(alleleIndex)));
  }

  @Test
  void getValueInfoRef() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.R).build())
            .valueType(ValueType.INTEGER)
            .build();
    int alleleIndex = 2;
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2, 3));
    assertEquals(3, vcfRecord.getValue(field, createAllele(alleleIndex)));
  }

  @Test
  void getValueInfoVariable() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.INTEGER)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2));
    assertEquals(asList(1, 2), vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueInfoVariableFlag() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.FLAG)
            .build();
    Allele allele = createAllele();
    assertThrows(FlagListException.class, () -> vcfRecord.getValue(field, allele));
  }

  @Test
  void getValueInfoFixed() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(2).build())
            .valueType(ValueType.INTEGER)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(asList(1, 2));
    assertEquals(asList(1, 2), vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueInfoFixedOneInteger() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.INTEGER)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(1);
    assertEquals(1, vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueInfoFixedOneFloat() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.FLOAT)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(1.2);
    assertEquals(1.2, (Double) vcfRecord.getValue(field, createAllele()), 1E-6);
  }

  @Test
  void getValueInfoFixedOneFlag() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.FLAG)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn(true);
    assertTrue((Boolean) vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueInfoFixedOneString() {
    FieldImpl field =
        FieldImpl.builder()
            .id("my_field")
            .fieldType(FieldType.INFO)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .valueType(ValueType.STRING)
            .build();
    when(variantContext.getAttribute("my_field")).thenReturn("str0");
    assertEquals("str0", vcfRecord.getValue(field, createAllele()));
  }

  @Test
  void getValueFormatAD() {
    FieldImpl field =
        FieldImpl.builder()
            .id("AD")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getAD()).thenReturn(new int[]{10, 10});
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(List.of(Integer.valueOf(10), Integer.valueOf(10)),
        vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatDP() {
    FieldImpl field =
        FieldImpl.builder()
            .id("DP")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getDP()).thenReturn(10);
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(10, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatGT() {
    FieldImpl field =
        FieldImpl.builder()
            .id("GT")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getAlleles()).thenReturn(List.of(htsjdk.variant.variantcontext.Allele.ALT_T,
        htsjdk.variant.variantcontext.Allele.REF_A));
    when(gt.isPhased()).thenReturn(true);
    when(variantContext.getAlleles()).thenReturn(List.of(htsjdk.variant.variantcontext.Allele.REF_A,
        htsjdk.variant.variantcontext.Allele.ALT_G,
        htsjdk.variant.variantcontext.Allele.ALT_T));
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals("2|0", vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatGQ() {
    FieldImpl field =
        FieldImpl.builder()
            .id("GQ")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getGQ()).thenReturn(10);
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(10, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatPL() {
    FieldImpl field =
        FieldImpl.builder()
            .id("PL")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getPL()).thenReturn(new int[]{10, 10});
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertTrue(java.util.Arrays.equals(new int[]{10, 10},
        (int[]) vcfRecord.getValue(field, createAllele(), sampleContext)));
  }

  @Test
  void getValueFormatCustom() {
    FieldImpl field =
        FieldImpl.builder()
            .id("test")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getExtendedAttribute("test")).thenReturn("testValue");
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals("testValue", vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatCustomInt() {
    FieldImpl field =
        FieldImpl.builder()
            .id("test")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.INTEGER)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getExtendedAttribute("test")).thenReturn("1");
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(1, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatCustomException() {
    FieldImpl field =
        FieldImpl.builder()
            .id("test")
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.INTEGER)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getExtendedAttribute("test")).thenReturn(List.of(1, 2));
    when(variantContext.getGenotype(0)).thenReturn(gt);
    Allele allele = createAllele();
    assertThrows(UnsupportedFormatFieldException.class,
        () -> vcfRecord.getValue(field, allele, sampleContext));
  }

  @Test
  void getValueFormatGenotype() {
    Field parent = FieldImpl.builder()
        .id("GENOTYPE")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    NestedField field = NestedField.nestedBuilder()
        .parent(parent)
        .id("ALLELES")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.INTEGER)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getAlleles()).thenReturn(List.of(htsjdk.variant.variantcontext.Allele.ALT_T,
        htsjdk.variant.variantcontext.Allele.REF_A));
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(List.of("T", "A"), vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatGenotypeNum() {
    Field parent = FieldImpl.builder()
        .id("GENOTYPE")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    NestedField field = NestedField.nestedBuilder()
        .parent(parent)
        .id("ALLELE_NUM")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.INTEGER)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getAlleles()).thenReturn(List.of(htsjdk.variant.variantcontext.Allele.ALT_T,
        htsjdk.variant.variantcontext.Allele.REF_A));
    when(variantContext.getGenotype(0)).thenReturn(gt);
    when(variantContext.getAlleles()).thenReturn(List.of(htsjdk.variant.variantcontext.Allele.REF_A,
        htsjdk.variant.variantcontext.Allele.ALT_G,
        htsjdk.variant.variantcontext.Allele.ALT_T));
    assertEquals(List.of(2, 0), vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @ParameterizedTest
  @CsvSource({"NO_CALL", "HOM_REF", "HET", "HOM_VAR", "UNAVAILABLE", "MIXED"})
  void getValueFormatGenotypeType(String argument) {
    Field parent = FieldImpl.builder()
        .id("GENOTYPE")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    NestedField field = NestedField.nestedBuilder()
        .parent(parent)
        .id("TYPE")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.INTEGER)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    Genotype gt = mock(Genotype.class);
    when(gt.getType()).thenReturn(GenotypeType.valueOf(argument));
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(argument, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @ParameterizedTest
  @CsvSource({"CALLED, false", "NON_INFORMATIVE, false", "PHASED, false"})
  void getValueFormatGenotype(String fieldName, boolean expected) {
    Field parent = FieldImpl.builder()
        .id("GENOTYPE")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    NestedField field = NestedField.nestedBuilder()
        .parent(parent)
        .id(fieldName)
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.FLAG)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    Genotype gt = mock(Genotype.class);
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(expected, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueFormatGenotypePloidy() {
    Field parent = FieldImpl.builder()
        .id("GENOTYPE")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.STRING)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    NestedField field = NestedField.nestedBuilder()
        .parent(parent)
        .id("PLOIDY")
        .fieldType(FieldType.GENOTYPE)
        .valueType(ValueType.INTEGER)
        .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
        .build();
    Genotype gt = mock(Genotype.class);
    when(variantContext.getGenotype(0)).thenReturn(gt);
    assertEquals(0, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueSampleProband() {
    FieldImpl field =
        FieldImpl.builder()
            .id("PROBAND")
            .fieldType(FieldType.SAMPLE)
            .valueType(ValueType.FLAG)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    assertEquals(true, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @ParameterizedTest
  @CsvSource({"SEX,MALE", "FATHER,DAD", "MOTHER,MOM", "FAMILY,FAM"})
  void getValueSample(String fieldName, String expected) {
    FieldImpl field =
        FieldImpl.builder()
            .id(fieldName)
            .fieldType(FieldType.SAMPLE)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.FIXED).count(1).build())
            .build();
    Genotype gt = mock(Genotype.class);
    assertEquals(expected, vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  @Test
  void getValueSamplePhenotypes() {
    FieldImpl field =
        FieldImpl.builder()
            .id("PHENOTYPES")
            .fieldType(FieldType.SAMPLE)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .build();
    Genotype gt = mock(Genotype.class);
    assertEquals(List.of("HP1", "HP2"), vcfRecord.getValue(field, createAllele(), sampleContext));
  }

  private Allele createAllele() {
    return createAllele(1);
  }

  private Allele createAllele(int alleleIndex) {
    return Allele.builder().bases("A").index(alleleIndex).build();
  }
}
