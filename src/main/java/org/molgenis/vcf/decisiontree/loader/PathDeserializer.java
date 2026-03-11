package org.molgenis.vcf.decisiontree.loader;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Path;

public class PathDeserializer extends JsonDeserializer<Path> {
  private final Path rootPath;

  public PathDeserializer(Path decisionTreeConfigPath) {
    this.rootPath = requireNonNull(decisionTreeConfigPath);
  }

  @Override
  public Path deserialize(JsonParser jp, DeserializationContext context) throws IOException {
    ObjectCodec objectCodec = jp.getCodec();
    ObjectNode root = objectCodec.readTree(jp);
    String pathString = root.get("path").asText();
    Path path = Path.of(pathString);
    if (rootPath.getParent() == null) {
      throw new IllegalArgumentException(
          "Unable to determine directory containing the decision tree.");
    }
    if (!path.isAbsolute()) {
      path = Path.of(rootPath.getParent().toString(), pathString).normalize();
    }
    return path;
  }
}
