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
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
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
    getPaths();
    ConfigDecisionTreeValidator validator = new ConfigDecisionTreeValidatorImpl();
    ConfigDecisionTreeLoader loader = new ConfigDecisionTreeLoaderImpl(validator);
    ConfigDecisionTree tree = loader.load(Path.of(
        "C:\\Users\\bartc\\Downloads\\decision_tree.json"));

    List<Node> nodes = new ArrayList<>();
    Map<String, Edge> edges = new HashMap<>();

    for (Entry<String, ConfigNode> entry : tree.getNodes().entrySet()) {
      nodes.add(new Node(entry.getKey(), entry.getKey() + ":" + entry.getValue().getDescription(),
          getCount(entry.getKey())));
      ConfigNode node = entry.getValue();
      if (node.getType() == Type.BOOL) {
        ConfigBoolNode boolNode = (ConfigBoolNode) node;
        Map<String, String> boolOutcomes = new HashMap<>();
        boolOutcomes.put("true", boolNode.getOutcomeTrue().getNextNode());
        boolOutcomes.put("false", boolNode.getOutcomeFalse().getNextNode());
        boolOutcomes.put("missing", boolNode.getOutcomeMissing().getNextNode());
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
    visualize(nodes, edges, paths);
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

  private static void visualize(List<Node> nodes, Map<String, Edge> edges,
      Map<String, AtomicInteger> paths) {
    for (String pathString : paths.keySet()) {
      List<String> path = Arrays.asList(pathString.split("\\|"));
      StringBuilder html = new StringBuilder();
      for (Node node : nodes) {
        boolean selected = path.contains(node.getId());
        html.append(nodeToHtml(node, selected));
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
            String.format(
                "C:\\Users\\bartc\\Documents\\git\\vip-decision-tree\\src\\main\\resources\\%s.html",
                String.format("Tree_%s", String.join("_", path)))),
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

  private static String nodeToHtml(Node node, boolean selected) {
    if (selected) {
      return String
          .format("g.setNode(\"%s\", {label: \"%s\", style: \"fill: yellow\"});", node.getId(),
              String.format("%s(%d)", node.getLabel(), node.getCount()));
    }
    return String.format("g.setNode(\"%s\", {label: \"%s\"});", node.getId(),
        String.format("%s(%d)", node.getLabel(), node.getCount()));
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

  private static Map<String, AtomicInteger> getPaths() {
    VCFFileReader reader = new VCFFileReader(Path.of(
        "C:\\Users\\bartc\\Documents\\git\\vip-decision-tree\\src\\test\\resources\\test_tree_out.vcf"),
        false);
    for (CloseableIterator<VariantContext> it = reader.iterator(); it.hasNext(); ) {
      VariantContext context = it.next();
      String path = context.getAttributeAsString("VIPP", "");
      if (!path.isEmpty()) {
        AtomicInteger counter;
        if (!paths.containsKey(path)) {
          counter = new AtomicInteger(0);
        } else {
          counter = paths.get(path);
        }
        counter.getAndIncrement();
        paths.put(path, counter);
      }
    }
    return paths;
  }
}