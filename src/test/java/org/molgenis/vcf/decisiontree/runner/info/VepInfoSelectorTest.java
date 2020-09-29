package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.ALLELE;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.PICK;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

@ExtendWith(MockitoExtension.class)
class VepInfoSelectorTest {

  @Mock
  NestedField alleleField;
  @Mock NestedField pickField;

  private VepInfoSelector vepInfoSelector;

  @BeforeEach
  void setUp() {
    vepInfoSelector = new VepInfoSelector();
  }

  @Test
  void isMatch() {
    when(pickField.getIndex()).thenReturn(1);
    VariantContext vc = mock(VariantContext.class);
    Allele allele = mock(Allele.class);
    when(vc.getAlternateAlleles()).thenReturn(Collections.singletonList(allele));
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    nestedFields.put(PICK, pickField);
    assertTrue(vepInfoSelector.isMatch("A|1|Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void isMatchMulti() {
    when(pickField.getIndex()).thenReturn(1);
    VariantContext vc = mock(VariantContext.class);
    Allele allele = mock(Allele.class);
    Allele allele2 = mock(Allele.class);
    Allele ref = mock(Allele.class);
    when(allele.getBaseString()).thenReturn("A");
    when(ref.getBaseString()).thenReturn("A");
    when(vc.getAlternateAllele(0)).thenReturn(allele);
    when(vc.getAlternateAlleles()).thenReturn(Arrays.asList(allele, allele2));
    when(vc.getReference()).thenReturn(ref);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    nestedFields.put(PICK, pickField);
    assertTrue(vepInfoSelector.isMatch("A|1|Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void isMatchNoPick() {
    VariantContext vc = mock(VariantContext.class);
    Allele allele = mock(Allele.class);
    when(vc.getAlternateAlleles()).thenReturn(Collections.singletonList(allele));
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    assertTrue(vepInfoSelector.isMatch("A|Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void noMatchPick() {
    when(pickField.getIndex()).thenReturn(1);
    VariantContext vc = mock(VariantContext.class);
    Allele allele = mock(Allele.class);
    when(vc.getAlternateAlleles()).thenReturn(Collections.singletonList(allele));
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    nestedFields.put(PICK, pickField);
    assertFalse(vepInfoSelector.isMatch("A||Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));

  }

  @ParameterizedTest
  @CsvSource({
      "AT,A,-|1|Y|Z",
      "AAT,A,-|1|Y|Z",
      "AAT,AA,A|1|Y|Z",
  })
  void isMatchMultiDel(String reference, String alternative, String info) {
    when(pickField.getIndex()).thenReturn(1);
    VariantContext vc = mock(VariantContext.class);
    Allele allele = mock(Allele.class);
    Allele allele2 = mock(Allele.class);
    Allele ref = mock(Allele.class);
    when(allele.getBaseString()).thenReturn(alternative);
    when(ref.getBaseString()).thenReturn(reference);
    when(vc.getAlternateAllele(0)).thenReturn(allele);
    when(vc.getAlternateAlleles()).thenReturn(Arrays.asList(allele, allele2));
    when(vc.getReference()).thenReturn(ref);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE, alleleField);
    nestedFields.put(PICK, pickField);
    assertTrue(vepInfoSelector.isMatch(info,vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }
}