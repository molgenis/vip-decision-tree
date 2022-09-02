package org.molgenis.vcf.decisiontree.loader;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigCategoricalNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode.Type;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;
import org.molgenis.vcf.decisiontree.loader.model.Edge;
import org.molgenis.vcf.decisiontree.loader.model.Node;

public class Visualizer {

  static Map<String, AtomicInteger> paths = new HashMap<>();

  public static void main(String[] args) {
    paths.put("", new AtomicInteger(0));
    ConfigDecisionTreeValidator validator = new ConfigDecisionTreeValidatorImpl();
    ConfigDecisionTreeLoader loader = new ConfigDecisionTreeLoaderImpl(validator);
    ConfigDecisionTree tree = loader.load(Path.of(
        "C:\\Users\\bartc\\Documents\\git\\vip-decision-tree\\src\\test\\resources\\example.json"));

    List<Node> nodes = new ArrayList<>();
    Map<String, Edge> edges = new HashMap<>();

    for (Entry<String, ConfigNode> entry : tree.getNodes().entrySet()) {
      nodes.add(new Node(entry.getKey(), entry.getValue().getType() == Type.LEAF ? entry.getKey()
          : entry.getValue().getDescription(),
          getCount(entry.getKey())));
      ConfigNode node = entry.getValue();
      if (node.getType() == Type.BOOL) {
        ConfigBoolNode boolNode = (ConfigBoolNode) node;
        Map<String, String> boolOutcomes = new HashMap<>();
        boolOutcomes.put("true", boolNode.getOutcomeTrue().getNextNode());
        boolOutcomes.put("false", boolNode.getOutcomeFalse().getNextNode());
        boolOutcomes.put("missing", boolNode.getOutcomeMissing().getNextNode());
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
    visualize(nodes, edges);
  }

  private static Integer getCount(String key) {
    int count = 0;
    for (Entry<String, AtomicInteger> entry : paths.entrySet()) {
      List<String> path = Arrays.asList(entry.getKey().split("\\|"));
      if (path.contains(key)) {
        count += entry.getValue().get();
      }
    }
    return count;
  }

  private static void visualize(List<Node> nodes, Map<String, Edge> edges) {
    for (String pathString : paths.keySet()) {
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
            "C:\\Users\\bartc\\Documents\\git\\vip-decision-tree\\src\\main\\resources\\template.html"));
        String output = template.replace("DAGRE_GOES_HERE", html.toString());
        Files.writeString(Path.of(
                "C:\\Users\\bartc\\Documents\\git\\vip-decision-tree\\src\\main\\resources\\tree.html"
            ),
            output);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static String edgeToHtml(Edge edge) {
    return String
        .format("g.setEdge(\"%s\", \"%s\", {label: \"%s\"});", edge.getNode1(), edge.getNode2(),
            edge.getLabel());
  }

  private static String nodeToHtml(Node node) {
    return String.format("g.setNode(\"%s\", {label: \"%s\"});", node.getId(),
        String.format("%s", node.getLabel()));
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