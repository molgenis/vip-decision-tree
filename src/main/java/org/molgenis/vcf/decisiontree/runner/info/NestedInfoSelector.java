package org.molgenis.vcf.decisiontree.runner.info;

import org.molgenis.vcf.decisiontree.filter.Allele;

public interface NestedInfoSelector {

  boolean isMatch(String infoValue, Allele allele);
}
