package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.WriterSettings;
import org.molgenis.vcf.decisiontree.filter.DecisionTreeExecutor;

interface DecisionTreeExecutorFactory {

  DecisionTreeExecutor create(WriterSettings writerSettings);
}
