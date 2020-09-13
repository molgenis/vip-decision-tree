package org.molgenis.vcf.decisiontree.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.Variant.builder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

@ExtendWith(MockitoExtension.class)
class ClassifierImplTest {

  @Mock
  private DecisionTreeExecutor decisionTreeExecutor;
  private ClassifierImpl classifier;

  @BeforeEach
  void setUp() {
    classifier = new ClassifierImpl(decisionTreeExecutor);
  }

  @Test
  void classify() {
    VcfMetadata vcfMetadata = mock(VcfMetadata.class);

    VcfReader vcfReader = mock(VcfReader.class);
    VcfRecord record0 = when(mock(VcfRecord.class).getNrAltAllelles()).thenReturn(1).getMock();
    VcfRecord record1 = when(mock(VcfRecord.class).getNrAltAllelles()).thenReturn(2).getMock();
    when(vcfReader.stream()).thenReturn(Stream.of(record0, record1));
    when(vcfReader.getMetadata()).thenReturn(vcfMetadata);

    DecisionTree decisionTree = mock(DecisionTree.class);

    Variant variant0 = builder().vcfMetadata(vcfMetadata).vcfRecord(record0).alleleIndex(1).build();
    Decision decision0 = mock(Decision.class);
    doReturn(decision0).when(decisionTreeExecutor).execute(decisionTree, variant0);

    Variant variant1 = builder().vcfMetadata(vcfMetadata).vcfRecord(record1).alleleIndex(1).build();
    Decision decision1 = mock(Decision.class);
    doReturn(decision1).when(decisionTreeExecutor).execute(decisionTree, variant1);

    Variant variant2 = builder().vcfMetadata(vcfMetadata).vcfRecord(record1).alleleIndex(2).build();
    Decision decision2 = mock(Decision.class);
    doReturn(decision2).when(decisionTreeExecutor).execute(decisionTree, variant2);

    DecisionWriter writer = mock(DecisionWriter.class);
    classifier.classify(vcfReader, decisionTree, writer);

    verify(writer).write(Collections.singletonList(decision0), record0);
    verify(writer).write(List.of(decision1, decision2), record1);
  }
}
