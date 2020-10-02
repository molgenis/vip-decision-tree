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

  private final VariantContext variantContext;

  public VcfRecord(VariantContext variantContext) {
    this.variantContext = requireNonNull(variantContext);
  }

  public int getNrAltAlleles() {
    return variantContext.getNAlleles() - 1;
  }

  public Allele getAltAllele(int altAlleleIndex) {
    String bases = variantContext.getAlternateAllele(altAlleleIndex).getBaseString();
    int alleleIndex = altAlleleIndex + 1;
    return new Allele(bases, alleleIndex);
  }

  public Object getValue(Field field, Allele allele) {
    Object value;
    FieldType fieldType = field.getFieldType();
    switch (fieldType) {
      case COMMON:
        value = getCommonValue(field, allele);
        break;
      case INFO:
        value = getInfoValue(field, allele);
        break;
      case INFO_NESTED:
        value = getNestedValue(field, allele);
        break;
      case FORMAT:
        throw new UnsupportedOperationException("FORMAT values are not yet supported."); // TODO
      default:
        throw new UnexpectedEnumException(fieldType);
    }
    return value;
  }

  private Object getNestedValue(Field field, Allele allele) {
    Object value = null;
    NestedField nestedField = (NestedField) field;
    String separator = Pattern.quote(nestedField.getParent().getSeparator().toString());
    int index = nestedField.getIndex();
    String parentId = nestedField.getParent().getId();
    List<String> infoValues = VcfUtils.getInfoAsStringList(variantContext, parentId);
    if (!infoValues.isEmpty()) {
      List<String> filteredInfo =
          infoValues.stream()
              .filter(
                  nestedValue -> nestedField.getNestedInfoSelector().isMatch(nestedValue, allele))
              .collect(Collectors.toList());
      if (!filteredInfo.isEmpty()) {
        String singleValue = filteredInfo.get(0);
        String[] split = singleValue.split(separator, -1);
        String stringValue = split[index];
        if (!stringValue.isEmpty()) {
          if (field.getSeparator() != null) {
            String nestedSeparator = Pattern.quote(nestedField.getSeparator().toString());
            value = getTypedInfoValue(field, stringValue, nestedSeparator);
          } else {
            value = getTypedInfoValue(field, stringValue);
          }
        }
      }
    }
    return value;
  }

  private Object getCommonValue(Field field, Allele allele) {
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
        value = allele.getBases();
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

  private Object getInfoValue(Field field, Allele allele) {
    Object value;

    ValueCount valueCount = field.getValueCount();
    Type valueCountType = valueCount.getType();
    switch (valueCountType) {
      case A:
        List<?> aInfoList = getInfoList(field);
        value = !aInfoList.isEmpty() ? aInfoList.get(allele.getIndex() - 1) : null;
        break;
      case R:
        List<?> rInfoList = getInfoList(field);
        value = !rInfoList.isEmpty() ? rInfoList.get(allele.getIndex()) : null;
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
