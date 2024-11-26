package org.molgenis.vcf.decisiontree.runner.info;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.utils.metadata.FieldMetadataService;
import org.molgenis.vcf.utils.metadata.FieldMetadataServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class VepMetadataMapperFactoryImpl implements VepMetadataMapperFactory {
    @Override
    public VepMetadataMapper create(Settings settings) {
        FieldMetadataService fieldMetadataService = new FieldMetadataServiceImpl(settings.getMetadataPath().toFile());
        return new VepMetadataMapperImpl(fieldMetadataService);
    }
}
