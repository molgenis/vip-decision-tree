package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

class UnsupportedMultiValueOperatorExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Cannot use operator 'NOT_EQUALS' on multi value field 'test'.",
        new UnsupportedMultiValueOperatorException(
            Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLOAT).valueCount(
                ValueCount.builder().type(Type.FIXED).count(3).build()).build(), ConfigOperator.NOT_EQUALS).getMessage());
  }
}