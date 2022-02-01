[![Build Status](https://app.travis-ci.com/molgenis/vip-decision-tree.svg?branch=master)](https://app.travis-ci.com/molgenis/vip-decision-tree)
[![Quality Status](https://sonarcloud.io/api/project_badges/measure?project=molgenis_vip-decision-tree&metric=alert_status)](https://sonarcloud.io/dashboard?id=molgenis_vip-decision-tree)
# Variant Interpretation Pipeline - VCF Decision Tree
Command-line application to classify variants in any VCF (Variant Call Format) file based on a decision tree.
## Requirements
- Java 17

## Usage
```
usage: java -jar vcf-decision-tree.jar -i <arg> -c <arg> [-o <arg>] [-f]
       [-l] [-p] [-d]
 -i,--input <arg>    Input VCF file (.vcf or .vcf.gz) that is annotated with VEP*.
 -c,--config <arg>   Input decision tree file (.json).
 -o,--output <arg>   Output VCF file (.vcf or .vcf.gz).
 -f,--force          Override the output file if it already exists.
 -l,--labels         Write decision tree outcome labels to output VCF
                     file.
 -p,--path           Write decision tree node path to output VCF file.
 -d,--debug          Enable debug mode (additional logging).

usage: java -jar vcf-decision-tree.jar -v
 -v,--version   Print version.
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

Decision nodes perform a test on the variant which determines the outcome consisting of the next node to process and optionally a label. 
Leaf nodes are terminal nodes that determine the class for a variant.
      
### Example
see `src/test/resources/example.json`

## Output VCF
Variant classifications and optionally their paths and labels are annotated on the input VCF in the VIPC, VIPP and VIPL info fields.
### Example
see `src/test/resources/example-classified.vcf`
see `src/test/resources/example-classified_paths-labels.vcf`
