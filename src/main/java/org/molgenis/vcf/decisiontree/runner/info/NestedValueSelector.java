package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.utils.QueryUtil;
import org.molgenis.vcf.decisiontree.utils.VcfUtils;

public class NestedValueSelector {

  public static final String SELECTED_ALLELE = "selectedAllele";
  public static final String SELECTED_ALLELE_INDEX = "selectedAlleleIndex";
  private final List<BoolQuery> queries;
  private final Character separator;

  public NestedValueSelector(
      List<BoolQuery> queries, Character separator) {
    this.queries = requireNonNull(queries);
    this.separator = requireNonNull(separator);
  }

  public Object select(Field field, List<String> infoValues, Allele allele) {
    List<String[]> filteredInfoValues = infoValues.stream()
        .map(infoValue -> infoValue.split(Pattern.quote(separator.toString()),-1))
        .filter(infoValue -> isAllSelectorsMatch(infoValue, allele))
        .collect(Collectors.toList());
    return getTypedFieldValue(field, filteredInfoValues);
  }

  private Object getTypedFieldValue(Field field, List<String[]> filteredInfoValues) {
    if (filteredInfoValues.isEmpty()) {
      return null;
    } else {
      String[] filteredInfo = filteredInfoValues.get(0);
      String stringValue = filteredInfo[field.getIndex()];
      if(stringValue == null || stringValue.isEmpty()){
        return null;
      }
      return VcfUtils.getTypedInfoValue(field, stringValue);
    }
  }

  private boolean isAllSelectorsMatch(String[] infoValue, Allele allele) {
    for (BoolQuery query : queries) {
      if (!isSingleSelectorMatch(infoValue, query, allele)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isSingleSelectorMatch(String[] infoValue,
      BoolQuery query, Allele allele) {
    boolean matched = false;
    query = preprocessQuery(query, allele);
    Integer index = query.getField().getIndex();
    if (index == null) {
      throw new MissingNestedInfoFieldException("index");
    }
    String value = infoValue[index];
    if (!value.isEmpty()) {
      Object typedValue = VcfUtils.getTypedInfoValue(query.getField(), value);
      matched = QueryUtil.executeQuery(query, typedValue);
    }
    return matched;
  }

  private static BoolQuery preprocessQuery(BoolQuery query, Allele allele) {
    if (query.getValue() == SELECTED_ALLELE) {
      query = toAlleleQuery(allele, query);
    }
    if (query.getValue() == SELECTED_ALLELE_INDEX) {
      query = toAlleleIndexQuery(allele, query);
    }
    return query;
  }

  private static BoolQuery toAlleleQuery(Allele allele, BoolQuery query) {
    return BoolQuery.builder().field(query.getField()).operator(query.getOperator())
        .value(allele.getBases()).build();
  }

  private static BoolQuery toAlleleIndexQuery(Allele allele, BoolQuery query) {
    return BoolQuery.builder().field(query.getField()).operator(query.getOperator())
        .value(allele.getIndex()).build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NestedValueSelector that = (NestedValueSelector) o;
    return queries.equals(that.queries) &&
        separator.equals(that.separator);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queries, separator);
  }
}
