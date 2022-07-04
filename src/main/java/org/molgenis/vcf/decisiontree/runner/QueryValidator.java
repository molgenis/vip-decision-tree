package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;

public interface QueryValidator {

  void validateBooleanNode(ConfigBoolQuery configBoolQuery, Field field);

  void validateCategoricalNode(Field field);
}
