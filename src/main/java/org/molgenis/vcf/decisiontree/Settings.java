package org.molgenis.vcf.decisiontree;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.Mode;

@Value
@Builder
public class Settings {

  Mode mode;
  Path inputVcfPath;
  AppSettings appSettings;
  WriterSettings writerSettings;
  boolean strict;
  SampleSettings sampleSettings;
}
