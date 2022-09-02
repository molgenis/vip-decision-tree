package org.molgenis.vcf.decisiontree.ped;

import static org.molgenis.vcf.decisiontree.ped.PedIndividual.AffectionStatus.AFFECTED;
import static org.molgenis.vcf.decisiontree.ped.PedIndividual.AffectionStatus.UNAFFECTED;
import static org.molgenis.vcf.decisiontree.ped.PedIndividual.AffectionStatus.UNKNOWN;

import org.molgenis.vcf.decisiontree.UnsupportedPedException;
import org.molgenis.vcf.decisiontree.ped.PedIndividual.AffectionStatus;
import org.molgenis.vcf.decisiontree.ped.PedIndividual.Sex;

class PedIndividualParser {

  public PedIndividual parse(String line) {
    String[] tokens = line.split("\\s+");
    if (tokens.length != 6) {
      throw new InvalidPedException(line);
    }

    Sex sex = parseSex(tokens[4]);
    AffectionStatus affectionStatus = parseAffectionStatus(tokens[5]);
    return new PedIndividual(tokens[0], tokens[1], tokens[2], tokens[3], sex, affectionStatus);
  }

  private Sex parseSex(String token) {
    Sex sex;
    switch (token) {
      case "1":
        sex = Sex.MALE;
        break;
      case "2":
        sex = Sex.FEMALE;
        break;
      default:
        sex = Sex.UNKNOWN;
        break;
    }
    return sex;
  }

  private AffectionStatus parseAffectionStatus(String token) {
    AffectionStatus affectionStatus;
    switch (token) {
      case "-9", "0":
        affectionStatus = UNKNOWN;
        break;
      case "1":
        affectionStatus = UNAFFECTED;
        break;
      case "2":
        affectionStatus = AFFECTED;
        break;
      default:
        throw new UnsupportedPedException(token);
    }
    return affectionStatus;
  }
}
