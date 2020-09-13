package org.molgenis.vcf.decisiontree.filter;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.Decision;

public interface DecisionWriter extends AutoCloseable {
  void write(List<Decision> decisions, VcfRecord vcfRecord);
}
