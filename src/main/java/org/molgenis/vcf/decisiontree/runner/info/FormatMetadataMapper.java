package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.utils.metadata.*;
import org.molgenis.vcf.utils.model.metadata.FieldMetadata;
import org.molgenis.vcf.utils.model.metadata.FieldMetadatas;
import java.util.HashMap;
import java.util.Map;

import static org.molgenis.vcf.decisiontree.filter.model.FieldType.*;
import static org.molgenis.vcf.decisiontree.runner.VepHelper.INFO_DESCRIPTION_PREFIX;
import static org.molgenis.vcf.utils.metadata.ValueCount.Type.VARIABLE;

public class FormatMetadataMapper {

    private final Settings settings;

    public FormatMetadataMapper(Settings settings) {
        this.settings = settings;
    }

    public Map<String, Field> map(VCFHeader vcfHeader) {
        FieldMetadataService fieldMetadataService = new FieldMetadataServiceImpl(settings.getMetadataPath().toFile());
        FieldImpl vepField =
                FieldImpl.builder()
                        .id("CSQ")
                        .fieldType(INFO)
                        .valueType(ValueType.STRING)
                        .valueCount(ValueCount.builder().type(VARIABLE).build())
                        .separator('|')
                        .build();

        FieldMetadatas fieldMetadatas = fieldMetadataService.load(vcfHeader, Map.of(FieldIdentifier.builder().type(org.molgenis.vcf.utils.metadata.FieldType.INFO).name("CSQ").build(), NestedAttributes.builder().prefix(INFO_DESCRIPTION_PREFIX).seperator("|").build()));

        Map<String, FieldMetadata> formatMeta = fieldMetadatas.getFormat();
        Map<String, Field> mappedFormatMeta = new HashMap<>();
        formatMeta.entrySet().forEach(entry -> mappedFormatMeta.put(entry.getKey(), mapFieldMetadata(entry.getKey(), entry.getValue())));
        return mappedFormatMeta;
    }

    private Field mapFieldMetadata(String id, FieldMetadata newMeta) {
        return new FieldImpl(id, FORMAT, newMeta.getType(),
                ValueCount.builder().count(newMeta.getNumberCount()).type(newMeta.getNumberType()).build(),
                newMeta.getNumberCount(), newMeta.getSeparator());
    }
}
