package org.molgenis.vcf.decisiontree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
    String treeConfigFile = ResourceUtils.getFile("classpath:example.json").toString();
    String outputFile = sharedTempDir.resolve("example-classified.vcf").toString();

    String[] args = {"-i", inputFile, "-c", treeConfigFile, "-o", outputFile};
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
    String treeConfigFile = ResourceUtils.getFile("classpath:example.json").toString();
    String outputFile = sharedTempDir.resolve("example-classified_paths-labels.vcf").toString();

    String[] args = {"-i", inputFile, "-c", treeConfigFile, "-o", outputFile, "-l", "-p"};
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
    String treeConfigFile = ResourceUtils.getFile("classpath:example_sample.json").toString();
    String outputFile = sharedTempDir.resolve("example-classified.vcf").toString();

    String[] args = {"-i", inputFile, "-c", treeConfigFile, "-o", outputFile, "-pb", "Patient",
        "-ph", "HP:0000951"};
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
}
