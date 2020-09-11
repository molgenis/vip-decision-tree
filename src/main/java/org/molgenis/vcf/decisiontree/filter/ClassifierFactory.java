package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.Settings;

public interface ClassifierFactory {
  Classifier create(Settings settings);
}
