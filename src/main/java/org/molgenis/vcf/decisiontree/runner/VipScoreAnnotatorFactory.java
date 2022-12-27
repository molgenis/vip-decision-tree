package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VipScoreAnnotator;

interface VipScoreAnnotatorFactory {

    VipScoreAnnotator create(Settings settings);
}
