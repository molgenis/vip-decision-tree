package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.utils.metadata.FieldMetadataService;
import org.molgenis.vcf.utils.metadata.FieldMetadataServiceImpl;
import org.molgenis.vcf.utils.model.FieldMetadata;
import org.molgenis.vcf.utils.vep.VepMetadataService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

@Component
public class VepMetadataMapperFactoryImpl implements VepMetadataMapperFactory {
    @Override
    public VepMetadataMapper create(Settings settings) {
        Path metadataPath = settings.getMetadataPath();
        FieldMetadataService fieldMetadataService = new CustomFieldMetadataService(metadataPath);
        VepMetadataService vepMetadataService = new VepMetadataService(fieldMetadataService);
        return new VepMetadataMapperImpl(vepMetadataService);
    }

    private static class CustomFieldMetadataService extends FieldMetadataServiceImpl {
        private final Path metadataPath;

        public CustomFieldMetadataService(Path metadataPath) {
            this.metadataPath = requireNonNull(metadataPath);
        }

        @Override
        public FieldMetadata load(VCFInfoHeaderLine vcfInfoHeaderLine) {
            try(InputStream inputStream = Files.newInputStream(metadataPath)) {
                return this.load(inputStream, vcfInfoHeaderLine);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
