package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.GenotypeMetadataMapper;
import org.molgenis.vcf.decisiontree.runner.info.MissingVepException;
import org.molgenis.vcf.decisiontree.runner.info.NestedHeaderLine;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataMapper;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final VepMetadataMapper vepMetadataMapper;
  private final boolean strict;
  private final GenotypeMetadataMapper genotypeMetadataMapper;
  private boolean inited = false;
  private NestedHeaderLine vepNestedHeaderLine = null;
  private NestedHeaderLine gtNestedHeaderLine = null;

  public VcfReader(VCFFileReader vcfFileReader, VepMetadataMapper vepMetadataMapper,
      GenotypeMetadataMapper genotypeMetadataMapper,
      boolean strict) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.vepMetadataMapper = requireNonNull(vepMetadataMapper);
    this.genotypeMetadataMapper = requireNonNull(genotypeMetadataMapper);
    this.strict = strict;
  }

  private void initNestedMeta() {
    if (!inited) {
      String vepID = VepHelper.getVepId(vcfFileReader.getHeader());
      if(vepID == null) {
        throw new MissingVepException();
      }
      vepNestedHeaderLine = vepMetadataMapper.map(vepID, vcfFileReader.getFileHeader());
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
        strict);
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
