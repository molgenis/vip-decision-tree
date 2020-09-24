package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Arrays.asList;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.VcfRecord;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.NestedField.NestedFieldBuilder;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.springframework.stereotype.Component;

@Component
public class SnpEffInfoMetadataMapper implements NestedMetadataMapper {

  private static final String INFO_ID = "ANN";
  private static final Pattern INFO_DESCRIPTION_PATTERN =
      Pattern.compile("Functional annotations: '(.*?)'");
  private static final String ALLELE = "Allele";

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    return vcfInfoHeaderLine.getID().equals(INFO_ID)
        && INFO_DESCRIPTION_PATTERN.matcher(vcfInfoHeaderLine.getDescription().trim()).matches();
  }

  @Override
  public Map<String, NestedField> map(VCFInfoHeaderLine vcfInfoHeaderLine) {
    Map<String, NestedField> nestedFields = new HashMap<>();
    int index = 0;
    for (String id : getNestedInfoIds(vcfInfoHeaderLine)) {
      Field annField = Field.builder()
          .id(vcfInfoHeaderLine.getID())
          .fieldType(FieldType.INFO)
          .valueType(ValueType.STRING)
          .valueCount(ValueCount.builder().type(VARIABLE).build())
          .separator('|')
          .build();
      nestedFields.put(id, mapNestedMetadataToField(id, index, annField));
      index++;
    }
    Map<NestedField, Object> selectors = getSelectors(nestedFields);
    for (Entry<String, NestedField> nestedFieldEntry : nestedFields.entrySet()) {
      NestedField nestedField = nestedFieldEntry.getValue();
      nestedField.setSelectors(selectors);
      nestedFields.put(nestedFieldEntry.getKey(), nestedField);
    }
    return nestedFields;
  }

  private Map<NestedField, Object> getSelectors(Map<String, NestedField> nestedFields) {
    Map<NestedField, Object> selector = new HashMap<>();
    if (nestedFields.containsKey(ALLELE)) {
      selector.put(nestedFields.get(ALLELE), VcfRecord.SELECTED_ALLELE);
    } else {
      throw new MissingRequiredNestedValueException("SnpEff", ALLELE);
    }
    return selector;
  }

  protected List<String> getNestedInfoIds(VCFInfoHeaderLine vcfInfoHeaderLine) {
    String description = vcfInfoHeaderLine.getDescription();
    int end = description.trim().length() - 1;//remove trailing space and quote
    String[] infoIds = description.trim().substring("Functional annotations: '".length(), end)
        .split("\\|", -1);
    return asList(infoIds).stream().map(id -> id.replaceAll("\\s+", ""))
        .collect(Collectors.toList());
  }

  protected NestedField mapNestedMetadataToField(String id, int index, Field annField) {
    NestedFieldBuilder fieldBuilder = NestedField.nestedBuilder().id(id).index(index)
        .parent(annField).fieldType(FieldType.INFO_NESTED);
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
      default:
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.STRING);
    }
    return fieldBuilder.build();
  }
}
