package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.WriterSettings;

public interface DecisionTreeExecutorFactory {
  DecisionTreeExecutor create(WriterSettings writerSettings);
}
