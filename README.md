[![Build Status](https://app.travis-ci.com/molgenis/vip-decision-tree.svg?branch=feat/annotation)](https://app.travis-ci.com/molgenis/vip-decision-tree)
[![Quality Status](https://sonarcloud.io/api/project_badges/measure?project=molgenis_vip-decision-tree&metric=alert_status)](https://sonarcloud.io/dashboard?id=molgenis_vip-decision-tree)

# Variant Interpretation Pipeline - GREEN-VARAN score annotator
Command-line application to calculate and annotate a variant score specialized for non-coding variants.

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

- Java 17

## Usage

```
usage: java -jar vcf-decision-tree.jar -i <arg> [-o <arg>] [-f]
       [-d] 
 -i,--input <arg>         VIP-non-coding* annotated input VCF file.
 -o,--output <arg>        Output VCF file (.vcf or .vcf.gz).
 -f,--force               Override the output file if it already exists.
 -d,--debug               Enable debug mode (additional logging).

usage: java -jar vcf-decision-tree.jar -v
 -v,--version   Print version.
```

*:[VIP-non-coding](https://github.com/molgenis/vip/tree/feat/non-coding)

## Examples

```
java -jar vcf-decision-tree.jar -i /Users/jonathan/Documents/Afstudeeropdracht/Data/vip_output/test_result/snv.vcf.gz -o out.vcf -f -d 
java -jar vcf-decision-tree.jar -i /Users/jonathan/Documents/Afstudeeropdracht/Data/vip_output/test_result/lp.vcf.gz -o out.vcf
```

### Supported Fields

Standard VCF
COMMON, INFO, FORMAT

Customized fields
INFO_VEP
Any field in the VEP value can be used, if the field is unknown to the tool it is interpreted as a singel value string field.

GENOTYPE
This fieldtype uses the information provided by htsjdk about the Genotype FORMAT field.

Allowed values are:

ALLELES: The alleles (list of strings) present in the genotype.
ALLELE_NUM: The allele numbers corresponding with the index in the VCF ALT field.
TYPE: The htsjdk genotype type, possible values: MIXED, HET, HOM_REF, HOM_VAR, NO_CALL, UNAVAILABLE.
CALLED: Boolean indication if the genotype for this sample is called.
MIXED: Boolean indication if the genotype is comprised of both calls and no-calls.
NON_INFORMATIVE: Boolean that returns true if all samples PLs are 0.
PHASED: Boolean indicating the genotype was called phased or unphased.
PLOIDY: The ploidy of the genotype as an integer, null if no call is present.
SAMPLE
This fieldtype is used to query properties of the samples, like phenotypes and pedigree information which are provided outside the VCF.

Allowed values are:

ID: The sample identifier.
AFFECTED_STATUS: The affected status of the sample, possible values: AFFECTED, UNAFFECTED, MISSING.
SEX: The sex of the sample, possible values: MALE, FEMALE, UNKNOWN.
FATHER_ID: The identifier for the father sample.
MOTHER_ID: The identifier for the mother sample.
FAMILY_ID: The identifier for the family.
PHENOTYPES: The list of phenotypes for the sample.

#### Standard VCF

#### Standard VCF

COMMON, INFO, FORMAT

#### Customized fields

##### INFO_VEP

Any field in the VEP value can be used, if the field is unknown to the tool it is interpreted as a
singel value string field.

#### Modes

##### Variant annotation

The tool will calculate a score of 0 to 3 based on vallues that are annotated by [VIP-non-coding](https://github.com/molgenis/vip/tree/feat/non-coding)
The scoring system is based on the scoring table from [GREEN-DB](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8934622/) 

The scoring system works as follows:
```
level 0: When nothing is annotated
level 1: If the variants has overlap with a region (DNase, TFBS or UCNE) or has a constraint value above or equal to 0.7 or a score of ncER(>49.9) fathmm(>0.5) ReMM*(>0.5)
level 2: score of ncER(>49.9) fathmm(>0.5) ReMM*(>0.5) and overlap with a region or a constraint value above or equal to 0.7
level 3: level 2 + constraint region above or equal to 0.7
```

## Output VCF

Variant annotaions are annotated on the input VCF in the
XXX fields.

### Example

#### Input
see `see src/test/resources/example.vcf`

#### Output 
see `see src/test/resources/example-classified.vcf`