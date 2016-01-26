package org.geneontology.reasoner;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class ExpressionMaterializingReasonerTest {

	private OWLOntology ontology = null;
	private ExpressionMaterializingReasonerFactory elkFactory = null;
	private ExpressionMaterializingReasonerFactory hermitFactory = null;
	private ExpressionMaterializingReasoner elkReasoner = null;
	private ExpressionMaterializingReasoner hermitReasoner = null;
	
	@Before
	public void before() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		ontology = m.loadOntologyFromOntologyDocument(new File("src/test/resources","neuron.owl"));
		elkFactory = new ExpressionMaterializingReasonerFactory(new ElkReasonerFactory());
		OWLReasonerFactory hermit = new org.semanticweb.HermiT.ReasonerFactory();
		hermitFactory = new ExpressionMaterializingReasonerFactory(hermit);
		elkReasoner = elkFactory.createReasoner(ontology);
		hermitReasoner = hermitFactory.createReasoner(ontology);
	}

	@After
	public void after() throws Exception {
		if (elkReasoner != null) {
			elkReasoner.dispose();
		}
		if (hermitReasoner != null) {
			hermitReasoner.dispose();
		}
		elkFactory = null;
		hermitFactory = null;
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
		elkReasoner.materializeExpressions();
		boolean[] bools = {true, false};
		// step 2: check inferences for all classes (or subset)
		for(OWLClass cls : ontology.getClassesInSignature()) {
			for (OWLObjectProperty p : ontology.getObjectPropertiesInSignature()) {
				for (boolean isDirect : bools) {
					Set<OWLClass> parents = elkReasoner.getSuperClassesOver(cls,p, isDirect);
					for (OWLClass par : parents) {
						// sorry Heiko...
						System.out.println(cls + " " + p + " "+isDirect + " = "+par);
					}
				}
			}
		}
	}
	//@Test
	public void test2() throws Exception {
		// step 1: materialize expressions, defaults to all
		elkReasoner.materializeExpressions();
		hermitReasoner.materializeExpressions();
		
		// step 2: check inferences for all classes (or subset)
		for(OWLClass cls : ontology.getClassesInSignature()) {
			Set<OWLClassExpression> elkSuperClassExpressions = elkReasoner.getSuperClassExpressions(cls, true);
			Set<OWLClassExpression> hermitSuperClassExpressions = hermitReasoner.getSuperClassExpressions(cls, true);
			// we expect Elk *not* to entail overlaps based on inverse axioms (neuron overlaps neuron)
			//assertEquals("Check that the inferences are the same for cls: "+cls.getIRI(), hermitSuperClassExpressions, elkSuperClassExpressions);
		}
	}
}
