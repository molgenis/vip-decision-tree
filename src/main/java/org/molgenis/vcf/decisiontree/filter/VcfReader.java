package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;

  public VcfReader(VCFFileReader vcfFileReader) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    return new VcfMetadata(vcfFileReader.getFileHeader());
  }

  public VCFFileReader unwrap() {
    return vcfFileReader;
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
