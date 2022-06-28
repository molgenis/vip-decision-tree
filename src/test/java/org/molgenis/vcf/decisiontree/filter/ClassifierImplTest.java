package org.molgenis.vcf.decisiontree.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.molgenis.vcf.decisiontree.filter.model.SampleMeta;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.VepHeaderLine;

@ExtendWith(MockitoExtension.class)
class ClassifierImplTest {

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
  private ConsequenceAnnotator consequenceAnnotator;
  @Mock
  private VepHelper vepHelper;

  private Classifier classifier;
  private FieldImpl parent;
  private VepHeaderLine vepHeaderLine;

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
    vepHeaderLine = VepHeaderLine.builder().parentField(parent)
        .nestedFields(nestedFields).build();
    when(vcfMetadata.getVepHeaderLine()).thenReturn(vepHeaderLine);
    classifier = new ClassifierImpl(decisionTreeExecutor, vepHelper, decisionTree,
        consequenceAnnotator, recordWriter, vcfMetadata);
  }

  @Test
  void classify() {
    VcfRecord record0 = mock(VcfRecord.class, "record0");
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

    when(vcfReader.stream()).thenReturn(Stream.of(record0, record1));

    VcfRecord record0a = mock(VcfRecord.class, "record0a");
    when(record0a.getVepValues(parent)).thenReturn(List.of(""));
    VcfRecord record1a = mock(VcfRecord.class, "record1a");
    when(record1a.getVepValues(parent)).thenReturn(List.of(""));
    VcfRecord record1b = mock(VcfRecord.class, "record1b");
    when(record1b.getVepValues(parent)).thenReturn(List.of(""));
    Map<Integer, List<VcfRecord>> recordMap0 = Map.of(1, Collections.singletonList(record0a));
    Map<Integer, List<VcfRecord>> recordMap1 = Map.of(1, Collections.singletonList(record1a), 2,
        Collections.singletonList(record1b));
    when(vepHelper.getRecordPerConsequence(record0,
        vepHeaderLine)).thenReturn(recordMap0);
    when(vepHelper.getRecordPerConsequence(record1,
        vepHeaderLine)).thenReturn(recordMap1);
    Decision decision1a = Decision.builder().clazz("test1a").path(Collections.emptyList())
        .labels(Collections.emptySet()).build();
    Decision decision2a = Decision.builder().clazz("test2a").path(Collections.emptyList())
        .labels(Collections.emptySet()).build();
    Decision decision2b = Decision.builder().clazz("test2b").path(Collections.emptyList())
        .labels(Collections.emptySet()).build();

    doReturn(decision1a).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record0a, allele0_1));
    doReturn(decision2a).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record1a, allele1_1));
    doReturn(decision2b).when(decisionTreeExecutor)
        .execute(decisionTree, new Variant(vcfMetadata, record1b, allele1_2));

    classifier.classify(vcfReader);

    verify(consequenceAnnotator).annotate(decision1a, "");
    verify(consequenceAnnotator).annotate(decision2a, "");
    verify(consequenceAnnotator).annotate(decision2b, "");
  }
}
