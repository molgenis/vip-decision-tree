[![Build Status](https://app.travis-ci.com/molgenis/vip-decision-tree.svg?branch=master)](https://app.travis-ci.com/molgenis/vip-decision-tree)
[![Quality Status](https://sonarcloud.io/api/project_badges/measure?project=molgenis_vip-decision-tree&metric=alert_status)](https://sonarcloud.io/dashboard?id=molgenis_vip-decision-tree)
# Variant Interpretation Pipeline - VCF Decision Tree
Command-line application to classify variants in any VCF (Variant Call Format) file based on a decision tree.
## Requirements
- Java 17

## Usage
```
usage: java -jar vcf-decision-tree.jar -i <arg> -c <arg> [-o <arg>] [-f]
       [-s] [-l] [-p] [-d] [-pb <arg>] [-pd <arg>] [-ph <arg>] [-m <arg>]
 -i,--input <arg>         VEP* annotated input VCF file.
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

Process finished with exit code 64

```

*:[VEP](https://www.ensembl.org/info/docs/tools/vep/index.html)

## Examples
```
java -jar vcf-decision-tree.jar -i my.vcf -c decision_tree.json -o out.vcf
java -jar vcf-decision-tree.jar -i my.vcf.gz -c decision_tree.json -o out.vcf.gz
java -jar vcf-decision-tree.jar -i my.vcf.gz -c decision_tree.json -o out.vcf.gz -f -l -p
java -jar vcf-decision-tree.jar -v
```

## Decision Tree

Each variant is classified using a decision tree which consists of decision nodes and leaf nodes.

Decision nodes perform a test on the variant which determines the outcome consisting of the next
node to process and optionally a label. Leaf nodes are terminal nodes that determine the class for a
variant.

### Supported Fields

#### VCF

COMMON, INFO, /**

* INFO field with nested information (VEP CSQ)
  */, FORMAT, /**
* FORMAT field with nested information (GENOTYPE info from htsjdk)
*
* Allowed values: {@link GenotypeFieldType}

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

#### Custom

##### INFO_VEP

Any field in the VEP value can be used, if the field is unknown to the tool it is interpreted as a
singel value string field.

##### FORMAT_GENOTYPE

This fieldtype uses the information provided by htsjdk about the Genotype FORMAT field.

Allowed values are:

- ALLELES: The alleles present in the genotype
- ALLELE_NUM: The allele numbers corresponding with the index in the VCF ALT field
- TYPE: The htsjdk genotype type, possible values: MIXED, HET, HOM_REF, HOM_VAR, NO_CALL,
  UNAVAILABLE.
- CALLED: if the genotype for this sample is called
- MIXED: Boolean indication if the genotype is comprised of both calls and no-calls.
- NON_INFORMATIVE: Boolean that returns true if all samples PLs are 0.
- PHASED: Boolean indicating the genotype was called phased or unphased.
- PLOIDY: The ploidy of the genotype.

##### SAMPLE

This fieldtype is used to query properties of the samples, like phenotypes and pedigree information
which are provided outside the VCF.

Allowed values are:
TODO

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
