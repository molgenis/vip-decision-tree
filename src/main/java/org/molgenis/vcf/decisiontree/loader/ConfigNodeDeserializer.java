package org.molgenis.vcf.decisiontree.loader;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigCategoricalNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigLeafNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;

public class ConfigNodeDeserializer extends JsonDeserializer<ConfigNode> {
  @Override
  public ConfigNode deserialize(JsonParser jp, DeserializationContext context) throws IOException {
    ObjectCodec objectCodec = jp.getCodec();
    ObjectNode root = objectCodec.readTree(jp);

    if (!root.has("type")) {
      throw new JsonMappingException(jp, "missing 'type' property");
    }

    ConfigNode configNode;
    String type = root.get("type").asText();
    switch (type.toUpperCase()) {
      case "BOOL":
        configNode = objectCodec.treeToValue(root, ConfigBoolNode.class);
        break;
      case "CATEGORICAL":
        configNode = objectCodec.treeToValue(root, ConfigCategoricalNode.class);
        break;
      case "LEAF":
        configNode = objectCodec.treeToValue(root, ConfigLeafNode.class);
        break;
      default:
        throw new JsonMappingException(
            jp,
            format("illegal 'type' value '%s' (allowed values: BOOL, CATEGORICAL, LEAF).", type));
    }
    return configNode;
  }
}
