package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

class UnsupportedValueCountExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unsupported number of values (3) for field 'test' for decision type 'CATEGORICAL'.",
        new UnsupportedValueCountException(
            Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLOAT).valueCount(
                ValueCount.builder().type(Type.FIXED).count(3).build()).build(), DecisionType.CATEGORICAL).getMessage());
  }
}