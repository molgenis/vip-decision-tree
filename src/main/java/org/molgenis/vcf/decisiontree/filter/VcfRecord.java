package org.molgenis.vcf.decisiontree.filter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.getTypedInfoValue;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.utils.VcfUtils;

/**
 * {@link VariantContext} wrapper that works with nested data (e.g. CSQ INFO fields)..
 */
public class VcfRecord {

  private static final List<String> PASS_FILTER = singletonList(VCFConstants.PASSES_FILTERS_v4);
  public static final String SELECTED_ALLELE = "SELECTED_ALLELE";

  private final VariantContext variantContext;

  public VcfRecord(VariantContext variantContext) {
    this.variantContext = requireNonNull(variantContext);
  }

  public int getNrAltAlleles() {
    return variantContext.getNAlleles() - 1;
  }

  public String getAltAllele(int altAlleleIndex) {
    return variantContext.getAlternateAllele(altAlleleIndex).getBaseString();
  }

  public Object getValue(Field field, int alleleIndex) {
    Object value;
    FieldType fieldType = field.getFieldType();
    switch (fieldType) {
      case COMMON:
        value = getCommonValue(field, alleleIndex);
        break;
      case INFO:
        value = getInfoValue(field, alleleIndex);
        break;
      case INFO_NESTED:
        value = getNestedValue(field, alleleIndex);
        break;
      case FORMAT:
        throw new UnsupportedOperationException("FORMAT values are not yet supported."); // TODO
      default:
        throw new UnexpectedEnumException(fieldType);
    }
    return value;
  }

  private Object getNestedValue(Field field, int alleleIndex) {
    Object value = null;
    NestedField nestedField = (NestedField) field;
    String separator = Pattern.quote(nestedField.getParent().getSeparator().toString());
    int index = nestedField.getIndex();
    String infoId = nestedField.getParent().getId();
    List<String> infoValues = VcfUtils.getInfoAsStringList(variantContext, infoId);
    if (!infoValues.isEmpty()) {
      List<String> filteredInfo = infoValues.stream()
          .filter(infoValue -> matchedSelector(infoValue, nestedField, alleleIndex)).collect(
              Collectors.toList());
      if (!filteredInfo.isEmpty()) {
        String singleValue = filteredInfo.get(0);
        String[] split = singleValue.split(separator);
        String stringValue = split[index];
        if (!stringValue.isEmpty()) {
          value = getTypedInfoValue(field, stringValue);
        }
      }
    }
    return value;
  }

  private boolean matchedSelector(String infoValue, NestedField nestedField, int alleleIndex) {
    String separator = Pattern.quote(nestedField.getParent().getSeparator().toString());
    Map<NestedField, Object> selectors = nestedField.getSelectors();
    String[] split = infoValue.split(separator);
    if (selectors != null) {
      for (Entry<NestedField, Object> selector : selectors.entrySet()) {
        Object value = selector.getValue();
        if (value.equals(SELECTED_ALLELE)) {
          value = variantContext.getAlternateAllele(alleleIndex - 1).getBaseString();
        }
        if (!getTypedInfoValue(selector.getKey(), split[selector.getKey().getIndex()])
            .equals(value)) {
          return false;
        }
      }
    }
    return true;
  }

  private Object getCommonValue(Field field, int alleleIndex) {
    Object value;
    switch (field.getId()) {
      case "#CHROM":
        value = variantContext.getContig();
        break;
      case "POS":
        value = variantContext.getStart();
        break;
      case "ID":
        value = variantContext.hasID() ? asList(variantContext.getID().split(";")) : emptyList();
        break;
      case "REF":
        value = variantContext.getReference().getBaseString();
        break;
      case "ALT":
        value = getAltAllele(alleleIndex - 1);
        break;
      case "QUAL":
        value = variantContext.hasLog10PError() ? variantContext.getPhredScaledQual() : null;
        break;
      case "FILTER":
        value = getCommonFilterValue();
        break;
      default:
        throw new UnknownFieldException(field.getId(), FieldType.COMMON);
    }
    return value;
  }

  private Object getCommonFilterValue() {
    Object value;
    Set<String> filters = variantContext.getFiltersMaybeNull();
    if (filters == null) {
      value = emptyList();
    } else if (filters.isEmpty()) {
      value = PASS_FILTER;
    } else {
      if (filters.size() == 1) {
        value = singletonList(filters.iterator().next());
      } else {
        value = new ArrayList<>(filters);
      }
    }
    return value;
  }

  private Object getInfoValue(Field field, int alleleIndex) {
    Object value;

    ValueCount valueCount = field.getValueCount();
    Type valueCountType = valueCount.getType();
    switch (valueCountType) {
      case A:
        List<?> aInfoList = getInfoList(field);
        value = !aInfoList.isEmpty() ? aInfoList.get(alleleIndex - 1) : null;
        break;
      case R:
        List<?> rInfoList = getInfoList(field);
        value = !rInfoList.isEmpty() ? rInfoList.get(alleleIndex) : null;
        break;
      case VARIABLE:
        value = getInfoList(field);
        break;
      case FIXED:
        value = valueCount.getCount() == 1 ? getInfo(field) : getInfoList(field);
        break;
      default:
        throw new UnexpectedEnumException(valueCountType);
    }
    return value;
  }

  private Object getInfo(Field field) {
    Object value;
    ValueType valueType = field.getValueType();
    switch (valueType) {
      case INTEGER:
        value = VcfUtils.getInfoAsInteger(variantContext, field);
        break;
      case FLAG:
        value = VcfUtils.getInfoAsBoolean(variantContext, field);
        break;
      case FLOAT:
        value = VcfUtils.getInfoAsDouble(variantContext, field);
        break;
      case CHARACTER:
      case STRING:
        value = VcfUtils.getInfoAsString(variantContext, field);
        break;
      default:
        throw new UnexpectedEnumException(valueType);
    }
    return value;
  }

  private List<?> getInfoList(Field field) {
    List<?> listValues;
    ValueType valueType = field.getValueType();
    switch (valueType) {
      case INTEGER:
        listValues = VcfUtils.getInfoAsIntegerList(variantContext, field);
        break;
      case FLOAT:
        listValues = VcfUtils.getInfoAsDoubleList(variantContext, field);
        break;
      case CHARACTER:
      case STRING:
        listValues = VcfUtils.getInfoAsStringList(variantContext, field);
        break;
      case FLAG:
        throw new FlagListException(field.getId());
      default:
        throw new UnexpectedEnumException(valueType);
    }
    return listValues;
  }

  public VariantContext unwrap() {
    return variantContext;
  }

  public String toDisplayString() {
    return String.format(
        "%s:%s %s",
        variantContext.getContig(),
        variantContext.getStart(),
        variantContext.getReference().getBaseString());
  }
}
