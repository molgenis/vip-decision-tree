package org.molgenis.vcf.decisiontree.filter;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.Decision;

public interface RecordWriter extends AutoCloseable {

  void write(VcfRecord vcfRecord);
}