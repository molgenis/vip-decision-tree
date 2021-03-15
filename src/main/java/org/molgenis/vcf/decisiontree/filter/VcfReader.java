package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.molgenis.vcf.decisiontree.runner.info.VcfNestedMetadata;
import org.molgenis.vcf.decisiontree.runner.info.VcfNestedMetadataParser;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final VcfNestedMetadataParser vcfNestedMetadataParser;
  private final boolean strict;
  private VcfNestedMetadata nestedMetadata;
  private boolean inited = false;

  public VcfReader(VCFFileReader vcfFileReader, VcfNestedMetadataParser vcfNestedMetadataParser,
      boolean strict) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.vcfNestedMetadataParser = requireNonNull(vcfNestedMetadataParser);
    this.strict = strict;
  }

  private void initNestedMeta() {
    if (!inited) {
      nestedMetadata = vcfNestedMetadataParser.map(vcfFileReader.getFileHeader());
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfFileReader.getFileHeader(), nestedMetadata, strict);
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
