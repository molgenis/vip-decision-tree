package org.molgenis.vcf.decisiontree.runner.info;

import java.util.HashMap;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.GenotypeFieldType;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.molgenis.vcf.decisiontree.filter.model.NestedField.NestedFieldBuilder;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;
import org.springframework.stereotype.Component;

import static org.molgenis.vcf.utils.metadata.ValueCount.Type.FIXED;
import static org.molgenis.vcf.utils.metadata.ValueCount.Type.VARIABLE;

@Component
public class GenotypeMetadataMapperImpl implements GenotypeMetadataMapper {

  public static final String GENOTYPE = "GENOTYPE";

  @Override
  public NestedHeaderLine map() {
    Map<String, NestedField> nestedFields = new HashMap<>();
    int index = 0;
    FieldImpl genotypeParentField =
        FieldImpl.builder()
            .id(GENOTYPE)
            .fieldType(FieldType.FORMAT)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(FIXED).count(1).build())
            .build();
    for (GenotypeFieldType genotypeFieldType : GenotypeFieldType.values()) {
      nestedFields.put(genotypeFieldType.name(),
          mapNestedMetadataToField(genotypeFieldType, index, genotypeParentField));
      index++;
    }
    return NestedHeaderLine.builder().parentField(genotypeParentField)
        .nestedFields(nestedFields).build();
  }


  protected NestedField mapNestedMetadataToField(
      GenotypeFieldType genotypeFieldType, int index, FieldImpl vepField) {
    NestedFieldBuilder fieldBuilder =
        NestedField.nestedBuilder()
            .id(genotypeFieldType.name())
            .index(index)
            .parent(vepField)
            .fieldType(FieldType.GENOTYPE);
    switch (genotypeFieldType) {
      case ALLELES -> fieldBuilder
          .valueCount(ValueCount.builder().type(VARIABLE).build())
          .valueType(ValueType.STRING);
      case ALLELE_NUM -> fieldBuilder
          .valueCount(ValueCount.builder().type(VARIABLE).build())
          .valueType(ValueType.INTEGER);
      case PLOIDY -> fieldBuilder
          .valueCount(ValueCount.builder().type(FIXED).count(1).build())
          .valueType(ValueType.INTEGER);
      case PHASED, CALLED, MIXED, NON_INFORMATIVE -> fieldBuilder
          .valueCount(ValueCount.builder().type(FIXED).count(1).build())
          .valueType(ValueType.FLAG);
      case TYPE -> fieldBuilder
          .valueCount(ValueCount.builder().type(FIXED).count(1).build())
          .valueType(ValueType.STRING);
      default -> throw new UnexpectedEnumException(genotypeFieldType);
    }
    return fieldBuilder.build();
  }
}
