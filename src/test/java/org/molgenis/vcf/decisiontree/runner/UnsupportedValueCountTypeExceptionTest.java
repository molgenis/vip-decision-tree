package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

class UnsupportedValueCountTypeExceptionTest {
  @Test
  void getMessage() {
    assertEquals(
        "Unsupported value count type 'G' of field 'test' for combination of decision type 'CATEGORICAL' and operator of type 'IN'.",
        new UnsupportedValueCountTypeException(
            FieldImpl.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLOAT)
                .valueCount(
                    ValueCount.builder().type(Type.G).build()).build(), DecisionType.CATEGORICAL, ConfigOperator.IN).getMessage());
  }
}