package org.molgenis.vcf.decisiontree.loader.model;

import java.nio.file.Path;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NonFinal
public class ConfigDecisionTree {

  @NonNull String rootNode;
  @NonNull Map<String, ConfigNode> nodes;
  Map<String, ConfigLabel> labels;
  Map<String, Path> files;
}
