package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;

public class RecordWriterImpl implements RecordWriter {

  private final VariantContextWriter vcfWriter;
    private final Thread writerThread;

    public RecordWriterImpl(VariantContextWriter vcfWriter, Thread writerThread) {
      this.vcfWriter = requireNonNull(vcfWriter);
      this.writerThread = requireNonNull(writerThread);
    }

  @Override
  public void write(VcfRecord vcfRecord) {
    vcfWriter.add(vcfRecord.unwrap());
  }

  @Override
  public void close() {
    vcfWriter.close();
      try {
          writerThread.join();
      } catch (InterruptedException e) {
          writerThread.interrupt();
          throw new WriterThreadInterruptedException(e.getMessage());
      }
  }
}
