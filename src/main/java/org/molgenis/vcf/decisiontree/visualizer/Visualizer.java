package org.molgenis.vcf.decisiontree.visualizer;

import static java.lang.String.format;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigNode.Type.LEAF;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.cli.*;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeLoader;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeLoaderImpl;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeValidator;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeValidatorImpl;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigCategoricalNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigExistsNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode.Type;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;
import org.molgenis.vcf.decisiontree.visualizer.model.Edge;
import org.molgenis.vcf.decisiontree.visualizer.model.Node;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Visualizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Visualizer.class);
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String DEFAULT = "default";
    public static final String MISSING = "missing";
    public static final String OPT_INPUT = "i";
    public static final String OPT_INPUT_LONG = "input";
    public static final String OPT_OUTPUT = "o";
    public static final String OPT_OUTPUT_LONG = "output";
    public static final String OPT_MERMAID = "m";
    public static final String OPT_MERMAID_LONG = "mermaid";
    public static final String OPT_FORCE = "f";
    public static final String OPT_FORCE_LONG = "force";
    private static final int STATUS_COMMAND_LINE_USAGE_ERROR = 64;
    public static final String JSON = ".json";

    public static void main(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        Path inputPath = Path.of(commandLine.getOptionValue(OPT_INPUT));
        Path outputPath = commandLine.hasOption(OPT_OUTPUT) ? Path.of(commandLine.getOptionValue(OPT_OUTPUT))
                : Path.of(commandLine.getOptionValue(OPT_INPUT).replace(JSON, ".html"));
        String filename = inputPath.getFileName().toString();
        boolean isMermaidEnabled = commandLine.hasOption(OPT_MERMAID);

        ConfigDecisionTreeValidator validator = new ConfigDecisionTreeValidatorImpl();
        ConfigDecisionTreeLoader loader = new ConfigDecisionTreeLoaderImpl(validator);
        ConfigDecisionTree tree = loader.load(
                inputPath);

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        for (Entry<String, ConfigNode> entry : tree.getNodes().entrySet()) {
            nodes.add(
                    new Node(entry.getKey(),
                            entry.getValue().getLabel(), entry.getValue().getType() == LEAF));
            ConfigNode node = entry.getValue();
            createEdges(edges, entry, node);
        }
        visualizeHtml(nodes, edges, outputPath, filename, isMermaidEnabled);
        LOGGER.info("Decision tree visualization written to '{}'", outputPath);
    }

    private static CommandLine getCommandLine(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = null;
        Options options = new Options();
        options.addOption(Option.builder(OPT_INPUT).longOpt(OPT_INPUT_LONG).desc("Input .json decision tree file.").hasArg().required().build());
        options.addOption(Option.builder(OPT_OUTPUT).longOpt(OPT_OUTPUT_LONG).desc("Output .html file.").hasArg().build());
        options.addOption(Option.builder(OPT_FORCE).longOpt(OPT_FORCE_LONG).desc("Overwrite output file.").build());
        options.addOption(Option.builder(OPT_MERMAID).longOpt(OPT_MERMAID_LONG).desc("Also output mermaid text file.").build());

        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            logException(options);
            System.exit(STATUS_COMMAND_LINE_USAGE_ERROR);
        }
        validateCommandLine(commandLine);
        return commandLine;
    }

    private static void createEdges(List<Edge> edges, Entry<String, ConfigNode> entry, ConfigNode node) {
        if (node.getType() == Type.BOOL) {
            ConfigBoolNode boolNode = (ConfigBoolNode) node;
            Map<String, String> boolOutcomes = new HashMap<>();
            boolOutcomes.put(TRUE, boolNode.getOutcomeTrue().getNextNode());
            boolOutcomes.put(FALSE, boolNode.getOutcomeFalse().getNextNode());
            boolOutcomes.put(MISSING, boolNode.getOutcomeMissing().getNextNode());
            processOutcomes(edges, entry, boolOutcomes);
        } else if (node.getType() == Type.EXISTS) {
            ConfigExistsNode boolNode = (ConfigExistsNode) node;
            Map<String, String> boolOutcomes = new HashMap<>();
            boolOutcomes.put(TRUE, boolNode.getOutcomeTrue().getNextNode());
            boolOutcomes.put(FALSE, boolNode.getOutcomeFalse().getNextNode());
            processOutcomes(edges, entry, boolOutcomes);
        } else if (node.getType() == Type.BOOL_MULTI) {
            Map<String, String> boolOutcomes = getBoolOutcomes((ConfigBoolMultiNode) node);
            processOutcomes(edges, entry, boolOutcomes);
        } else if (node.getType() == Type.CATEGORICAL) {
            Map<String, String> categoricalOutcomes = getCategoricalOutcomes((ConfigCategoricalNode) node);
            processOutcomes(edges, entry, categoricalOutcomes);
        }
    }

    private static Map<String, String> getBoolOutcomes(ConfigBoolMultiNode node) {
        Map<String, String> boolOutcomes = new HashMap<>();
        for (ConfigBoolMultiQuery configBoolMultiQuery : node.getOutcomes()) {
            boolOutcomes.put(configBoolMultiQuery.getDescription(),
                    configBoolMultiQuery.getOutcomeTrue().getNextNode());
        }
        boolOutcomes.put(DEFAULT, node.getOutcomeDefault().getNextNode());
        boolOutcomes.put(MISSING, node.getOutcomeMissing().getNextNode());
        return boolOutcomes;
    }

    private static Map<String, String> getCategoricalOutcomes(ConfigCategoricalNode node) {
        Map<String, String> categoricalOutcomes = new HashMap<>();
        if (node.getOutcomeDefault() != null) {
            categoricalOutcomes.put(DEFAULT, node.getOutcomeDefault().getNextNode());
        }
        for (Entry<String, ConfigNodeOutcome> configEntry : node.getOutcomeMap()
                .entrySet()) {
            categoricalOutcomes.put(configEntry.getKey(), configEntry.getValue().getNextNode());
        }
        return categoricalOutcomes;
    }

    private static void validateCommandLine(CommandLine commandLine) {
        validateInput(commandLine);
        validateOutput(commandLine);
    }

    private static void validateInput(CommandLine commandLine) {
        Path inputPath = Path.of(commandLine.getOptionValue(OPT_INPUT));
        if (!Files.exists(inputPath)) {
            throw new IllegalArgumentException(
                    format("Input file '%s' does not exist.", inputPath));
        }
        if (Files.isDirectory(inputPath)) {
            throw new IllegalArgumentException(
                    format("Input file '%s' is a directory.", inputPath));
        }
        if (!Files.isReadable(inputPath)) {
            throw new IllegalArgumentException(
                    format("Input file '%s' is not readable.", inputPath));
        }
        String inputPathStr = inputPath.toString();
        if (!inputPathStr.endsWith(JSON)) {
            throw new IllegalArgumentException(
                    format("Input file '%s' is not a .json file.", inputPathStr));
        }
    }

    private static void validateOutput(CommandLine commandLine) {
        String outputPathStr = commandLine.getOptionValue(OPT_OUTPUT);
        if (commandLine.hasOption(OPT_OUTPUT) && !outputPathStr.endsWith(".html")) {
            throw new IllegalArgumentException(
                    format("Output file '%s' is not a .html file.", outputPathStr));
        }
        Path outputPath = commandLine.hasOption(OPT_OUTPUT) ? Path.of(commandLine.getOptionValue(OPT_OUTPUT))
                : Path.of(commandLine.getOptionValue(OPT_INPUT).replace(JSON, ".html"));

        if (!commandLine.hasOption(OPT_FORCE) && Files.exists(outputPath)) {
            throw new IllegalArgumentException(
                    format("Output file '%s' already exists", outputPath));
        }
    }

    private static void visualizeHtml(List<Node> nodes, List<Edge> edges, Path outputPath, String title, boolean isMermaidEnabled) {
        StringBuilder mmdContent = new StringBuilder();
        mmdContent.append("flowchart TD\n");
        for (Node node : nodes) {
            mmdContent.append(nodeToMmd(node));
            mmdContent.append("\n");
            if (node.isLeaf()) {
                mmdContent.append(String.format("style %s_ fill:#00ff00%n", node.getId()));
            }
        }
        for (Edge edge : edges) {
            mmdContent.append(edgeToMmd(edge));
            mmdContent.append("\n");
        }
        try {
            Path templatePath = Paths.get(Objects.requireNonNull(Visualizer.class.getResource("/template-mmd.html")).toURI());
            String template = Files.readString(templatePath);
            String output = template
                    .replace("MERMAID_PLACEHOLDER", mmdContent.toString())
                    .replace("TITLE_PLACEHOLDER", title);
            Files.writeString(outputPath, output);
            if (isMermaidEnabled) {
                Files.writeString(Path.of(outputPath.toString().replace(".html", ".mmd")),
                        mmdContent.toString());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new TemplateMissingException();
        }
    }

    private static String edgeToMmd(Edge edge) {
        String label =
                edge.getLabel() != null ? edge.getLabel() : "Add description to visualize a label.";
        return String
                .format("%s_ -->|\"%s\"| %s_", edge.getNode1(), label, edge.getNode2());
    }

    private static String nodeToMmd(Node node) {
        return String.format("%s_(\"%s\")", node.getId(), node.getLabel());
    }

    private static void processOutcomes(List<Edge> edges, Entry<String, ConfigNode> entry,
                                        Map<String, String> outcomes) {
        for (Entry<String, String> outcome : outcomes.entrySet()) {
            edges.add(new Edge(entry.getKey(), outcome.getValue(), outcome.getKey()));
        }
    }

    private static void logException(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        String cmdLineSyntax = "java -jar vcf-decision-tree-visualizer.jar";
        formatter.printHelp(cmdLineSyntax, options, true);
    }
}