package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;
import org.molgenis.vcf.decisiontree.filter.model.Node;

public class EvaluationException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public EvaluationException(Node node, Variant variant, String message) {
    super(
        format(
            "Error evaluating node '%s' for variant '%s': %s",
            node.getId(), variant.toDisplayString(), message));
  }
}
