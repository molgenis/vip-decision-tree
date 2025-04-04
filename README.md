[![Build Status](https://app.travis-ci.com/molgenis/vip-decision-tree.svg?branch=main)](https://app.travis-ci.com/molgenis/vip-decision-tree)
[![Quality Status](https://sonarcloud.io/api/project_badges/measure?project=molgenis_vip-decision-tree&metric=alert_status)](https://sonarcloud.io/dashboard?id=molgenis_vip-decision-tree)

# Variant Interpretation Pipeline - VCF Decision Tree
Command-line application to classify variants in any VCF (Variant Call Format) file based on a
decision tree. The tool can be used stand alone but is build for use in the [VIP pipeline](https://molgenis.github.io/vip/)

<!-- TOC -->
  * [Requirements](#requirements)
  * [Usage](#usage)
  * [General](#general)
      * [Modes](#modes)
  * [Creating or modifying a decision tree](#creating-or-modifying-a-decision-tree)
    * [Basic structure](#basic-structure)
    * [Core concepts](#core-concepts)
      * [Fields](#fields)
      * [Operators](#operators)
      * [Values](#values)
    * [Nodes](#nodes)
  * [Output VCF](#output-vcf)
  * [Technical](#technical)
<!-- TOC -->

## Requirements
- Java 21

## Usage

### Decision tree
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
#### Usage examples
```
java -jar vcf-decision-tree.jar -i my.vcf -m field_metadata.json -c decision_tree.json -o out.vcf
java -jar vcf-decision-tree.jar -i my.vcf.gz -m field_metadata.json -c decision_tree.json -o out.vcf.gz
java -jar vcf-decision-tree.jar -i my.vcf.gz -m field_metadata.json -c decision_tree.json -o out.vcf.gz -f -l -p
java -jar vcf-decision-tree.jar -v
```
*:[VEP](https://www.ensembl.org/info/docs/tools/vep/index.html)

### Decision tree visualizer
```
usage: java -jar vcf-decision-tree-visualizer.jar -i <arg> [-o <arg>] [-f] [-m]
 -i,--input <arg>    Input .json decision tree file.
 -o,--output <arg>   Output .html file.
 -f,--force          Overwrite output file.
 -m,--mermaid        Also output mermaid text file.
```
#### Usage examples
```
java -jar vcf-decision-tree-visualizer.jar -i decision_tree.json -o visual.html
java -jar vcf-decision-tree-visualizer.jar --input decision_tree.json --output visual.html --force --mermaid
```



## General
Each variant is classified using a decision tree which consists of decision nodes and leaf nodes.

Decision nodes perform a test on the variant which determines the outcome consisting of the next
node to process and optionally a label. Leaf nodes are terminal nodes that determine the class for a
variant.

Please note that the decision tree tool only classifies variant-effect combinations and does not do any filtering.
When using the decision tree as part of the VIP pipeline the configuration of the pipeline might need to be customized.
Documentation on how to do that can be found [here](https://molgenis.github.io/vip/advanced/classification_trees/#customization).

#### Modes

##### Variant classification (default)
In the variant classification mode the tool will output a classification per VEP value (CSQ), this
classification will be added to the VEP value under the key `VIPC`.

Optionally labels and the path through the tree can be annotated to the VEP value as well.

##### Sample classification
In the sample classification mode the tool will output a classification per VEP value as a comma
separated list for which the index corresponds to the VEP value index, this classification will be
added to the FORMAT fields value under the key `VIPC_S`.

Optionally labels and the path through the tree can be annotated to the FORMAT fields as well.

## Creating or modifying a decision tree

### Basic structure
The top level of the [json](https://www.json.org/json-en.html) file contains:
- the `rootnode`, this is the node where the tree starts.
- a list of all nodes in the tree
- a list of files, e.g. gene panels, that are used in the tree nodes. (optional)

Example: (for more detailed information read the sections below).
```
{
  "rootNode": "panel",
  "files": {
    "my_file" : {
      "path" : "/path/to/my/file"
    }
  },
  "nodes": {
    "panel": {
      "label": "Panel",
      "description": "MyCsqField value present in my file",
      "type": "BOOL",
      "query":
      {
        "field": "INFO/CSQ/MyCsqField",
        "operator": in,
        "value": "file:my_file"
      }
      "outcomeTrue": {
        "nextNode": "exit_a"
      },
      "outcomeFalse": {
        "nextNode": "gene_exists"
      }
    },
    "gene_exists": {
      "description": "Gene exists",
      "type": "EXISTS",
      "field": "INFO/CSQ/Gene",
      "outcomeTrue": {
        "nextNode": "exit_a"
      },
      "outcomeFalse": {
        "nextNode": "exit_b"
      }
    },
    "exit_a": {
      "label": "A_label",
      "description": "A_desc",
      "type": "LEAF",
      "class": "A"
    },
    "exit_b": {
      "label": "B",
      "description": "B_desc",
      "type": "LEAF",
      "class": "B_label"
    },
  }
}
```
### Core concepts

#### Fields
The fields specify where information can be found in the input vcf file, the different types of fields are described below.
A list of fields available when the decision tree is used as part of the VIP pipeline can be found [here](https://molgenis.github.io/vip/advanced/annotations/).

##### Standard VCF
COMMON, INFO, FORMAT

##### Metadata
For values in the input VCF file the header of the vcf file are used to determine the metadata.
For both nested field and overriding the specification in the headers the `metadata.json` files is used.
This file contains metadata specifications for `FORMAT` and `INFO` fields in the input vcf

##### Customized fields

###### INFO_VEP
Any field in the VEP value can be used, if the field is not specified in the `metadata.json` it is interpreted as a
single value string field.

###### GENOTYPE
This fieldtype uses the information provided by htsjdk about the Genotype FORMAT field.

Allowed values are:

- ALLELES: The alleles (list of strings) present in the genotype.
- ALLELE_NUM: The allele numbers corresponding with the index in the VCF ALT field.
- TYPE: The htsjdk genotype type, possible values: MIXED, HET, HOM_REF, HOM_VAR, NO_CALL,
  UNAVAILABLE.
- CALLED: Boolean indication if the genotype for this sample is called.
- MIXED: Boolean indication if the genotype is a combination of both calls and no-calls.
- NON_INFORMATIVE: Boolean that returns true if all samples PLs are 0.
- PHASED: Boolean indicating the genotype was called phased or unphased.
- PLOIDY: The ploidy of the genotype as an integer, null if no call is present.

###### SAMPLE
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

#### Operators

##### Boolean
The following operators are available for boolean queries:

| Operator      | Description                                                 | Supported Field types                                                         |
|---------------|-------------------------------------------------------------|-------------------------------------------------------------------------------|
| ==            | Field value equals specified value.                         | Integer, Float, Flag, Character, String, and lists of those types             |
| !=            | Field value does not equal specified value.                 | Integer, Float, Flag, Character, String, and lists of those types             |
| <             | Field value is less than the specified value.               | Integer, Float                                                                |
| <=            | Field value is less or equal that the specified value.      | Integer, Float                                                                |
| >             | Field value greater than the specified value.               | Integer, Float                                                                |
| >=            | Field value greater or equal than the specified value.      | Integer, Float                                                                |
| in            | Field value is in the list of specified values.             | Integer, Float, Flag, Character, String, and lists of those types<sup>1</sup> |
| !in           | Field value is not in the list of specified values.         | Integer, Float, Flag, Character, String, and lists of those types<sup>1</sup> |
| contains      | Field values contain the specified value.                   | Lists of Integer, Float, Flag, Character, String                              |
| !contains     | Field values do not contain the specified value.            | Lists of Integer, Float, Flag, Character, String                              |
| contains_any  | Field values contains at least one of the specified values. | Lists of Integer, Float, Flag, Character, String                              |
| contains_all  | Field values contains all of the specified values.          | Lists of Integer, Float, Flag, Character, String                              |
| contains_none | Field values contains none of the specified values.         | Lists of Integer, Float, Flag, Character, String                              |
1) In cases where the query value contains 'child' lists as part of the value in the specified list.

##### Multi bool operators
The following operators are available for groups of queries in `MULTIBOOL` nodes:

| Operator | Description               |
|----------|---------------------------|
| AND      | All of the queries match. |
| OR       | Any of the queries match. |

#### Values
Depending on the operator and field of the query values can be strings, numbers or lists of strings or numbers.
There are also a few special values are available:

##### File
The files specified on the top level of the tree can be used, using the `file:` prefix.
files work as lists of values, where every line in the file is an item on the list.

Example
```
      "query": {
        "field": "INFO/CSQ/Gene",
        "operator": "in",
        "value": "file:my_file"
      }
```

##### Field
Using the `field:` prefix the query will use the value of another field as the value for the query.

Example
```
      "query": {
        "field": "FORMAT/GENOTYPE/ALLELES",
        "operator": "contains_any",
        "value": "field:ALT"
      }
```

##### Lists
Lists of values should be specified comma-separated between square brackets.

Example
```
      "query": {
        "field": "INFO/MY_FIELD",
        "operator": "in",
        "value": ["a","b","c"] 
      },
```

### Nodes
Nodes are the building blocks of the tree, each node has one or more conditions to determine what the next node to evaluate is.
The exceptions are the leaf nodes, which are the "end state" nodes for the tree. 
All nodes need to have a label and can have a description.

#### BOOL
Boolean nodes are nodes that result in true or false based on a single query.

The query should contain a field, an operator and a value, see the corresponding paragraphs of this document for the available options.

The node itself needs to have 'defaultNode' to proceed with if no category matches.
Optionally a 'missingNode' can be specified to proceed with if the field is empty or not present in the vcf,
if the `outcomeMissing` is not specified the `outcomeDefault` is used instead for these cases.

Example:
```    
"example_node": {
      "type": "BOOL",
      "description": "this is an example",
      "query": {
        "field": "INFO/FIELD",
        "operator": "==",
        "value": "TEST_VALUE"
      },
      "outcomeTrue": {
        "nextNode": "my_next_node"
      },
      "outcomeFalse": {
        "nextNode": "my_other_node"
      },
      "outcomeMissing": {
        "nextNode": "my_third_node"
      }
    }
```

#### EXISTS
An `EXISTS` node will check if a field is present and has a value.
Since the result is always either true or false no missing of default node can be specified.

Please note that in `strict` mode an exception is thrown if a field is missing from the VCF input. In these cases the `EXISTS` node can only be used to determine if an existing field has no value.

Example
```
    "gene": {
      "description": "Gene exists",
      "type": "EXISTS",
      "field": "INFO/CSQ/Gene",
      "outcomeTrue": {
        "nextNode": "my_next_node"
      },
      "outcomeFalse": {
        "nextNode": "my_other_node"
      }
    },
```

#### BOOL_MULTI
`BOOL_MULTI` nodes are nodes that can combine multiple fields and or multiple queries in a single node.
Sets of boolean queries can be used to determine the next node, these groups are provide under the "outcomes" key.
Each groups is an object containing a description, a list of queries and optionally an operator. If no operator is provided `OR` is used as default.
If multiple sets of queries result in "true" the first set is used to determine the next node.
If any of the fields used in the `BOOL_MULTI` node is missing or empty the node will move on to the `outcomeMissing` node.

Example:
```
"consequence": {
      "description": "Filter consequences",
      "type": "BOOL_MULTI",
      "fields": [
        "INFO/CSQ/FIELD1",
        "INFO/FIELD2"
      ],
      "outcomes": [
        {
          "description": "",
          "queries": [
            {
              "field": "INFO/FIELD2",
              "operator": "<",
              "value": 2
            }
          ],
          "outcomeTrue": {
            "nextNode": "next_node"
          }
        },
        {
          "description": "Allele Frequency >= 0.02 or Number of Homozygotes > 5",
          "operator": "OR",
          "queries": [
            {
              "field": "INFO/CSQ/FIELD1",
              "operator": "==",
              "value": "TEST"
            },
            {
              "field": "INFO/FIELD2",
              "operator": ">",
              "value": "1"
            },
          ],
          "outcomeTrue": {
            "nextNode": "other_node"
          }
        }
      ],
      "outcomeDefault": {
        "nextNode": "my_default_node"
      },
      "outcomeMissing": {
        "nextNode": "my_default_node"
      }
    }
```

#### CATEGORICAL
A `CATEGORICAL` node will take the value of the specified field and try to map it to a list of options specified by the node.
The categories need to have a `label` and a `nextNode`, this is the node to continue with if the category matches.
The node itself needs to have defaultNode to proceed with if no category matches.
Optionally a 'missingNode' can be specified to proceed with if the field is empty or not present in the vcf, 
if the `outcomeMissing` is not specified the `outcomeDefault` is used instead for these cases.

Example:
```
    "my_cat": {
      "type": "CATEGORICAL",
      "description": "my_cat description",
      "field": "INFO/CAT",
      "outcomeMap": {
        "high": {
          "nextNode": "include",
          "label": "cat_high"
        },
        "low": {
          "nextNode": "exclude",
          "label": "cat_low"
        }
      },
      "outcomeMissing": {
        "nextNode": "exclude",
        "label": "no_cat"
      },
      "outcomeDefault": {
        "nextNode": "exclude",
        "label": "cat_unknown"
      }
    },
```
#### LEAF
`LEAF` nodes are the endpoint of the tree, these nodes specify the class that a variant effect will be annotated with.
```
    "exit_a": {
      "label": "A_label",
      "description": "A_desc",
      "type": "LEAF",
      "class": "A"
    }
```

### Example
see `src/test/resources/example.json` and `src/test/resources/example_sample.json`

## Output VCF
When used in 'variant mode' variant classifications and optionally their paths and labels are annotated on the input VCF in the
`VIPC`, `VIPP` and `VIPL` info fields.
When used in 'sample mode' classifications and optionally their paths and labels for each proband sample are written in the `VIPC_S`,`VIPP_S`,`VIPL_S` FORMAT field, and a list of classifications for every sample is written to the `VIPC_S` INFO field.

### Example
see `src/test/resources/example-classified.vcf`
see `src/test/resources/example-classified_paths-labels.vcf`
see `src/test/resources/example_sample-classified.vcf`
see `src/test/resources/example_sample-classified_paths-labels.vcf`

## Technical
### Installation
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