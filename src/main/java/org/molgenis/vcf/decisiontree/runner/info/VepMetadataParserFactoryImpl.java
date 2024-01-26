package org.molgenis.vcf.decisiontree.runner.info;

import org.molgenis.vcf.decisiontree.Settings;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class VepMetadataParserFactoryImpl implements VepMetadataParserFactory {
    private final VepMetadataMapperFactory vepMetadataMapperFactory;

    public VepMetadataParserFactoryImpl(VepMetadataMapperFactory vepMetadataMapperFactory) {
        this.vepMetadataMapperFactory = requireNonNull(vepMetadataMapperFactory);
    }

    @Override
    public VepMetadataParser create(Settings settings) {
        VepMetadataMapper vepMetadataMapper = vepMetadataMapperFactory.create(settings);
        return new VepMetadataParserImpl(vepMetadataMapper);
    }
}
