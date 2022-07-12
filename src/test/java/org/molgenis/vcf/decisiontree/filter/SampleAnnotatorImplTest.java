package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.VIPC_S;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

@ExtendWith(MockitoExtension.class)
class SampleAnnotatorImplTest {

  @Mock
  VariantContextBuilder vc;

  SampleAnnotator sampleAnnotator = new SampleAnnotatorImpl(false, false);

  @BeforeEach
  void setUp() {
  }

  @Test
  void annotate() {

    GenotypesContext genotypesContext = GenotypesContext.copy(GenotypesContext.NO_GENOTYPES);
    GenotypeBuilder gtBuilder = new GenotypeBuilder();
    gtBuilder.name("Patient");
    Genotype gt = gtBuilder.make();
    genotypesContext.add(gt);
    when(vc.getGenotypes()).thenReturn(genotypesContext);

    GenotypeBuilder expectedGt = new GenotypeBuilder(gt);
    expectedGt.attribute(VIPC_S, List.of("TEST"));
    Genotype expected = expectedGt.make();
    GenotypesContext expectedContext = GenotypesContext.copy(genotypesContext);
    expectedContext.replace(expected);

    Node node1 = mock(Node.class);
    Label label1 = mock(Label.class);
    sampleAnnotator.annotate(
        List.of(new Decision("TEST", List.of(node1), Set.of(label1))), 0, vc);
    ArgumentCaptor<GenotypesContext> captor = ArgumentCaptor.forClass(GenotypesContext.class);
    verify(vc).genotypes(captor.capture());
    assertEquals(List.of("TEST"), captor.getValue().get(0).getExtendedAttribute(VIPC_S));
  }
}