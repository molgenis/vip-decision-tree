package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;

public class CountMismatchException extends RuntimeException {

  public CountMismatchException(ConfigBoolQuery query) {
    super(
        format("Query value for field '%s' should be a collection for query '%s'.", query.getField(), query.toString()));
  }
}
