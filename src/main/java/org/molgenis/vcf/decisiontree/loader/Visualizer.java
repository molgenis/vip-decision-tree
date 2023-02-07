package org.molgenis.vcf.decisiontree.loader;

import static org.molgenis.vcf.decisiontree.loader.model.ConfigNode.Type.LEAF;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigCategoricalNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigExistsNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode.Type;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;
import org.molgenis.vcf.decisiontree.loader.model.Edge;
import org.molgenis.vcf.decisiontree.loader.model.Node;

public class Visualizer {

  static Map<String, AtomicInteger> paths = new HashMap<>();

  public static void main(String[] args) {
    Path path = Path.of(args[0]);
    String filename = path.getFileName().toString();
    paths.put("", new AtomicInteger(0));
    ConfigDecisionTreeValidator validator = new ConfigDecisionTreeValidatorImpl();
    ConfigDecisionTreeLoader loader = new ConfigDecisionTreeLoaderImpl(validator);
    ConfigDecisionTree tree = loader.load(
        path);

    List<Node> nodes = new ArrayList<>();
    Map<String, Edge> edges = new HashMap<>();

    for (Entry<String, ConfigNode> entry : tree.getNodes().entrySet()) {
      nodes.add(
          new Node(entry.getKey(),
              entry.getValue().getDescription() != null ? entry.getValue().getDescription()
                  : entry.getKey(), entry.getValue().getType() == LEAF));
      ConfigNode node = entry.getValue();
      if (node.getType() == Type.BOOL) {
        ConfigBoolNode boolNode = (ConfigBoolNode) node;
        Map<String, String> boolOutcomes = new HashMap<>();
        boolOutcomes.put("true", boolNode.getOutcomeTrue().getNextNode());
        boolOutcomes.put("false", boolNode.getOutcomeFalse().getNextNode());
        boolOutcomes.put("missing", boolNode.getOutcomeMissing().getNextNode());
        processOutcomes(edges, entry, boolOutcomes);
      } else if (node.getType() == Type.EXISTS) {
        ConfigExistsNode boolNode = (ConfigExistsNode) node;
        Map<String, String> boolOutcomes = new HashMap<>();
        boolOutcomes.put("true", boolNode.getOutcomeTrue().getNextNode());
        boolOutcomes.put("false", boolNode.getOutcomeFalse().getNextNode());
        processOutcomes(edges, entry, boolOutcomes);
      } else if (node.getType() == Type.BOOL_MULTI) {
        ConfigBoolMultiNode boolMultiNode = (ConfigBoolMultiNode) node;
        Map<String, String> boolOutcomes = new HashMap<>();
        for (ConfigBoolMultiQuery configBoolMultiQuery : boolMultiNode.getOutcomes()) {
          boolOutcomes.put(configBoolMultiQuery.getDescription(),
              configBoolMultiQuery.getOutcomeTrue().getNextNode());
        }
        boolOutcomes.put("default", boolMultiNode.getOutcomeDefault().getNextNode());
        boolOutcomes.put("missing", boolMultiNode.getOutcomeMissing().getNextNode());
        processOutcomes(edges, entry, boolOutcomes);
      } else if (node.getType() == Type.CATEGORICAL) {
        ConfigCategoricalNode categoricalNode = (ConfigCategoricalNode) node;
        Map<String, String> categoricalOutcomes = new HashMap<>();
        if (categoricalNode.getOutcomeDefault() != null) {
          categoricalOutcomes.put("default", categoricalNode.getOutcomeDefault().getNextNode());
        }
        for (Entry<String, ConfigNodeOutcome> configEntry : categoricalNode.getOutcomeMap()
            .entrySet()) {
          categoricalOutcomes.put(configEntry.getKey(), configEntry.getValue().getNextNode());
        }
        processOutcomes(edges, entry, categoricalOutcomes);
      }
    }
    visualizeHtml(nodes, edges, filename);
    visualizeMermaid(nodes, edges, filename);
  }

  private static void visualizeHtml(List<Node> nodes, Map<String, Edge> edges, String filename) {
    StringBuilder html = new StringBuilder();
    for (Node node : nodes) {
      html.append(nodeToHtml(node));
      html.append("\n");
    }
    for (Edge edge : edges.values()) {
      html.append(edgeToHtml(edge));
      html.append("\n");
    }
    try {
      String template = Files.readString(Path.of(
          "src\\main\\resources\\template.html"));
      String output = template.replace("DAGRE_GOES_HERE", html.toString());
      Files.writeString(Path.of(
              "src\\main\\resources\\" + filename + ".html"
          ),
          output);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void visualizeMermaid(List<Node> nodes, Map<String, Edge> edges, String filename) {
    StringBuilder mmdContent = new StringBuilder();
    mmdContent.append("flowchart TD\n");
    for (Node node : nodes) {
      mmdContent.append(nodeToMmd(node));
      mmdContent.append("\n");
    }
    for (Edge edge : edges.values()) {
      mmdContent.append(edgeToMmd(edge));
      mmdContent.append("\n");
    }
    try {
      Files.writeString(Path.of(
              "src\\main\\resources\\" + filename + ".mmd"
          ),
          mmdContent.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String edgeToHtml(Edge edge) {
    String label =
        edge.getLabel() != null ? edge.getLabel() : "Add description to visualize a label.";
    return String
        .format("g.setEdge(\"%s\", \"%s\", {label: \"%s\"});", edge.getNode1(), edge.getNode2(),
            label);
  }

  private static String nodeToHtml(Node node) {
    String htmlNode;
    if (node.isLeaf()) {
      htmlNode = String.format("g.setNode(\"%s\", {label: \"%s\", style: \"fill: #00ff00\"});",
          node.getId(),
          String.format("%s", node.getLabel()));
    } else {
      htmlNode = String.format("g.setNode(\"%s\", {label: \"%s\", style: \"fill: #33ccff\"});",
          node.getId(),
          String.format("%s", node.getLabel()));
    }
    return htmlNode;
  }

  private static String edgeToMmd(Edge edge) {
    String label =
        edge.getLabel() != null ? edge.getLabel() : "Add description to visualize a label.";
    return String
        .format("%s_ -->|%s| %s_", edge.getNode1(), label, edge.getNode2());
  }

  private static String nodeToMmd(Node node) {
    return String.format("%s_([%s])", node.getId(), node.getLabel());
  }

  private static void processOutcomes(Map<String, Edge> edges, Entry<String, ConfigNode> entry,
      Map<String, String> outcomes) {
    for (Entry<String, String> outcome : outcomes.entrySet()) {
      String id = String.format("%s_%s", entry.getKey(), outcome.getValue());
      String label = outcome.getKey();
      if (edges.containsKey(id)) {
        Edge edge = edges.get(id);
        String oldLabel = edge.getLabel();
        label = oldLabel + "\\n" + outcome.getKey();
      }
      edges.put(id, new Edge(entry.getKey(), outcome.getValue(), label));
    }
  }
}