package org.molgenis.vcf.decisiontree.utils;

import java.nio.file.Path;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

public class TestHdt {

  public static void main(String[] args) {
    SailRepository repo = new SailRepository(new NativeStore(
        Path.of("C:\\Users\\bartc\\Documents\\git\\vip-decision-tree\\src\\main\\resources\\store")
            .toFile()));
    try (SailRepositoryConnection conn = repo.getConnection()) {
      // conn.add(Path.of("C:\\vibe-3.1.0.hdt").toFile(), RDFFormat.HDT);

      StringBuilder qb = new StringBuilder();
      qb.append("# Default DisGeNET prefixes.\n"
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
          + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
          + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
          + "PREFIX dcterms: <http://purl.org/dc/terms/>\n"
          + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
          + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
          + "PREFIX void: <http://rdfs.org/ns/void#>\n"
          + "PREFIX sio: <http://semanticscience.org/resource/>\n"
          + "PREFIX so: <http://www.sequenceontology.org/miso/current_svn/term/SO:>\n"
          + "PREFIX ncit: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>\n"
          + "PREFIX up: <http://purl.uniprot.org/core/>\n"
          + "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n"
          + "PREFIX dctypes: <http://purl.org/dc/dcmitype/>\n"
          + "PREFIX wi: <http://purl.org/ontology/wi/core#>\n"
          + "PREFIX eco: <http://purl.obolibrary.org/obo/eco.owl#>\n"
          + "PREFIX prov: <http://www.w3.org/ns/prov#>\n"
          + "PREFIX pav: <http://purl.org/pav/>\n"
          + "PREFIX obo: <http://purl.obolibrary.org/obo/> \n"
          + "\n"
          + "# Custom prefixes.\n"
          + "PREFIX umls: <http://linkedlifedata.com/resource/umls/id/> # DisGeNET\n"
          + "PREFIX ncbigene: <http://identifiers.org/ncbigene/> # DisGeNET\n"
          + "PREFIX pda: <http://rdf.disgenet.org/resource/pda/> # DisGeNET\n"
          + "PREFIX gda: <http://rdf.disgenet.org/resource/gda/> # DisGeNET\n"
          + "PREFIX hgnc: <http://identifiers.org/hgnc.symbol/> # DisGeNET\n"
          + "PREFIX ordo: <http://www.orpha.net/ORDO/> # DisGeNET / Orphanet\n"
          + "PREFIX hoom: <http://www.semanticweb.org/ontology/HOOM#> # Orphanet\n"
          + "PREFIX void5: <http://rdf.disgenet.org/v5.0.0/void/> # DisGeNET\n"
          + "PREFIX void6: <http://rdf.disgenet.org/v6.0.0/void/> # DisGeNET\n"
          + "PREFIX pmid: <http://identifiers.org/pubmed/> # DisGeNET\n"
          + "\n"
          + "# Query.\n"
          + "CONSTRUCT {\n"
          + "\t?disease rdf:type ncit:C7057 ;\n"
          + "\tdcterms:title ?title ;\n"
          + "\tsio:SIO_000212 ?gda . # added sio:SIO_000212 here so sio:SIO_000628 can be removed from ?gda\n"
          + "}\n"
          + "WHERE {\n"
          + "\t?gda sio:SIO_000628 ?disease , ?gene .\n"
          + "\t?disease dcterms:title ?title .\n"
          + "\n"
          + "\tFILTER EXISTS { ?gda rdf:type/rdfs:subClassOf* sio:SIO_000983 . }\n"
          + "\tFILTER EXISTS { ?disease rdf:type ncit:C7057 . }\n"
          + "\tFILTER EXISTS { ?gene rdf:type ncit:C16612 . }\n"
          + "}");

      conn.prepareQuery(QueryLanguage.SPARQL, qb.toString());
      String queryString = "SELECT ?disease ?title WHERE { ?disease ?p ?title } ";
      TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
      try (TupleQueryResult result = tupleQuery.evaluate()) {
        while (result.hasNext()) {  // iterate over the result
          BindingSet bindingSet = result.next();
          Value valueOfX = bindingSet.getValue("disease");
          System.out.println(valueOfX.stringValue());
          // do something interesting with the values here...
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
