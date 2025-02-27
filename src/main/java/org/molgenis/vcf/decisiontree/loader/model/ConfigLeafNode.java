package org.molgenis.vcf.decisiontree.loader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigLeafNode implements ConfigNode {
  @NonNull Type type = Type.LEAF;

  String description;
  String label;

  @JsonProperty("class")
  String clazz;
}
