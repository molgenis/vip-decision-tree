package org.molgenis.vcf.decisiontree.runner.info;

import org.molgenis.vcf.decisiontree.Settings;

public interface VepMetadataParserFactory {
    VepMetadataParser create(Settings settings);
}
