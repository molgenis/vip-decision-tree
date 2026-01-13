package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;

import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.runner.VepHelper;
import org.molgenis.vcf.decisiontree.runner.info.*;

/**
 * {@link VCFFileReader} wrapper that works with nested metadata and data (e.g. CSQ INFO fields).
 */
public class VcfReader implements AutoCloseable {

  private final VCFFileReader vcfFileReader;
  private final VepMetadataMapper vepMetadataMapper;
  private final boolean strict;
  private final GenotypeMetadataMapper genotypeMetadataMapper;
  private final FormatMetadataMapper formatMetadataMapper;
  private boolean inited = false;
  private NestedHeaderLine vepNestedHeaderLine = null;
  private NestedHeaderLine gtNestedHeaderLine = null;
  private Map<String, Field> formatNestedHeaderLine = null;

  public VcfReader(VCFFileReader vcfFileReader, VepMetadataMapper vepMetadataMapper,
      GenotypeMetadataMapper genotypeMetadataMapper, FormatMetadataMapper formatMetadataMapper,
      boolean strict) {
    this.vcfFileReader = requireNonNull(vcfFileReader);
    this.vepMetadataMapper = requireNonNull(vepMetadataMapper);
    this.genotypeMetadataMapper = requireNonNull(genotypeMetadataMapper);
    this.formatMetadataMapper = requireNonNull(formatMetadataMapper);
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
      formatNestedHeaderLine =  formatMetadataMapper.map(vcfFileReader.getFileHeader());
      inited = true;
    }
  }

  public Stream<VcfRecord> stream() {
    return StreamSupport.stream(vcfFileReader.spliterator(), false).map(VcfRecord::new);
  }

  public VcfMetadata getMetadata() {
    initNestedMeta();
    return new VcfMetadata(vcfFileReader.getFileHeader(), vepNestedHeaderLine, gtNestedHeaderLine,
            new NestedFormatHeaderLine(formatMetadataMapper.map(vcfFileReader.getHeader())), strict);
  }

  @Override
  public void close() {
    vcfFileReader.close();
  }
}
