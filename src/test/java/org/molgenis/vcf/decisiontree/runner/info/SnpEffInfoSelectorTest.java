package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.ALLELE;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

@ExtendWith(MockitoExtension.class)
class SnpEffInfoSelectorTest {

  @Mock
  NestedField alleleField;

  private SnpEffInfoSelector snpEffInfoSelector;

  @BeforeEach
  void setUp() {
    snpEffInfoSelector = new SnpEffInfoSelector();
  }

  @Test
  void isMatch() {
    VariantContext vc = mock(VariantContext.class);
    when(alleleField.getIndex()).thenReturn(0);
    Allele allele = mock(Allele.class);
    when(allele.getBaseString()).thenReturn("A");
    when(vc.getAlternateAllele(0)).thenReturn(allele);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    assertTrue(snpEffInfoSelector.isMatch("A|X|Y", vc, 1,
        NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void noMatch() {
    VariantContext vc = mock(VariantContext.class);
    when(alleleField.getIndex()).thenReturn(0);
    Allele allele = mock(Allele.class);
    when(allele.getBaseString()).thenReturn("A");
    when(vc.getAlternateAllele(0)).thenReturn(allele);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    assertFalse(snpEffInfoSelector.isMatch("T|X|Y", vc, 1,
        NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }
}