package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;

public class RecordWriterImpl implements RecordWriter {

  private final VariantContextWriter vcfWriter;

  public RecordWriterImpl(VariantContextWriter vcfWriter) {
    this.vcfWriter = requireNonNull(vcfWriter);
  }

  @Override
  public void write(VcfRecord vcfRecord) {
    vcfWriter.add(vcfRecord.unwrap());
  }

  @Override
  public void close() {
    vcfWriter.close();
  }
}
