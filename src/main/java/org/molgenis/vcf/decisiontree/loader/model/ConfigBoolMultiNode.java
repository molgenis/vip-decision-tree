package org.molgenis.vcf.decisiontree.loader.model;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.NodeType;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigBoolMultiNode implements ConfigNode {

  @NonNull Type type = Type.BOOL_MULTI;

  String description;

  @NonNull NodeType nodeType = NodeType.DECISION;
  @NonNull String id;
  String label;

  @NonNull DecisionType decisionType = DecisionType.BOOL;
  @NonNull List<String> fields;
  @NonNull List<ConfigBoolMultiQuery> outcomes;

  @NonFinal
  @Setter
  ConfigNodeOutcome outcomeMissing;
  @NonFinal
  @Setter
  ConfigNodeOutcome outcomeDefault;
}
