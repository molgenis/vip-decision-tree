package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Arrays.asList;
import static org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator.EQUALS;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;
import static org.molgenis.vcf.decisiontree.runner.info.NestedValueSelector.SELECTED_ALLELE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.springframework.stereotype.Component;

@Component
public class SnpEffInfoMetadataMapper implements NestedMetadataMapper {

  private static final String INFO_ID = "ANN";
  private static final Pattern INFO_DESCRIPTION_PATTERN =
      Pattern.compile("Functional annotations: '(.*?)'");
  public static final char SEPARATOR = '|';
  private static final String ALLELE = "Allele";
  private final NestedValueSelectorFactory nestedValueSelectorFactory;

  public SnpEffInfoMetadataMapper(
      NestedValueSelectorFactory nestedValueSelectorFactory) {
    this.nestedValueSelectorFactory = nestedValueSelectorFactory;
  }

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    return vcfInfoHeaderLine.getID().equals(INFO_ID)
        && INFO_DESCRIPTION_PATTERN.matcher(vcfInfoHeaderLine.getDescription().trim()).matches();
  }

  @Override
  public Field map(VCFInfoHeaderLine vcfInfoHeaderLine) {
    Map<String, Field> nestedFields = new HashMap<>();
    int index = 0;
    for (String id : getNestedInfoIds(vcfInfoHeaderLine)) {
      nestedFields.put(id, mapNestedMetadataToField(id, index, vcfInfoHeaderLine.getID()));
      index++;
    }
    NestedValueSelector selector = createSelector(nestedFields, vcfInfoHeaderLine.getID());
    for (Entry<String, Field> entry: nestedFields.entrySet()) {
      Field field = entry.getValue();
      field.setNestedValueSelector(selector);
      nestedFields.put(entry.getKey(), field);
    }
    return
        Field.builder()
            .id(vcfInfoHeaderLine.getID())
            .fieldType(FieldType.INFO)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(VARIABLE).build())
            .separator(SEPARATOR).children(nestedFields).build();
  }

  private NestedValueSelector createSelector(Map<String, Field> nestedFields, String parent) {
    BoolQuery query;
    Field field = nestedFields.get(ALLELE);
    if(field != null) {
      query = BoolQuery.builder().field(field).operator(EQUALS).value(SELECTED_ALLELE).build();
    }else{
      throw new MissingRequiredNestedValueException(ALLELE, parent);
    }
    return nestedValueSelectorFactory.create(Collections.singletonList(query),SEPARATOR);
  }

  protected List<String> getNestedInfoIds(VCFInfoHeaderLine vcfInfoHeaderLine) {
    String description = vcfInfoHeaderLine.getDescription();
    Matcher matcher = INFO_DESCRIPTION_PATTERN.matcher(description);
    if (!matcher.find()) {
      throw new InvalidHeaderLineException(vcfInfoHeaderLine.getID());
    }
    String[] infoIds = matcher.group(1).split("\\|", -1);
    return asList(infoIds).stream()
        .map(id -> id.replaceAll("\\s+", ""))
        .collect(Collectors.toList());
  }

  protected Field mapNestedMetadataToField(
      String id, int index, String parentId) {
    Field.FieldBuilder fieldBuilder =
        Field.builder()
            .id(id)
            .index(index)
            .parentId(parentId)
            .fieldType(FieldType.INFO_NESTED);
    switch (id) {
      case "cDNA.pos/cDNA.length":
      case "CDS.pos/CDS.length":
      case "AA.pos/AA.length":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(2).build())
            .separator('/')
            .valueType(ValueType.INTEGER);
        break;
      case "Distance":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.INTEGER);
        break;
      case "ERRORS/WARNINGS/INFO":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(3).build())
            .separator('/')
            .valueType(ValueType.STRING);
        break;
      case "Annotation":
        fieldBuilder
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.STRING)
            .separator('&');
        break;
      default:
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.STRING);
    }
    return fieldBuilder.build();
  }
}
