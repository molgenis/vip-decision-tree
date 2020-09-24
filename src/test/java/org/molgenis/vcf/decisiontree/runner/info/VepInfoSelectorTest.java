package org.molgenis.vcf.decisiontree.runner.info;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoSelector.ALLELE_NUM;
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
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    nestedFields.put(PICK, pickField);
    assertTrue(vepInfoSelector.isMatch("1|1|Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void noMatch() {
    VariantContext vc = mock(VariantContext.class);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    nestedFields.put(PICK, pickField);
    assertFalse(vepInfoSelector.isMatch("2|1|Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void isMatchMulti() {
    when(pickField.getIndex()).thenReturn(1);
    VariantContext vc = mock(VariantContext.class);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    nestedFields.put(PICK, pickField);
    assertTrue(vepInfoSelector.isMatch("1|1|Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }

  @Test
  void isMatchNoPick() {
    VariantContext vc = mock(VariantContext.class);
    Allele allele = mock(Allele.class);
    Map<String, NestedField> nestedFields = new HashMap<>();
    nestedFields.put(ALLELE_NUM, alleleField);
    assertTrue(vepInfoSelector.isMatch("1|Y|Z",vc, 1, NestedInfoHeaderLine.builder().nestedFields(nestedFields).build()));
  }
}