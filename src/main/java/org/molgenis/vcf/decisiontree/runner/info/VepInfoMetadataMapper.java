package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Arrays.asList;
import static org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator.EQUALS;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.FIXED;
import static org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type.VARIABLE;
import static org.molgenis.vcf.decisiontree.runner.info.NestedValueSelector.SELECTED_ALLELE_INDEX;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.springframework.stereotype.Component;

@Component
public class VepInfoMetadataMapper implements NestedMetadataMapper {

  private static final String INFO_DESCRIPTION_PREFIX =
      "Consequence annotations from Ensembl VEP. Format: ";
  public static final char SEPARATOR = '|';
  public static final String PICK = "PICK";
  public static final String ALLELE_NUM = "ALLELE_NUM";

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    // match on the description since the INFO ID is configurable (default: CSQ)
    String description = vcfInfoHeaderLine.getDescription();
    return description.startsWith(INFO_DESCRIPTION_PREFIX);
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
            .separator(SEPARATOR)
            .children(nestedFields)
            .build();
  }

  private NestedValueSelector createSelector(Map<String, Field> nestedFields, String parentId) {
    List<BoolQuery> selectorQueries = new ArrayList<>();
    selectorQueries.add(createQuery(PICK,EQUALS,1, false, nestedFields, parentId));
    selectorQueries.add(createQuery(ALLELE_NUM,EQUALS,SELECTED_ALLELE_INDEX, true, nestedFields, parentId));
    return new NestedValueSelector(selectorQueries,SEPARATOR);
  }

  private BoolQuery createQuery(String fieldName, Operator operator, Object value, boolean required, Map<String, Field> nestedFields, String parentId){
    BoolQuery query = null;
    Field field = nestedFields.get(fieldName);
    if(field != null) {
      query = BoolQuery.builder().field(field).operator(operator).value(value).build();
    }else if(required){
      throw new MissingRequiredNestedValueException(fieldName, parentId);
    }
    return query;
  }

  protected List<String> getNestedInfoIds(VCFInfoHeaderLine vcfInfoHeaderLine) {
    String description = vcfInfoHeaderLine.getDescription();
    String[] infoIds = description.substring(INFO_DESCRIPTION_PREFIX.length()).split("\\|", -1);
    return asList(infoIds);
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
      case "Consequence":
      case "Existing_variation":
      case "CLIN_SIG":
      case "FLAGS":
        fieldBuilder
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.STRING)
            .separator('&');
        break;
      case "PHENO":
      case "PUBMED":
      case "SOMATIC":
        fieldBuilder
            .valueCount(ValueCount.builder().type(Type.VARIABLE).build())
            .valueType(ValueType.INTEGER)
            .separator('&');
        break;
      case "STRAND":
      case "HGNC_ID":
      case "cDNA_position":
      case "CDS_position":
      case "Protein_position":
      case PICK:
      case ALLELE_NUM:
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.INTEGER);
        break;
      case "gnomAD_AF":
      case "gnomAD_AFR_AF":
      case "gnomAD_AMR_AF":
      case "gnomAD_ASJ_AF":
      case "gnomAD_EAS_AF":
      case "gnomAD_FIN_AF":
      case "gnomAD_NFE_AF":
      case "gnomAD_OTH_AF":
      case "gnomAD_SAS_AF":
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.FLOAT);
        break;
      default:
        fieldBuilder
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .valueType(ValueType.STRING);
    }
    return fieldBuilder.build();
  }
}
