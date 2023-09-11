package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;

import java.io.Serial;

public class CountMismatchException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public CountMismatchException(ConfigBoolQuery query) {
    super(
        format("Query value for field '%s' should be a collection for query '%s'.", query.getField(), query));
  }
}
