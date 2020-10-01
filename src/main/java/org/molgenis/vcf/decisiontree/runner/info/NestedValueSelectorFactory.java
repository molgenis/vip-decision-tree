package org.molgenis.vcf.decisiontree.runner.info;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;

public interface NestedValueSelectorFactory {
  NestedValueSelector create(List<BoolQuery> selectorQueries, Character separator);
}
