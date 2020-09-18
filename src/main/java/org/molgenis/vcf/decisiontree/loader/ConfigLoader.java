package org.molgenis.vcf.decisiontree.loader;

import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.loader.model.Config;

public interface ConfigLoader {
  Config load(Path decisionTreeConfigPath);
}
