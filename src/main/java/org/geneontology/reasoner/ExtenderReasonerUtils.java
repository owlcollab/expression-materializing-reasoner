package org.geneontology.reasoner;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * @author cjm
 *
 */
public class ExtenderReasonerUtils {

    public enum ClassType {ANON, NAMED, ALL};

    public static Set<OWLSubClassOfAxiom> getInferredSubClassOfAxiomsForNamedClasses(OWLExtendedReasoner xr, boolean direct) {
        Set<OWLSubClassOfAxiom> axioms = new HashSet<>();
        OWLOntology ont = xr.getRootOntology();
        OWLDataFactory df = ont.getOWLOntologyManager().getOWLDataFactory();
        Set<OWLClass> cs = ont.getClassesInSignature(Imports.INCLUDED);
        for (OWLClass c : cs) {

            for (OWLClassExpression sc : xr.getSuperClassExpressions(c, direct)) {
                axioms.add(df.getOWLSubClassOfAxiom(c, sc));
            }

        }
        return axioms;
    }
}
