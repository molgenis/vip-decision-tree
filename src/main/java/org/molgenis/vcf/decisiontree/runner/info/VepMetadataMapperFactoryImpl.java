package org.molgenis.vcf.decisiontree.runner.info;

import org.molgenis.vcf.decisiontree.Settings;
import org.springframework.stereotype.Component;

@Component
public class VepMetadataMapperFactoryImpl implements VepMetadataMapperFactory {
    @Override
    public VepMetadataMapper create(Settings settings) {
        return new VepMetadataMapperImpl(settings.getMetadataPath());
    }
}
