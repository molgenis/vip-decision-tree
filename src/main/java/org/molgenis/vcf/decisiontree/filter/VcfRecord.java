package org.molgenis.vcf.decisiontree.filter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.getTypedVcfValue;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.GenotypeFieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.decisiontree.filter.model.SampleFieldType;
import org.molgenis.vcf.decisiontree.utils.VcfUtils;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

/** {@link VariantContext} wrapper that works with nested data (e.g. CSQ INFO fields).. */
@Getter
public class VcfRecord {

  private static final List<String> PASS_FILTER = List.of(VCFConstants.PASSES_FILTERS_v4);

  private VariantContext variantContext;

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

  public @Nullable Object getValue(Field field, Allele allele) {
    return getValue(field, allele, null);
  }

  public @Nullable Object getValue(
      Field field, Allele allele, @Nullable SampleContext sampleContext) {
    Object value;
    FieldType fieldType = field.getFieldType();
    value =
        switch (fieldType) {
          case COMMON -> getCommonValue(field, allele);
          case INFO -> getInfoValue(field, allele);
          case INFO_VEP -> getNestedVepValue(field);
          case GENOTYPE ->
              sampleContext != null ? getNestedGTValue((NestedField) field, sampleContext) : null;
          case FORMAT -> {
            if (sampleContext == null) {
              throw new UnsupportedOperationException(
                  "Cannot filter on FORMAT fields when running in variant filter mode.");
            }
            yield getFormatField(field, sampleContext);
          }
          case SAMPLE -> sampleContext != null ? getSampleValue(field, sampleContext) : null;
        };
    return value;
  }

  private @Nullable Object getSampleValue(Field field, SampleContext sampleContext) {
    Object value;
    SampleFieldType sampleField = SampleFieldType.valueOf(field.getId().toUpperCase(Locale.ROOT));
    value =
        switch (sampleField) {
          case ID -> sampleContext.getId();
          case AFFECTED_STATUS -> sampleContext.getAffectedStatus().toString();
          case SEX -> sampleContext.getSex().toString();
          case FATHER_ID -> sampleContext.getFatherId();
          case MOTHER_ID -> sampleContext.getMotherId();
          case FAMILY_ID -> sampleContext.getFamilyId();
          case PHENOTYPES -> {
            List<String> phenotypes = sampleContext.getPhenotypes();
            yield phenotypes.isEmpty() ? null : phenotypes;
          }
          default -> throw new UnexpectedEnumException(sampleField);
        };
    return value;
  }

  private @Nullable Object getNestedGTValue(NestedField field, SampleContext sampleContext) {
    Object value;

    Genotype genotype =
        sampleContext != null ? variantContext.getGenotype(sampleContext.getIndex()) : null;
    if (genotype == null) {
      return null;
    }
    GenotypeFieldType genotypeFieldType = GenotypeFieldType.valueOf(field.getId());
    value =
        switch (genotypeFieldType) {
          case ALLELES ->
              genotype.getAlleles().stream()
                  .map(htsjdk.variant.variantcontext.Allele::getBaseString)
                  .toList();
          case ALLELE_NUM ->
              genotype.getAlleles().stream()
                  .map(allele -> variantContext.getAlleles().indexOf(allele))
                  .toList();
          case TYPE ->
              switch (genotype.getType()) {
                case MIXED -> GenotypeType.MIXED.name();
                case HET -> GenotypeType.HET.name();
                case HOM_REF -> GenotypeType.HOM_REF.name();
                case HOM_VAR -> GenotypeType.HOM_VAR.name();
                case NO_CALL -> GenotypeType.NO_CALL.name();
                case UNAVAILABLE -> GenotypeType.UNAVAILABLE.name();
              };
          case MIXED -> genotype.isMixed();
          case CALLED -> genotype.isCalled();
          case PLOIDY -> {
            int ploidy = genotype.getPloidy();
            yield ploidy != 0 ? ploidy : null;
          }
          case PHASED -> genotype.isPhased();
          case NON_INFORMATIVE -> genotype.isNonInformative();
        };
    return value;
  }

  @SuppressWarnings("java:S1612")
  // suggested use of methode reference Integer::toString is not possible due to ambiguity
  private @Nullable Object getFormatField(Field field, SampleContext sampleContext) {
    Genotype genotype = variantContext.getGenotype(sampleContext.getIndex());
    if (genotype == null) {
      return null;
    }

    Object typedValue;
    switch (field.getId()) {
      case "GT" -> {
        String separator = genotype.isPhased() ? "|" : "/";
        typedValue =
            String.join(
                separator,
                genotype.getAlleles().stream()
                    .map(allele -> variantContext.getAlleles().indexOf(allele))
                    .map(this::mapAlleleString)
                    .toList());
      }
      case "AD" -> {
        int[] ad = genotype.getAD();
        typedValue = ad != null ? IntStream.of(ad).boxed().toList() : null;
      }
      case "DP" -> {
        typedValue = genotype.getDP();
        if ((Integer) typedValue == -1) {
          typedValue = null;
        }
      }
      case "GQ" -> {
        int gq = genotype.getGQ();
        typedValue = gq != -1 ? gq : null;
      }
      case "PL" -> {
        int[] pl = genotype.getPL();
        typedValue = pl != null ? IntStream.of(pl).boxed().toList() : null;
      }
      default -> typedValue = getExtendedAttributeValue(field, genotype);
    }
    return typedValue;
  }

  private String mapAlleleString(Integer index) {
    return index != -1 ? Integer.toString(index) : ".";
  }

  private @Nullable Object getExtendedAttributeValue(Field field, Genotype genotype) {
    Object typedValue;
    Object value;
    value = genotype.getExtendedAttribute(field.getId());
    if (value != null && !(value instanceof String)) {
      throw new UnsupportedFormatFieldException(value.getClass());
    }

    ValueCount valueCount = field.getValueCount();
    ValueCount.Type valueCountType = valueCount.getType();
    switch (valueCountType) {
      case A, R, VARIABLE ->
          typedValue =
              value != null ? VcfUtils.getTypedVcfListValue(field, value.toString()) : null;
      case FIXED -> {
        if (valueCount.getCount() == 1) {
          typedValue = value != null ? VcfUtils.getTypedVcfValue(field, value.toString()) : null;
        } else {
          typedValue =
              value != null ? VcfUtils.getTypedVcfListValue(field, value.toString()) : null;
        }
      }
      default -> throw new UnexpectedEnumException(valueCountType);
    }
    return typedValue;
  }

  public List<String> getVepValues(Field vepField) {
    return getVariantContext().getAttributeAsStringList(vepField.getId(), "");
  }

  private @Nullable Object getNestedVepValue(Field field) {
    Object value = null;
    NestedField nestedField = (NestedField) field;
    String separator = Pattern.quote(nestedField.getParent().getSeparator().toString());
    int index = nestedField.getIndex();
    String parentId = nestedField.getParent().getId();
    List<String> infoValues =
        VcfUtils.getInfoAsStringList(variantContext, parentId, VCFConstants.MISSING_VALUE_v4);
    if (!infoValues.isEmpty()) {
      String singleValue = infoValues.getFirst();
      String[] split = singleValue.split(separator, -1);
      String stringValue = split[index];
      if (!stringValue.isEmpty()) {
        if (nestedField.getSeparator() != null) {
          String nestedSeparator = Pattern.quote(nestedField.getSeparator().toString());
          value = getTypedVcfValue(field, stringValue, nestedSeparator);
        } else {
          value = VcfUtils.getTypedVcfValue(field, stringValue);
        }
      }
    }
    return value;
  }

  private @Nullable Object getCommonValue(Field field, Allele allele) {
    return switch (field.getId()) {
      case "#CHROM" -> variantContext.getContig();
      case "POS" -> variantContext.getStart();
      case "ID" -> variantContext.hasID() ? asList(variantContext.getID().split(";")) : emptyList();
      case "REF" -> variantContext.getReference().getBaseString();
      case "ALT" -> asList(allele.getBases().split(","));
      case "QUAL" -> variantContext.hasLog10PError() ? variantContext.getPhredScaledQual() : null;
      case "FILTER" -> getCommonFilterValue();
      default -> throw new UnknownFieldException(field.getId(), FieldType.COMMON);
    };
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
        value = List.of(filters.iterator().next());
      } else {
        value = new ArrayList<>(filters);
      }
    }
    return value;
  }

  private @Nullable Object getInfoValue(Field field, Allele allele) {
    ValueCount valueCount = field.getValueCount();
    ValueCount.Type valueCountType = valueCount.getType();
    return switch (valueCountType) {
      case A -> {
        List<?> aInfoList = getInfoList(field);
        yield !aInfoList.isEmpty() ? aInfoList.get(allele.getIndex() - 1) : null;
      }
      case R -> {
        List<?> rInfoList = getInfoList(field);
        yield !rInfoList.isEmpty() ? rInfoList.get(allele.getIndex()) : null;
      }
      case VARIABLE -> getInfoList(field);
      case FIXED -> valueCount.getCount() <= 1 ? getInfo(field) : getInfoList(field);
      default -> throw new UnexpectedEnumException(valueCountType);
    };
  }

  private @Nullable Object getInfo(Field field) {
    ValueType valueType = field.getValueType();
    return switch (valueType) {
      case INTEGER -> VcfUtils.getInfoAsInteger(variantContext, field);
      case FLAG -> VcfUtils.getInfoAsBoolean(variantContext, field);
      case FLOAT -> VcfUtils.getInfoAsDouble(variantContext, field);
      case CHARACTER, STRING, CATEGORICAL -> VcfUtils.getInfoAsString(variantContext, field);
    };
  }

  private List<?> getInfoList(Field field) {
    ValueType valueType = field.getValueType();
    return switch (valueType) {
      case INTEGER -> VcfUtils.getInfoAsIntegerList(variantContext, field);
      case FLOAT -> VcfUtils.getInfoAsDoubleList(variantContext, field);
      case CHARACTER, STRING, CATEGORICAL -> VcfUtils.getInfoAsStringList(variantContext, field);
      case FLAG -> throw new FlagListException(field.getId());
    };
  }

  public void setAttribute(Field attribute, Object value) {
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(attribute.getId(), value);
    variantContext = variantContextBuilder.make();
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

  public VcfRecord getFilteredCopy(String consequence, Field vepField) {
    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);
    variantContextBuilder.attribute(vepField.getId(), List.of(consequence));
    VariantContext filterVariantContext = variantContextBuilder.make();
    return new VcfRecord(filterVariantContext);
  }
}
