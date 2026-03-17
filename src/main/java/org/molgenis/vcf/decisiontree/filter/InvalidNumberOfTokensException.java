package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;
import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;

public class InvalidNumberOfTokensException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private final transient List<String> fieldTokens;
  private final transient FieldType type;
  private final int expected;

  public InvalidNumberOfTokensException(List<String> fieldTokens, FieldType type, int expected) {
    this.fieldTokens = fieldTokens;
    this.type = type;
    this.expected = expected;
  }

  @Override
  public String getMessage() {
    return format(
        "Unsupported number of tokens for input '%s' expecting %d tokens for fieldtype '%s'.",
        String.join(",", fieldTokens), expected, type);
  }
}
