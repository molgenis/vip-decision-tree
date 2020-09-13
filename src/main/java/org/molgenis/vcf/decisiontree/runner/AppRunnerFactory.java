package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;

public interface AppRunnerFactory {

  AppRunner create(Settings settings);
}
