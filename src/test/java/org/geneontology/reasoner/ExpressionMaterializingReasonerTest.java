package org.geneontology.reasoner;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class ExpressionMaterializingReasonerTest {

    private static Logger LOG = Logger.getLogger(ExpressionMaterializingReasoner.class);

	private OWLOntology ontology = null;
	private ExpressionMaterializingReasonerFactory elkEmrFactory = null;
	private ExpressionMaterializingReasonerFactory hermitEmrFactory = null;
	private ExpressionMaterializingReasoner elkExtReasoner = null;
	private ExpressionMaterializingReasoner hermitExtReasoner = null;
	private OWLDataFactory owlDataFactory;
	
	@Before
	public void before() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		ontology = m.loadOntologyFromOntologyDocument(new File("src/test/resources","neuron.owl"));
		elkEmrFactory = new ExpressionMaterializingReasonerFactory(new ElkReasonerFactory());
		OWLReasonerFactory hermit = new org.semanticweb.HermiT.ReasonerFactory();
		hermitEmrFactory = new ExpressionMaterializingReasonerFactory(hermit);
		elkExtReasoner = elkEmrFactory.createReasoner(ontology);
		hermitExtReasoner = hermitEmrFactory.createReasoner(ontology);
		owlDataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
	}

	@After
	public void after() throws Exception {
		if (elkExtReasoner != null) {
			elkExtReasoner.dispose();
		}
		if (hermitExtReasoner != null) {
			hermitExtReasoner.dispose();
		}
		elkEmrFactory = null;
		hermitEmrFactory = null;
		ontology = null;
	}
	
	
	// what we expect with Elk -
	// the following should be the only direct results for this class-property pair when direct is set
	//
	// <http://x.org/kenyon-cell> <http://x.org/in-taxon> true = <http://x.org/arthropod>
	// <http://x.org/Purkinje-cell-type-1> <http://x.org/in-taxon> true = <http://x.org/vertebrate>
	// <http://x.org/kenyon-cell> <http://x.org/has-part> true = <http://x.org/axon>
	// <http://x.org/Purkinje-cell-type-1> <http://x.org/part-of> true = <http://x.org/cerebellum>
	@Test
	public void test1() throws Exception {
		// step 1: materialize expressions, defaults to all
	    // AUTOMATIC NOW: elkExtReasoner.rewriteRangeAxioms();
	    elkExtReasoner.materializeExpressions();
		boolean[] bools = {true, false};
		boolean okSvfTest1 = false;
		// step 2: check inferences for all classes (or subset)
		for(OWLClass cls : ontology.getClassesInSignature()) {
			for (OWLObjectProperty p : ontology.getObjectPropertiesInSignature()) {
				for (boolean isDirect : bools) {
					Set<OWLClass> parents = elkExtReasoner.getSuperClassesOver(cls,p, isDirect);
					for (OWLClass par : parents) {
						//logger.info(cls + " " + p + " "+isDirect + " = "+par);
					}
                    Set<OWLObjectSomeValuesFrom> svfparents = 
                            elkExtReasoner.getSomeValuesFromSuperClasses(cls, p, isDirect);
                    for (OWLClass par : parents) {
                        String s = p +" SOME " + cls + " SubClassOf: " +
                                p + " SOME " + par;
                        LOG.debug("SVF: " + s +" DIRECT: "+isDirect);
                        if (s.equals("<http://x.org/overlaps> SOME <http://x.org/brain-neuron> SubClassOf: "+
                        "<http://x.org/overlaps> SOME <http://x.org/nervous-system>")) {
                            okSvfTest1 = true;
                        }
                    }
                   
					
				}
			}
		}
        testSuperClassExpression(true, "mushroom-body", "overlaps", "cell");
        testSuperClassExpression(true, "mushroom-body", "part-of", "nervous-system");
        testSuperClassExpression(false, "neuron", "overlaps", "mushroom-body");
		
		
		//assertTrue("SVF test failed", okSvfTest1);
        Set<OWLSubClassOfAxiom> axs = elkExtReasoner.getInferredSubClassOfGCIAxioms();
        
        for (OWLSubClassOfAxiom ax : axs) {
            LOG.debug("AX: "+ax);
        }
        testSVFSubsumption(true, "part-of", "Purkinje-cell", "overlaps", "nervous-system", axs);
        testSVFSubsumption(false, "overlaps", "Purkinje-cell", "part-of", "nervous-system", axs);
        testSVFSubsumption(false, "enables", "Purkinje-cell", "part-of", "organism", axs);
        testSVFSubsumptionEmpty("enables", "Purkinje-cell", axs);
        testSVFSubsumptionEmpty("is-active-in", "apoptosis", axs);
        testSVFSubsumption(true, "enables", "receptor-activity-in-apoptosis", "involved-in", "apoptosis", axs);
        testSVFSubsumption(true, "enables", "receptor-activity-in-apoptosis", "acts-upstream-of-or-within", "apoptosis", axs);
        testSVFSubsumption(false, "enables", "receptor-activity-in-apoptosis", "acts-upstream-of", "apoptosis", axs);
        testSVFSubsumption(true, "acts-upstream-of", "receptor-activity-in-apoptosis", "acts-upstream-of", "receptor-activity-in-apoptosis", axs);
        testSVFSubsumption(true, "acts-upstream-of", "receptor-activity-in-apoptosis", "acts-upstream-of", "receptor-activity", axs);
        testSVFSubsumption(true, "acts-upstream-of", "receptor-activity-in-apoptosis", "acts-upstream-of-or-within", "receptor-activity-in-apoptosis", axs);
        testSVFSubsumption(true, "acts-upstream-of", "receptor-activity-in-apoptosis", "acts-upstream-of-or-within", "apoptosis", axs);
        testSVFSubsumption(false, "acts-upstream-of", "receptor-activity-in-apoptosis", "involved-in", "apoptosis", axs);
        testSVFSubsumption(true, "involved-in", "regulation-of-apoptosis", "involved-in-regulation-of", "apoptosis", axs);
        testSVFSubsumption(false, "involved-in", "regulation-of-apoptosis", "involved-in", "apoptosis", axs);
        
        // this requires swrl
        // todo: allow passing in chain of relations
        //testSVFSubsumption(true, "involved-in-regulation-of", "regulation-of-apoptosis", "involved-in-regulation-of", "apoptosis", axs);

        testSVFSubsumption(false, "involved-in-regulation-of", "regulation-of-apoptosis", "involved-in", "apoptosis", axs);
	}

	private void testSuperClassExpression(boolean isTrue, String c,
            String p, String filler) {
	    OWLObjectSomeValuesFrom svf = getSVF(p, filler);
	    boolean ok = false;
	    for ( OWLClassExpression x : elkExtReasoner.getSuperClassExpressions(getClass(c), false) ) {
	        if (x.equals(svf)) {
	            ok = true;
	        }
	    }
	    assertTrue(ok == isTrue);
    }

    private void testSVFSubsumptionEmpty(String p1, String c1, Set<OWLSubClassOfAxiom> axs) {
	    OWLObjectSomeValuesFrom svf1 = getSVF(p1, c1);
	    Set<OWLObjectSomeValuesFrom> supers =
	            elkExtReasoner.getSomeValuesFromSuperClasses(getClass(c1), getProperty(p1), false, true);
	    assertTrue("expected empty", supers.size() == 0);
	}
	           
	private void testSVFSubsumption(boolean isTrue, String p1, String c1,
            String p2, String c2, Set<OWLSubClassOfAxiom> axs) {
        OWLObjectSomeValuesFrom svf1 = getSVF(p1, c1);
        OWLObjectSomeValuesFrom svf2 = getSVF(p2, c2);
        
        LOG.info("Expect "+svf1+ " SubClassOf "+svf2+" IS "+isTrue);
        // first test: dynamic call
        boolean ok1 = false;
        for (OWLObjectSomeValuesFrom svf : elkExtReasoner.getSomeValuesFromSuperClasses(getClass(c1), getProperty(p1), false, true)) {
            if (svf.equals(svf2))
                ok1 = true;
        }
        assertTrue("could not find "+svf2, ok1 == isTrue);
        
        // second test: check map
        boolean ok2 = false;
        for (OWLSubClassOfAxiom ax : axs) {
            if (ax.getSubClass().equals(svf1)) {
                if (ax.getSuperClass().equals(svf2)) {
                    ok2 = true;
                }                
            }
        }
        //assertTrue("could not find "+svf2, ok2 == isTrue);
    }

    // too slow, do not run
	//@Test
	public void testCompareHermitVsElk() throws Exception {
		// step 1: materialize expressions, defaults to all
		elkExtReasoner.materializeExpressions();
		hermitExtReasoner.materializeExpressions();
		
		// step 2: check inferences for all classes (or subset)
		for(OWLClass cls : ontology.getClassesInSignature()) {
			Set<OWLClassExpression> elkSuperClassExpressions = elkExtReasoner.getSuperClassExpressions(cls, true);
			Set<OWLClassExpression> hermitSuperClassExpressions = hermitExtReasoner.getSuperClassExpressions(cls, true);
			// we expect Elk *not* to entail overlaps based on inverse axioms (neuron overlaps neuron)
			//assertEquals("Check that the inferences are the same for cls: "+cls.getIRI(), hermitSuperClassExpressions, elkSuperClassExpressions);
		}
	}
	
	// utils
    private OWLClass getClass(String id) {
        return owlDataFactory.getOWLClass(IRI.create("http://x.org/"+id));
    }
    private OWLObjectProperty getProperty(String id) {
        return owlDataFactory.getOWLObjectProperty(IRI.create("http://x.org/"+id));
    }
	
	private OWLObjectSomeValuesFrom getSVF(String p, String c) {
	    return owlDataFactory.getOWLObjectSomeValuesFrom(getProperty(p), getClass(c));
	}
	
}
