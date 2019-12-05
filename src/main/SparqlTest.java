package main;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;

public class SparqlTest {
	
	/***********************************/
    /* Constants                       */
    /***********************************/

    // Directory where we've stored the local data files, such as places.owl
    public static final String SOURCE = "./resources/ontologies/";

    // Places ontology namespace
    public static final String OMDb = "http://www.semanticweb.org/edison/ontologies/2019/10/OMdB_Semantic#";
    

	public static void main(String[] args) {
		//create instance of OntModel class
		OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		
		//read ontology model
		FileManager.get().readModel( m, SOURCE + "OMdB_Semantic.owl" );
		
		String prefix = "prefix OMdB: <" + OMDb + ">\n" +
                		"prefix rdfs: <" + RDFS.getURI() + ">\n" +
                		"prefix owl: <" + OWL.getURI() + ">\n";

		String query_text=  prefix +
					"SELECT ?aName "
					+ "WHERE {?actor a OMdB:Actor." 
					+ "?actor OMdB:name ?aName."
					+ "}" ; 
		
		Query query = QueryFactory.create( query_text );
        QueryExecution qexec = QueryExecutionFactory.create( query, m );
        try {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out( results, m );
        }
        finally {
            qexec.close();
        }

	}

}
