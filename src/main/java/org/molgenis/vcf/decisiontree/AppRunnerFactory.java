package org.molgenis.vcf.decisiontree;

interface AppRunnerFactory {
  AppRunner create(Settings settings);
}
