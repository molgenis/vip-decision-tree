package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.Decision;

public interface ConsequenceAnnotator {

  String annotate(Decision decision, String consequence);
}
