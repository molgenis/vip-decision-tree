package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.molgenis.vcf.decisiontree.runner.info.GenotypeMetadataMapper;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataParser;
import org.molgenis.vcf.utils.metadata.MetadataService;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final VepMetadataParser vepMetadataParser;
  private final boolean strict;
  private final GenotypeMetadataMapper genotypeMetadataMapper;
  private boolean inited = false;
  private NestedHeaderLine vepNestedHeaderLine = null;
  private NestedHeaderLine gtNestedHeaderLine = null;
  private final MetadataService metadataService;
  public VcfReader(VCFFileReader vcfFileReader, VepMetadataParser vepMetadataParser,
      GenotypeMetadataMapper genotypeMetadataMapper, MetadataService metadataService, boolean strict) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.vepMetadataParser = requireNonNull(vepMetadataParser);
    this.genotypeMetadataMapper = requireNonNull(genotypeMetadataMapper);
    this.metadataService = requireNonNull(metadataService);
    this.strict = strict;
  }

  private void initNestedMeta() {
    if (!inited) {
      vepNestedHeaderLine = vepMetadataParser.map(vcfFileReader.getFileHeader());
      gtNestedHeaderLine = genotypeMetadataMapper.map();
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfFileReader.getFileHeader(), vepNestedHeaderLine, gtNestedHeaderLine,
            metadataService, strict);
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
