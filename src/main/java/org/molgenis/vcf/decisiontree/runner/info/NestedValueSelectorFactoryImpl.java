package org.molgenis.vcf.decisiontree.runner.info;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.springframework.stereotype.Component;

@Component
public class NestedValueSelectorFactoryImpl implements NestedValueSelectorFactory{

  @Override
  public NestedValueSelector create(List<BoolQuery> selectorQueries, Character separator) {
    return new NestedValueSelector(selectorQueries,separator);
  }
}
