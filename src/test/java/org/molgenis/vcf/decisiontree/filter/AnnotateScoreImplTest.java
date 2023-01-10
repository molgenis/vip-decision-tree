package org.molgenis.vcf.decisiontree.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;

@ExtendWith(MockitoExtension.class)
class AnnotateScoreImplTest {

  @Mock
  private VcfMetadata vcfMetadata;
  @Mock
  private VcfReader vcfReader;
  @Mock
  private RecordWriter recordWriter;
  @Mock
  private VipScoreAnnotator vipScoreAnnotator;
  @Mock
  private VepHelper vepHelper;

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
    classifier = new AnnotateScoreImpl(vepHelper, recordWriter, vcfMetadata, vipScoreAnnotator);
  }

//  @Test
//  void classify() {
//    VcfRecord record0 = mock(VcfRecord.class, "record0");
//    org.molgenis.vcf.decisiontree.filter.Allele allele0_1 = org.molgenis.vcf.decisiontree.filter.Allele.builder()
//            .bases("G").index(0).build();
//    when(record0.getAltAllele(0)).thenReturn(allele0_1);
//
//    when(record0.getNrAltAlleles()).thenReturn(1);
//    VcfRecord record1 = mock(VcfRecord.class, "record1");
//    when(record1.getNrAltAlleles()).thenReturn(2);
//    org.molgenis.vcf.decisiontree.filter.Allele allele1_1 = org.molgenis.vcf.decisiontree.filter.Allele.builder()
//            .bases("G").index(0).build();
//    org.molgenis.vcf.decisiontree.filter.Allele allele1_2 = org.molgenis.vcf.decisiontree.filter.Allele.builder()
//            .bases("T").index(1).build();
//    when(record1.getAltAllele(0)).thenReturn(allele1_1);
//    when(record1.getAltAllele(1)).thenReturn(allele1_2);
//
//    when(vcfReader.stream()).thenReturn(Stream.of(record0, record1));
//
//    VcfRecord record0a = mock(VcfRecord.class, "record0a");
//    when(record0a.getVepValues(parent)).thenReturn(List.of(""));
//    VcfRecord record1a = mock(VcfRecord.class, "record1a");
//    when(record1a.getVepValues(parent)).thenReturn(List.of(""));
//    VcfRecord record1b = mock(VcfRecord.class, "record1b");
//    when(record1b.getVepValues(parent)).thenReturn(List.of(""));
//    Map<Integer, List<VcfRecord>> recordMap0 = Map.of(1, List.of(record0a));
//    Map<Integer, List<VcfRecord>> recordMap1 = Map.of(1, List.of(record1a), 2,
//            List.of(record1b));
//    when(vepHelper.getRecordPerConsequence(record0,
//            nestedHeaderLine)).thenReturn(recordMap0);
//    when(vepHelper.getRecordPerConsequence(record1,
//            nestedHeaderLine)).thenReturn(recordMap1);
//
//    classifier.classify(vcfReader);
//
//    verify(vipScoreAnnotator).annotate(0, "");
//    verify(vipScoreAnnotator).annotate(0, "");
//    verify(vipScoreAnnotator).annotate(0, "");
//  }
}
