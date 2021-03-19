package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

class UnsupportedValueTypeExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unsupported value type 'FLOAT' of field 'test' for decision type 'CATEGORICAL'.",
        new UnsupportedValueTypeException(
            FieldImpl.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLOAT)
                .valueCount(
                    ValueCount.builder().type(Type.A).build()).build(), DecisionType.CATEGORICAL)
            .getMessage());
  }
}