[![Build Status](https://app.travis-ci.com/molgenis/vip-decision-tree.svg?branch=main)](https://app.travis-ci.com/molgenis/vip-decision-tree)
[![Quality Status](https://sonarcloud.io/api/project_badges/measure?project=molgenis_vip-decision-tree&metric=alert_status)](https://sonarcloud.io/dashboard?id=molgenis_vip-decision-tree)

# Variant Interpretation Pipeline - VCF Decision Tree
Command-line application to classify variants in any VCF (Variant Call Format) file based on a
decision tree.

# Installation
Generate a personal access token in GitHub with at least the scope "read:packages".

Then add a settings.xml to your Maven .m2 folder, or edit it if you already have one. It should
contain the following:
```
<?xml version="1.0"?>

<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>
  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          </repository>
          <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/molgenis/vip-utils</url>
            <snapshots>
              <enabled>true</enabled>
            </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>[YOUR VIP USERNAME]</username>
      <password>[YOUR PERSONAL ACCESS TOKEN]</password>
    </server>
   </servers>
</settings>
```

## Requirements

- Java 21

## Usage

```
usage: java -jar vcf-decision-tree.jar -i <arg> -c <arg> [-m <arg>] [-o <arg>] [-f]
       [-s] [-l] [-p] [-d] [-pb <arg>] [-pd <arg>] [-ph <arg>] [-m <arg>]
 -i,--input <arg>         VEP* annotated input VCF file.
 -m,--metadata <arg>      VCF metadata file (.json).
 -c,--config <arg>        Input decision tree file (.json).
 -o,--output <arg>        Output VCF file (.vcf or .vcf.gz).
 -f,--force               Override the output file if it already exists.
 -s,--strict              Throw exception if field from the decision tree
                          is missing entirely in the input VCF.
 -l,--labels              Write decision tree outcome labels to output VCF
                          file.
 -p,--path                Write decision tree node path to output VCF
                          file.
 -d,--debug               Enable debug mode (additional logging).
 -pb,--probands <arg>     Comma-separated list of proband names.
 -pd,--pedigree <arg>     Comma-separated list of pedigree files (.ped).
 -ph,--phenotypes <arg>   Comma-separated list of sample-phenotypes (e.g.
                          HP:123 or HP:123;HP:234 or
                          sample0/HP:123,sample1/HP:234). Phenotypes are
                          CURIE formatted (prefix:reference) and separated
                          by a semicolon.
 -m,--mode <arg>          Run mode: 'variant' (default) or 'sample',
                          'sample' mode classifies provided probands, or
                          all samples if no probands given.

usage: java -jar vcf-decision-tree.jar -v
 -v,--version   Print version.
```

*:[VEP](https://www.ensembl.org/info/docs/tools/vep/index.html)

## Examples
```
java -jar vcf-decision-tree.jar -i my.vcf -m field_metadata.json -c decision_tree.json -o out.vcf
java -jar vcf-decision-tree.jar -i my.vcf.gz -m field_metadata.json -c decision_tree.json -o out.vcf.gz
java -jar vcf-decision-tree.jar -i my.vcf.gz -m field_metadata.json -c decision_tree.json -o out.vcf.gz -f -l -p
java -jar vcf-decision-tree.jar -v
```

## Decision Tree

Each variant is classified using a decision tree which consists of decision nodes and leaf nodes.

Decision nodes perform a test on the variant which determines the outcome consisting of the next
node to process and optionally a label. Leaf nodes are terminal nodes that determine the class for a
variant.

### Supported Fields

#### Standard VCF

COMMON, INFO, FORMAT

#### Customized fields

##### INFO_VEP

Any field in the VEP value can be used, if the field is unknown to the tool it is interpreted as a
singel value string field.

##### GENOTYPE

This fieldtype uses the information provided by htsjdk about the Genotype FORMAT field.

Allowed values are:

- ALLELES: The alleles (list of strings) present in the genotype.
- ALLELE_NUM: The allele numbers corresponding with the index in the VCF ALT field.
- TYPE: The htsjdk genotype type, possible values: MIXED, HET, HOM_REF, HOM_VAR, NO_CALL,
  UNAVAILABLE.
- CALLED: Boolean indication if the genotype for this sample is called.
- MIXED: Boolean indication if the genotype is comprised of both calls and no-calls.
- NON_INFORMATIVE: Boolean that returns true if all samples PLs are 0.
- PHASED: Boolean indicating the genotype was called phased or unphased.
- PLOIDY: The ploidy of the genotype as an integer, null if no call is present.

##### SAMPLE

This fieldtype is used to query properties of the samples, like phenotypes and pedigree information
which are provided outside the VCF.

Allowed values are:

- ID: The sample identifier.
- AFFECTED_STATUS: The affected status of the sample, possible values: AFFECTED, UNAFFECTED,
  MISSING.
- SEX: The sex of the sample, possible values: MALE, FEMALE, UNKNOWN.
- FATHER_ID: The identifier for the father sample.
- MOTHER_ID: The identifier for the mother sample.
- FAMILY_ID: The identifier for the family.
- PHENOTYPES: The list of phenotypes for the sample.

#### Modes

##### Variant classification (default)

In the variant classification mode the tool will output a classification per VEP value (CSQ), this
classification will be added to the VEP value under the key "VIPC".

Optionally labels and the path through the tree can be annotated to the VEP value as well.

##### Sample classification

In the sample classification mode the tool will output a classification per VEP value as a comma
separated list for which the index corresponds to the VEP value index, this classification will be
added to the FORMAT fields value under the key "VIPC_S".

Optionally labels and the path through the tree can be annotated to the FORMAT fields as well.

### Example

see `src/test/resources/example.json` and `src/test/resources/example_sample.json`

## Output VCF

Variant classifications and optionally their paths and labels are annotated on the input VCF in the
VIPC, VIPP and VIPL info fields.

### Example

see `src/test/resources/example-classified.vcf`
see `src/test/resources/example-classified_paths-labels.vcf`
see `src/test/resources/example_sample-classified.vcf`
see `src/test/resources/example_sample-classified_paths-labels.vcf`
