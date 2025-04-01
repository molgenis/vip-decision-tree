package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFHeader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;

public interface ConsequenceAnnotatorFactory {
   ConsequenceAnnotator create(Settings settings, VCFHeader header);
}
