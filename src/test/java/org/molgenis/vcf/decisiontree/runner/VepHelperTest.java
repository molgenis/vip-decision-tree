package org.molgenis.vcf.decisiontree.runner;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.VcfRecord;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;

@ExtendWith(MockitoExtension.class)
class VepHelperTest {

  private VepHelper vepHelper;
  private NestedHeaderLine vepHeader;

  @BeforeEach
  void setUp() {
    vepHelper = new VepHelper();
    ValueCount valueCount = ValueCount.builder().type(Type.VARIABLE).build();
    FieldImpl parent = FieldImpl.builder().id("VEP").fieldType(
            FieldType.INFO)
        .valueType(ValueType.STRING).valueCount(valueCount).separator('|').build();
    NestedField nestedField1 = NestedField.nestedBuilder().id("ALLELE_NUM").parent(parent)
        .fieldType(FieldType.INFO_VEP)
        .valueType(ValueType.STRING).valueCount(valueCount).build();
    NestedField nestedField2 = NestedField.nestedBuilder().id("effect").parent(parent)
        .fieldType(FieldType.INFO_VEP)
        .valueType(ValueType.STRING).valueCount(valueCount).build();
    Map<String, NestedField> nestedFields = Map.of("field1", nestedField1, "ALLELE_NUM",
        nestedField2);
    vepHeader = NestedHeaderLine.builder().parentField(parent)
        .nestedFields(nestedFields).build();
  }

  @Test
  void getRecordPerConsequence() {
    VcfRecord record = mock(VcfRecord.class);
    when(record.getVepValues(vepHeader.getParentField())).thenReturn(List.of("1", "2"));
    VcfRecord filteredRecord1 = mock(VcfRecord.class, "filteredRecord1");
    when(record.getFilteredCopy("1", vepHeader.getParentField())).thenReturn(filteredRecord1);
    VcfRecord filteredRecord2 = mock(VcfRecord.class, "filteredRecord2");
    when(record.getFilteredCopy("2", vepHeader.getParentField())).thenReturn(filteredRecord2);
    assertEquals(Map.of(1, singletonList(filteredRecord1), 2, singletonList(filteredRecord2)),
        vepHelper.getRecordPerConsequence(record, vepHeader));
  }

  @Test
  void createEmptyCsqRecord() {
    VcfRecord record = mock(VcfRecord.class);
    VariantContext variantContext = mock(VariantContext.class);
    when(variantContext.getContig()).thenReturn("1");
    when(variantContext.getID()).thenReturn("1");
    when(variantContext.getAlleles()).thenReturn(
        List.of(Allele.REF_A, Allele.ALT_G, Allele.ALT_T));
    when(record.getVariantContext()).thenReturn(variantContext);
    assertEquals(List.of("0|"), vepHelper.createEmptyCsqRecord(record, 0, vepHeader).getVepValues(
        vepHeader.getParentField()));
  }
}