package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;

interface ClassifierFactory {

  Classifier create(Settings settings);

}
