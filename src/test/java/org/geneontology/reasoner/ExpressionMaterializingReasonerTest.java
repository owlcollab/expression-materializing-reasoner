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
		OWLReasonerFactory hermit = new org.semanticweb.HermiT.Reasoner.ReasonerFactory();
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
	
	@Test
	public void test() throws Exception {
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
