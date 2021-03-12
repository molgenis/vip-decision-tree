package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.NestedField.NestedFieldBuilder;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.springframework.stereotype.Component;

@Component
public class SnpEffInfoMetadataMapper implements NestedMetadataMapper {

  private static final String INFO_ID = "ANN";
  private static final Pattern INFO_DESCRIPTION_PATTERN =
      Pattern.compile("Functional annotations: '(.*?)'");

  private final SnpEffInfoSelectorFactory snpEffInfoSelectorFactory;

  public SnpEffInfoMetadataMapper(SnpEffInfoSelectorFactory snpEffInfoSelectorFactory) {
    this.snpEffInfoSelectorFactory = requireNonNull(snpEffInfoSelectorFactory);
  }

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    return vcfInfoHeaderLine.getID().equals(INFO_ID)
        && INFO_DESCRIPTION_PATTERN.matcher(vcfInfoHeaderLine.getDescription().trim()).matches();
  }

  @Override
  public NestedInfoHeaderLine map(VCFInfoHeaderLine vcfInfoHeaderLine) {
    SnpEffInfoSelector infoSelector = snpEffInfoSelectorFactory.create();
    Map<String, NestedField> nestedFields = new HashMap<>();
    int index = 0;
    for (String id : getNestedInfoIds(vcfInfoHeaderLine)) {
      FieldImpl annField =
          FieldImpl.builder()
              .id(vcfInfoHeaderLine.getID())
              .fieldType(FieldType.INFO)
              .valueType(ValueType.STRING)
              .valueCount(ValueCount.builder().type(VARIABLE).build())
              .separator('|')
              .build();
      nestedFields.put(id, mapNestedMetadataToField(id, index, annField, infoSelector));
      index++;
    }
    NestedInfoHeaderLine nestedInfoHeaderLine =
        NestedInfoHeaderLine.builder().nestedFields(nestedFields).build();
    infoSelector.setNestedInfoHeaderLine(nestedInfoHeaderLine);
    return nestedInfoHeaderLine;
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

  protected NestedField mapNestedMetadataToField(
      String id, int index, FieldImpl annField, NestedInfoSelector infoSelector) {
    NestedFieldBuilder fieldBuilder =
        NestedField.nestedBuilder()
            .id(id)
            .index(index)
            .parent(annField)
            .fieldType(FieldType.INFO_NESTED)
            .nestedInfoSelector(infoSelector);
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
