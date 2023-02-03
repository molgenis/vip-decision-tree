[![Build Status](https://app.travis-ci.com/molgenis/vip-decision-tree.svg?branch=feat/annotation)](https://app.travis-ci.com/molgenis/vip-decision-tree)
[![Quality Status](https://sonarcloud.io/api/project_badges/measure?project=molgenis_vip-decision-tree&metric=alert_status)](https://sonarcloud.io/dashboard?id=molgenis_vip-decision-tree)

# Variant Interpretation Pipeline - GREEN-VARAN score annotator
Command-line application to calculate and annotate a variant score specialized for non-coding variants.

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

##### Variant annotation

The tool will calculate a score of 0 to 3 based on vallues that are annotated by [VIP-non-coding](https://github.com/molgenis/vip/tree/feat/non-coding)
The scoring system is based on the scoring table from [GREEN-DB](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8934622/) 

The scoring system works as follows:
```
Level 0: Nothing
Level 1: Gnomad population AF < 1% (0.01)
Level 2: Level 1 + overlap with at least one function element among transcription factors binding sites (TFBS), DNase peaks, ultra-conserved elements (UCNE)
Level 3: Level 2 + prediction score value above the suggested FDR50 threshold for at least one among ncER, FATHMM-MKL, ReMM
Level 4: Level 3 + region constraint value ≥ 0.7
```

## Output VCF

Variant annotaions are annotated on the input VCF in the
VIPC fields.

### Example

#### Input
see `see src/test/resources/example.vcf`

#### Output 
see `see src/test/resources/example-classified.vcf`
