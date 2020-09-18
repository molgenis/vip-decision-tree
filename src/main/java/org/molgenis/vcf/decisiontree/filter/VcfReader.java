package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.NonNull;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNestedMetadata;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final Map<String, ConfigNestedMetadata> nestedMetadata;

  public VcfReader(VCFFileReader vcfFileReader, @NonNull Map<String, ConfigNestedMetadata> configNestedMetadata) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.nestedMetadata = requireNonNull(configNestedMetadata);
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(vc -> new VcfRecord(vc, nestedMetadata));
  }

  public VcfMetadata getMetadata() {
    return new VcfMetadata(vcfFileReader.getFileHeader(), nestedMetadata);
  }

  public VCFFileReader unwrap() {
    return vcfFileReader;
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
