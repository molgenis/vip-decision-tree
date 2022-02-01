package org.molgenis.vcf.decisiontree.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.Variant.builder;
import static org.molgenis.vcf.decisiontree.runner.info.VepInfoMetadataMapper.ALLELE_NUM;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.runner.info.VepHeaderLine;

@ExtendWith(MockitoExtension.class)
class ClassifierImplTest {

  @Mock
  private DecisionTreeExecutor decisionTreeExecutor;
  private ClassifierImpl classifier;

  @BeforeEach
  void setUp() {
    classifier = new ClassifierImpl(decisionTreeExecutor);
  }

  @Test
  void classify() {
//FIXME
  }
}
