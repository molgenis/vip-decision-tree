package org.molgenis.vcf.decisiontree.runner.info;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.Allele;

public interface NestedInfoSelector {

  String select(List<String> infoValues, Allele allele);
}
