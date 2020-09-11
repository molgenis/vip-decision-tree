package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import htsjdk.variant.variantcontext.CommonInfo;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

@ExtendWith(MockitoExtension.class)
class DecisionWriterImplTest {

  @Mock
  private VariantContextWriter vcfWriter;
  private DecisionWriterImpl decisionWriter;

  @AfterEach
  void tearDown() {
    decisionWriter.close();
  }

  @Test
  void writeOne() {
    decisionWriter = new DecisionWriterImpl(vcfWriter);

    VariantContext variantContext = mock(VariantContext.class);
    // can't mock CommonInfo because it is final
    CommonInfo commonInfo = new CommonInfo(null, 0, null, null);
    when(variantContext.getCommonInfo()).thenReturn(commonInfo);

    String clazz = "my_class";
    Decision decision = Decision.builder().clazz(clazz).labels(Set.of()).path(List.of()).build();
    decisionWriter.write(List.of(decision), variantContext);
    verify(vcfWriter).add(variantContext);
    assertAll(
        () -> assertEquals(clazz, commonInfo.getAttribute("VIPC")),
        () -> assertFalse(commonInfo.hasAttribute("VIPL")),
        () -> assertFalse(commonInfo.hasAttribute("VIPP")));
  }

  @Test
  void writeOneLabelsAndPaths() {
    decisionWriter = new DecisionWriterImpl(vcfWriter, true, true);

    VariantContext variantContext = mock(VariantContext.class);
    // can't mock CommonInfo because it is final
    CommonInfo commonInfo = new CommonInfo(null, 0, null, null);
    when(variantContext.getCommonInfo()).thenReturn(commonInfo);

    Node node0 = when(mock(Node.class).getId()).thenReturn("node0").getMock();
    Node node1 = when(mock(Node.class).getId()).thenReturn("node1").getMock();
    Label label0 = when(mock(Label.class).getId()).thenReturn("label0").getMock();
    Label label1 = when(mock(Label.class).getId()).thenReturn("label1").getMock();

    String clazz = "my_class";
    Decision decision =
        Decision.builder()
            .clazz(clazz)
            .labels(new LinkedHashSet<>(List.of(label0, label1)))
            .path(List.of(node0, node1))
            .build();
    decisionWriter.write(List.of(decision), variantContext);
    verify(vcfWriter).add(variantContext);
    assertAll(
        () -> assertEquals(clazz, commonInfo.getAttribute("VIPC")),
        () -> assertEquals("label0|label1", commonInfo.getAttribute("VIPL")),
        () -> assertEquals("node0|node1", commonInfo.getAttribute("VIPP")));
  }

  @Test
  void writeOneLabelsAndPathsNoLabels() {
    decisionWriter = new DecisionWriterImpl(vcfWriter, true, true);

    VariantContext variantContext = mock(VariantContext.class);
    // can't mock CommonInfo because it is final
    CommonInfo commonInfo = new CommonInfo(null, 0, null, null);
    when(variantContext.getCommonInfo()).thenReturn(commonInfo);

    String clazz = "my_class";
    Decision decision = Decision.builder().clazz(clazz).labels(Set.of()).path(List.of()).build();
    decisionWriter.write(List.of(decision), variantContext);
    verify(vcfWriter).add(variantContext);
    assertAll(
        () -> assertEquals(clazz, commonInfo.getAttribute("VIPC")),
        () -> assertEquals(".", commonInfo.getAttribute("VIPL")),
        () -> assertEquals(".", commonInfo.getAttribute("VIPP")));
  }

  @Test
  void writeMultiple() {
    decisionWriter = new DecisionWriterImpl(vcfWriter);

    VariantContext variantContext = mock(VariantContext.class);
    // can't mock CommonInfo because it is final
    CommonInfo commonInfo = new CommonInfo(null, 0, null, null);
    when(variantContext.getCommonInfo()).thenReturn(commonInfo);

    String clazz0 = "class0";
    Decision decision0 = Decision.builder().clazz(clazz0).labels(Set.of()).path(List.of()).build();
    String clazz1 = "class1";
    Decision decision1 = Decision.builder().clazz(clazz1).labels(Set.of()).path(List.of()).build();
    decisionWriter.write(List.of(decision0, decision1), variantContext);
    verify(vcfWriter).add(variantContext);
    assertAll(
        () -> assertEquals("class0,class1", commonInfo.getAttribute("VIPC")),
        () -> assertFalse(commonInfo.hasAttribute("VIPL")),
        () -> assertFalse(commonInfo.hasAttribute("VIPP")));
  }

  @Test
  void writeMultipleLabelsAndPaths() {
    decisionWriter = new DecisionWriterImpl(vcfWriter, true, true);

    VariantContext variantContext = mock(VariantContext.class);
    // can't mock CommonInfo because it is final
    CommonInfo commonInfo = new CommonInfo(null, 0, null, null);
    when(variantContext.getCommonInfo()).thenReturn(commonInfo);

    Node node0 = when(mock(Node.class).getId()).thenReturn("node0").getMock();
    Node node1 = when(mock(Node.class).getId()).thenReturn("node1").getMock();
    Label label0 = when(mock(Label.class).getId()).thenReturn("label0").getMock();
    Label label1 = when(mock(Label.class).getId()).thenReturn("label1").getMock();

    String clazz0 = "class0";
    Decision decision0 =
        Decision.builder()
            .clazz(clazz0)
            .labels(new LinkedHashSet<>(List.of(label0, label1)))
            .path(List.of())
            .build();
    String clazz1 = "class1";
    Decision decision1 =
        Decision.builder().clazz(clazz1).labels(Set.of()).path(List.of(node0, node1)).build();
    decisionWriter.write(List.of(decision0, decision1), variantContext);
    verify(vcfWriter).add(variantContext);
    assertAll(
        () -> assertEquals("class0,class1", commonInfo.getAttribute("VIPC")),
        () -> assertEquals("label0|label1,.", commonInfo.getAttribute("VIPL")),
        () -> assertEquals(".,node0|node1", commonInfo.getAttribute("VIPP")));
  }
}
