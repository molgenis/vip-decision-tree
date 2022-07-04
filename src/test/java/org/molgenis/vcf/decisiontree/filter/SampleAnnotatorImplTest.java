package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl.VISD;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

@ExtendWith(MockitoExtension.class)
class SampleAnnotatorImplTest {

  @Mock
  VariantContext vc;

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
    when(vc.getContig()).thenReturn("1");
    when(vc.getID()).thenReturn("1");
    when(vc.getStart()).thenReturn(1);
    when(vc.getEnd()).thenReturn(1);
    when(vc.getAlleles()).thenReturn(List.of(Allele.REF_A, Allele.ALT_T));

    GenotypeBuilder expectedGt = new GenotypeBuilder(gt);
    expectedGt.attribute(VISD, List.of("TEST"));
    Genotype expected = expectedGt.make();

    Node node1 = mock(Node.class);
    Label label1 = mock(Label.class);
    VariantContext result = sampleAnnotator.annotate(
        List.of(new Decision("TEST", List.of(node1), Set.of(label1))), 0, vc);
    assertEquals(expected.getExtendedAttribute(VISD),
        result.getGenotype("Patient").getExtendedAttribute(VISD));
  }
}