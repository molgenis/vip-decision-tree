package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;

public class InvalidNumberOfTokensException extends RuntimeException {

  private static final String MESSAGE = "Unsupported number of tokens for input '%s' expecting %d tokens for fieldtype '%s'.";
  private final List<String> fieldTokens;
  private final FieldType type;
  private final int expected;

  public InvalidNumberOfTokensException(List<String> fieldTokens, FieldType type, int expected) {
    this.fieldTokens = fieldTokens;
    this.type = type;
    this.expected = expected;
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, String.join(",", fieldTokens), expected, type);
  }

}
