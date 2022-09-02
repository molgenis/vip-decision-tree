package org.molgenis.vcf.decisiontree.filter;

public interface RecordWriter extends AutoCloseable {

  void write(VcfRecord vcfRecord);
}