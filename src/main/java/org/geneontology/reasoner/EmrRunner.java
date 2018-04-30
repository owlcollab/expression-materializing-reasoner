package org.geneontology.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Command line wrapper
 * 
 * @author cjm
 *
 */
public class EmrRunner {

    private static Logger LOG = Logger.getLogger(EmrRunner.class);

    @Parameter(names = { "-v",  "--verbose" }, description = "Level of verbosity")
    private Integer verbose = 1;

    @Parameter(names = { "-o", "--out"}, description = "output json/yaml file")
    private String outpath;

    @Parameter(names = { "-p", "--properties"}, description = "OWL file containing declarations of properties to be used")
    private String propOntPath;

    @Parameter(names = { "-m", "--method"}, description = "one of: closure, svf")
    private String method;

    @Parameter(names = { "-t", "--to"}, description = "output format: json or yaml")
    private String outformat;

    @Parameter(description = "Files")
    private List<String> files = new ArrayList<>();

    OWLOntologyManager manager;


    public static void main(String ... args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        EmrRunner main = new EmrRunner();
        new JCommander(main, args);
        main.run();
    }

    public void run() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {

        Logger.getRootLogger().setLevel(Level.INFO);
        Logger.getLogger("org.semanticweb.elk").setLevel(Level.OFF);
        
        OWLOntology ontology   = this.loadOWL(files.get(0));
        
        OWLOntology propOnt = null;
        Set<OWLObjectProperty> props = null;
        if (propOntPath != null) {
            propOnt = loadOWL(propOntPath);
            props = propOnt.getObjectPropertiesInSignature();
            LOG.info("PROPS: "+props);
        }
        
        ElkReasonerFactory ef = new ElkReasonerFactory();
        OWLExtendedReasonerFactory elkEmrFactory;
        elkEmrFactory =  new ExpressionMaterializingReasonerFactory(ef);
        LOG.info("creating extended reasoner");
        OWLExtendedReasoner xreasoner = elkEmrFactory.createReasoner(ontology);
        LOG.info("Done creating reasoner");
 
        LOG.info("materializing props");
        if (props != null) {
            for (OWLObjectProperty p : props) {        
                ((ExpressionMaterializingReasoner)xreasoner).materializeExpressions(p);
            }
        }
        LOG.info("DONE materializing props");
        
        
        OWLOntologyManager mgr = ontology.getOWLOntologyManager();
        OWLDataFactory df = mgr.getOWLDataFactory();
        OWLOntology newOntology = mgr.createOntology();
        newOntology = mgr.createOntology();
        if (method.equals("svf")) {
            LOG.info("Getting all subsumptions");
            Set<OWLSubClassOfAxiom> axioms = xreasoner.getInferredSubClassOfGCIAxioms(props);
            mgr.addAxioms(newOntology, axioms);
            
        }
        else {
            // assume method is closure
            Set<OWLSubClassOfAxiom> axioms = ExtenderReasonerUtils.getInferredSubClassOfAxiomsForNamedClasses(xreasoner, false);
            mgr.addAxioms(newOntology, axioms);
        }
        File outfile = new File(outpath);
        LOG.info("Saving to "+outfile);
        System.out.println("Saving to "+outfile);
        FunctionalSyntaxDocumentFormat fmt = new FunctionalSyntaxDocumentFormat();
        mgr.saveOntology(newOntology, fmt, IRI.create(outfile));

    }

    private OWLOntologyManager getOWLOntologyManager() {
        if (manager == null)
            manager = OWLManager.createOWLOntologyManager();
        return manager;
    }
    /**
     * @param iri
     * @return OWL Ontology 
     * @throws OWLOntologyCreationException
     */
    public OWLOntology loadOWL(IRI iri) throws OWLOntologyCreationException {
        return getOWLOntologyManager().loadOntology(iri);
    }
  
    public OWLOntology loadOWL(String path) throws OWLOntologyCreationException {
        File file = new File(path);
        return loadOWL(file);
    }
  
    


    /**
     * @param file
     * @return OWL Ontology
     * @throws OWLOntologyCreationException
     */
    public OWLOntology loadOWL(File file) throws OWLOntologyCreationException {
        IRI iri = IRI.create(file);
        return getOWLOntologyManager().loadOntologyFromOntologyDocument(iri);       
    }
}
