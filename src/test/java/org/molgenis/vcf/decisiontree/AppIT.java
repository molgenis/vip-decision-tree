package org.molgenis.vcf.decisiontree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.molgenis.vcf.decisiontree.visualizer.Visualizer;
import org.springframework.boot.SpringApplication;
import org.springframework.util.ResourceUtils;

class AppIT {

  private static final Pattern HEADER_VERSION_PATTERN = Pattern
      .compile("^##VIP_treeVersion=.*?$", Pattern.MULTILINE);
  private static final Pattern HEADER_COMMAND_PATTERN = Pattern
      .compile("^##VIP_treeCommand=.*?$", Pattern.MULTILINE);

  @TempDir
  Path sharedTempDir;

  @Test
  void test() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example.vcf").toString();
    String metadataFile = ResourceUtils.getFile("classpath:field_metadata.json").toString();
    String treeConfigFile = ResourceUtils.getFile("classpath:example.json").toString();
    String outputFile = sharedTempDir.resolve("example-classified.vcf").toString();

    String[] args = {"-i", inputFile, "-m", metadataFile, "-c", treeConfigFile, "-o", outputFile};
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    // output differs every run (different tmp dir)
    outputVcf = HEADER_VERSION_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeVersion=");
    outputVcf = HEADER_COMMAND_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeCommand=");

    Path expectedOutputFile = ResourceUtils.getFile("classpath:example-classified.vcf").toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }

  @Test
  void testIncludePathsAndLabels() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example.vcf").toString();
    String metadataFile = ResourceUtils.getFile("classpath:field_metadata.json").toString();
    String treeConfigFile = ResourceUtils.getFile("classpath:example.json").toString();
    String outputFile = sharedTempDir.resolve("example-classified_paths-labels.vcf").toString();

    String[] args = {"-i", inputFile, "-m", metadataFile, "-c", treeConfigFile, "-o", outputFile, "-l", "-p"};
    SpringApplication.run(App.class, args);

    String outputVcf =
        Files.readString(Path.of(outputFile));

    // output differs every run due (different tmp dir)
    outputVcf = HEADER_VERSION_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeVersion=");
    outputVcf = HEADER_COMMAND_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeCommand=");

    Path expectedOutputFile = ResourceUtils.getFile("classpath:example-classified_paths-labels.vcf")
        .toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");
    assertEquals(expectedOutputVcf, outputVcf);
  }


  @Test
  void testSamples() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example_samples.vcf").toString();
    String metadataFile = ResourceUtils.getFile("classpath:field_metadata.json").toString();
    String treeConfigFile = ResourceUtils.getFile("classpath:example_sample.json").toString();
    String pedFile = ResourceUtils.getFile("classpath:example_samples.ped").toString();
    String outputFile = sharedTempDir.resolve("example-classified.vcf").toString();

    String[] args = {"-i", inputFile, "-m", metadataFile, "-c", treeConfigFile, "-o", outputFile, "-pb", "Patient",
        "-t", "samPlE", "-ph", "HP:0000951;HP:0003124", "-pd", pedFile
    };
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    // output differs every run (different tmp dir)
    outputVcf = HEADER_VERSION_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeVersion=");
    outputVcf = HEADER_COMMAND_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeCommand=");

    Path expectedOutputFile = ResourceUtils.getFile("classpath:example_samples-classified.vcf")
        .toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }

  @Test
  void testSamplesPathLabels() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example_samples.vcf").toString();
    String metadataFile = ResourceUtils.getFile("classpath:field_metadata.json").toString();
    String treeConfigFile = ResourceUtils.getFile("classpath:example_sample.json").toString();
    String outputFile = sharedTempDir.resolve("example-classified.vcf").toString();

    String[] args = {"-i", inputFile, "-m", metadataFile, "-c", treeConfigFile, "-o", outputFile, "-pb", "Patient",
        "-l", "-p", "-t", "sAMPlE", "-ph", "HP:0000951;HP:0003124",};
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    // output differs every run (different tmp dir)
    outputVcf = HEADER_VERSION_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeVersion=");
    outputVcf = HEADER_COMMAND_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeCommand=");

    Path expectedOutputFile = ResourceUtils.getFile(
            "classpath:example_samples-classified_paths-labels.vcf")
        .toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }

  @Test
  void testSamplesNoProbandPhenoPed() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example_samples.vcf").toString();
    String metadataFile = ResourceUtils.getFile("classpath:field_metadata.json").toString();
    String treeConfigFile = ResourceUtils.getFile("classpath:example_sample.json").toString();
    String outputFile = sharedTempDir.resolve("example-classified-noPPP.vcf").toString();

    String[] args = {"-i", inputFile, "-m", metadataFile, "-c", treeConfigFile, "-o", outputFile,
        "-t", "samPlE", "-l", "-p",
    };
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    // output differs every run (different tmp dir)
    outputVcf = HEADER_VERSION_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeVersion=");
    outputVcf = HEADER_COMMAND_PATTERN.matcher(outputVcf).replaceAll("##VIP_treeCommand=");

    Path expectedOutputFile = ResourceUtils.getFile(
            "classpath:example_samples-classified-noPPP.vcf")
        .toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }

  @Test
  void testVisualize() throws IOException {
    String treeConfigFile = ResourceUtils.getFile("classpath:example.json").toString();
    Path outputFile = sharedTempDir.resolve("actual.html");

    String[] args = {"-i", treeConfigFile, "-m", "-o", outputFile.toString()};
    Visualizer.main(args);

    Path expectedOutputFile = ResourceUtils.getFile(
                    "classpath:expected.html")
            .toPath();
    String expectedOutput = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");
    String actualOutput = Files.readString(outputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutput, actualOutput);
  }
}
