package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.utils.metadata.*;
import org.molgenis.vcf.utils.model.metadata.FieldMetadata;
import org.molgenis.vcf.utils.model.metadata.FieldMetadatas;
import org.molgenis.vcf.utils.model.metadata.NestedFieldMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO_VEP;
import static org.molgenis.vcf.utils.metadata.ValueCount.Type.VARIABLE;

public class VepMetadataMapperImpl implements VepMetadataMapper {

  public static final String ALLELE_NUM = "ALLELE_NUM";
  private static final String INFO_DESCRIPTION_PREFIX = "Consequence annotations from Ensembl VEP. Format: ";

  private final FieldMetadataService fieldMetadataService;

  public VepMetadataMapperImpl(FieldMetadataService fieldMetadataService) {
    this.fieldMetadataService = requireNonNull(fieldMetadataService);
  }

  @Override
  public boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine) {
    // match on the description since the INFO ID is configurable (default: CSQ)
    String description = vcfInfoHeaderLine.getDescription();
    return description.startsWith(INFO_DESCRIPTION_PREFIX);
  }

  @Override
  public NestedHeaderLine map(String csqId, VCFHeader vcfHeader) {
    VCFInfoHeaderLine vcfInfoHeaderLine = vcfHeader.getInfoHeaderLine(csqId);

    FieldImpl vepField =
        FieldImpl.builder()
            .id(vcfInfoHeaderLine.getID())
            .fieldType(INFO)
            .valueType(ValueType.STRING)
            .valueCount(ValueCount.builder().type(VARIABLE).build())
            .separator('|')
            .build();

    FieldMetadatas fieldMetadatas = fieldMetadataService.load(vcfHeader, Map.of(FieldIdentifier.builder().type(org.molgenis.vcf.utils.metadata.FieldType.INFO).name(csqId).build(), NestedAttributes.builder().prefix(INFO_DESCRIPTION_PREFIX).seperator("|").build()));
    Map<String, NestedField> nestedFields = new HashMap<>();
    FieldMetadata csqField = fieldMetadatas.getInfo().get(csqId);
    if(csqField == null){
      throw new MissingVepMetaException();
    }
    Map<String, NestedFieldMetadata> nestedFieldsMeta = csqField.getNestedFields();
    if(nestedFieldsMeta == null){
      throw new NotParentFieldException(csqId);
    }
    for(Entry<String, NestedFieldMetadata> entry : nestedFieldsMeta.entrySet()){
      nestedFields.put(entry.getKey(), mapNestedFieldMetadata(entry.getKey(), entry.getValue(), vepField));
    }
    return NestedHeaderLine.builder().parentField(vepField).nestedFields(nestedFields).build();
  }

  private NestedField mapNestedFieldMetadata(String id, NestedFieldMetadata nestedMeta, Field parent) {
    return new NestedField(id, INFO_VEP, nestedMeta.getType(),
            ValueCount.builder().count(nestedMeta.getNumberCount()).type(nestedMeta.getNumberType()).build(),
            nestedMeta.getNumberCount(), nestedMeta.getSeparator(), nestedMeta.getIndex(), parent);
  }
}
