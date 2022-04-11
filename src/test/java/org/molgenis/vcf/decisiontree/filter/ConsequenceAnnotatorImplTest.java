package org.molgenis.vcf.decisiontree.filter;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

@ExtendWith(MockitoExtension.class)
class ConsequenceAnnotatorImplTest {

  private String clazz;
  private Set<Label> labels;

  @BeforeEach
  void setUp() {
    clazz = "EXIT_NOW";
    Label label1 = Label.builder().id("id1").description("desc1").build();
    Label label2 = Label.builder().id("id2").description("desc2").build();
    labels = Set.of(label1, label2);
  }

  @Test
  void annotateLabelsPath() {
    Node node1 = mock(Node.class);
    when(node1.getId()).thenReturn("node1");
    Node node2 = mock(Node.class);
    when(node2.getId()).thenReturn("node2");
    Node node3 = mock(Node.class);
    when(node3.getId()).thenReturn("node3");
    List<Node> path = List.of(node1, node2, node3);
    Decision decision = Decision.builder().clazz(clazz).labels(labels).path(path).build();

    ConsequenceAnnotator annotator = new ConsequenceAnnotatorImpl(true, true);
    assertEquals("|||EXIT_NOW|node1&node2&node3|id1&id2", annotator.annotate(decision, "||"));
  }

  @Test
  void annotateLabels() {
    Decision decision = Decision.builder().clazz(clazz).labels(labels).path(emptyList()).build();
    ConsequenceAnnotator annotator = new ConsequenceAnnotatorImpl(true, false);
    assertEquals("|||EXIT_NOW|id1&id2", annotator.annotate(decision, "||"));
  }

  @Test
  void annotate() {
    Decision decision = Decision.builder().clazz(clazz).labels(labels).path(emptyList()).build();
    ConsequenceAnnotator annotator = new ConsequenceAnnotatorImpl(false, false);
    assertEquals("|||EXIT_NOW", annotator.annotate(decision, "||"));
  }
}