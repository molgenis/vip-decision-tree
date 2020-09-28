package org.molgenis.vcf.decisiontree.loader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Path;

public class PathDeserializer extends JsonDeserializer<Path> {
  @Override
  public Path deserialize(JsonParser jp, DeserializationContext context) throws IOException {
    ObjectCodec objectCodec = jp.getCodec();
    ObjectNode root = objectCodec.readTree(jp);
    String pathString = root.get("path").asText();
    return Path.of(pathString);
  }
}
