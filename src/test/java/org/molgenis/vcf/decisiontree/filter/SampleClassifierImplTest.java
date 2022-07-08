package org.molgenis.vcf.decisiontree.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;

@ExtendWith(MockitoExtension.class)
class SampleClassifierImplTest {

  @Mock
  private DecisionTreeExecutor decisionTreeExecutor;
  @Mock
  private VcfMetadata vcfMetadata;
  @Mock
  private VcfReader vcfReader;
  @Mock
  private DecisionTree decisionTree;
  @Mock
  private RecordWriter recordWriter;
  @Mock
  private VepHelper vepHelper;
  @Mock
  private SampleAnnotator sampleAnnotator;


  private Classifier classifier;
  private FieldImpl parent;
  private NestedHeaderLine nestedHeaderLine;

  @BeforeEach
  void setUp() {
    ValueCount valueCount = ValueCount.builder().type(Type.VARIABLE).build();
    parent = FieldImpl.builder().id("VEP").fieldType(FieldType.INFO)
        .valueType(ValueType.STRING).valueCount(valueCount).separator('|').build();
    NestedField nestedField1 = NestedField.nestedBuilder().id("ALLELE_NUM").parent(parent)
        .fieldType(FieldType.INFO_VEP)
        .valueType(ValueType.STRING).valueCount(valueCount).build();
    NestedField nestedField2 = NestedField.nestedBuilder().id("effect").parent(parent)
        .fieldType(FieldType.INFO_VEP)
        .valueType(ValueType.STRING).valueCount(valueCount).build();
    Map<String, NestedField> nestedFields = Map.of("field1", nestedField1, "ALLELE_NUM",
        nestedField2);
    nestedHeaderLine = NestedHeaderLine.builder().parentField(parent)
        .nestedFields(nestedFields).build();
    when(vcfMetadata.getVepHeaderLine()).thenReturn(nestedHeaderLine);
    classifier = new SampleClassifierImpl(decisionTreeExecutor, vepHelper, decisionTree,
        recordWriter, sampleAnnotator, Set.of());
  }

  @Test
  void classify() {
    VcfRecord record0 = mock(VcfRecord.class, "record0");
    VariantContext vc0 = mock(VariantContext.class, "vc0");

    GenotypesContext genotypesContext = GenotypesContext.copy(GenotypesContext.NO_GENOTYPES);
    Genotype gt0a = new GenotypeBuilder().name("Patient").make();
    genotypesContext.add(gt0a);
    Genotype gt0b = new GenotypeBuilder().name("Patient2").make();
    genotypesContext.add(gt0b);
    when(vc0.getGenotypes()).thenReturn(genotypesContext);
    when(vc0.getContig()).thenReturn("1");
    when(vc0.getID()).thenReturn("1");
    when(vc0.getStart()).thenReturn(1);
    when(vc0.getEnd()).thenReturn(1);
    when(vc0.getNSamples()).thenReturn(2);
    when(vc0.getGenotype(0)).thenReturn(gt0a);
    when(vc0.getGenotype(1)).thenReturn(gt0b);
    when(vc0.getAlleles()).thenReturn(List.of(
        htsjdk.variant.variantcontext.Allele.REF_A, htsjdk.variant.variantcontext.Allele.ALT_T));

    VariantContext vc1 = mock(VariantContext.class, "vc1");
    GenotypesContext genotypesContext1 = GenotypesContext.copy(GenotypesContext.NO_GENOTYPES);
    Genotype gt1a = new GenotypeBuilder().name("Patient").make();
    genotypesContext.add(gt1a);
    Genotype gt1b = new GenotypeBuilder().name("Patient2").make();
    genotypesContext.add(gt1b);
    when(vc1.getGenotypes()).thenReturn(genotypesContext1);
    when(vc1.getContig()).thenReturn("1");
    when(vc1.getID()).thenReturn("1");
    when(vc1.getStart()).thenReturn(1);
    when(vc1.getEnd()).thenReturn(1);
    when(vc1.getNSamples()).thenReturn(2);
    when(vc1.getGenotype(0)).thenReturn(gt1a);
    when(vc1.getGenotype(1)).thenReturn(gt1b);
    when(vc1.getAlleles()).thenReturn(List.of(
        htsjdk.variant.variantcontext.Allele.REF_A, htsjdk.variant.variantcontext.Allele.ALT_T));

    org.molgenis.vcf.decisiontree.filter.Allele allele0_1 = org.molgenis.vcf.decisiontree.filter.Allele.builder()
        .bases("G").index(0).build();
    when(record0.getAltAllele(0)).thenReturn(allele0_1);

    when(record0.getNrAltAlleles()).thenReturn(1);
    VcfRecord record1 = mock(VcfRecord.class, "record1");
    when(record1.getNrAltAlleles()).thenReturn(2);
    org.molgenis.vcf.decisiontree.filter.Allele allele1_1 = org.molgenis.vcf.decisiontree.filter.Allele.builder()
        .bases("G").index(0).build();
    org.molgenis.vcf.decisiontree.filter.Allele allele1_2 = org.molgenis.vcf.decisiontree.filter.Allele.builder()
        .bases("T").index(1).build();
    when(record1.getAltAllele(0)).thenReturn(allele1_1);
    when(record1.getAltAllele(1)).thenReturn(allele1_2);

    when(record0.getVariantContext()).thenReturn(vc0);
    when(record1.getVariantContext()).thenReturn(vc1);

    when(vcfReader.stream()).thenReturn(Stream.of(record0, record1));
    when(vcfReader.getMetadata()).thenReturn(vcfMetadata);

    VcfRecord record0a = mock(VcfRecord.class, "record0a");
    VcfRecord record1a = mock(VcfRecord.class, "record1a");
    VcfRecord record1b = mock(VcfRecord.class, "record1b");
    Map<Integer, List<VcfRecord>> recordMap0 = Map.of(1, List.of(record0a));
    Map<Integer, List<VcfRecord>> recordMap1 = Map.of(1, List.of(record1a), 2,
        List.of(record1b));
    when(vepHelper.getRecordPerConsequence(record0,
        nestedHeaderLine)).thenReturn(recordMap0);
    when(vepHelper.getRecordPerConsequence(record1,
        nestedHeaderLine)).thenReturn(recordMap1);
    Decision decision1a = Decision.builder().clazz("test1a").path(List.of())
        .labels(Set.of()).build();
    Decision decision2a = Decision.builder().clazz("test2a").path(List.of())
        .labels(Set.of()).build();
    Decision decision2b = Decision.builder().clazz("test2b").path(List.of())
        .labels(Set.of()).build();

    doReturn(decision1a).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record0a, allele0_1),
            0);
    doReturn(decision2a).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record1a, allele1_1),
            0);
    doReturn(decision2b).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record1b, allele1_2),
            0);
    doReturn(decision1a).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record0a, allele0_1),
            1);
    doReturn(decision2a).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record1a, allele1_1),
            1);
    doReturn(decision2b).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record1b, allele1_2),
            1);

    doReturn(vc0).when(sampleAnnotator)
        .annotate(List.of(new Decision("test1a", List.of(), Set.of())),
            0, vc0);
    doReturn(vc0).when(sampleAnnotator)
        .annotate(List.of(new Decision("test1a", List.of(), Set.of())),
            1, vc0);
    doReturn(vc1).when(sampleAnnotator).annotate(
        List.of(new Decision("test2a", List.of(), Set.of()),
            new Decision("test2b", List.of(), Set.of())), 0,
        vc1);
    doReturn(vc1).when(sampleAnnotator).annotate(
        List.of(new Decision("test2a", List.of(), Set.of()),
            new Decision("test2b", List.of(), Set.of())), 1,
        vc1);

    classifier.classify(vcfReader);

    verify(sampleAnnotator).annotate(
        List.of(new Decision("test1a", List.of(), Set.of())), 0,
        vc0);
    verify(sampleAnnotator).annotate(
        List.of(new Decision("test1a", List.of(), Set.of())),
        1, vc0);
    verify(sampleAnnotator).annotate(
        List.of(new Decision("test2a", List.of(), Set.of()),
            new Decision("test2b", List.of(), Set.of())), 0,
        vc1);
    verify(sampleAnnotator).annotate(
        List.of(new Decision("test2a", List.of(), Set.of()),
            new Decision("test2b", List.of(), Set.of())), 1,
        vc1);
  }
}
