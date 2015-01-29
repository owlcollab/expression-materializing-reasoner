package org.geneontology.reasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public interface OWLExtendedReasonerFactory<REASONER extends OWLExtendedReasoner> extends OWLReasonerFactory {

	REASONER createNonBufferingReasoner(OWLOntology ontology);

	REASONER createReasoner(OWLOntology ontology);

	REASONER createNonBufferingReasoner(OWLOntology ontology,
            OWLReasonerConfiguration config)
            throws IllegalConfigurationException;

	REASONER createReasoner(OWLOntology ontology,
            OWLReasonerConfiguration config)
            throws IllegalConfigurationException;
}
