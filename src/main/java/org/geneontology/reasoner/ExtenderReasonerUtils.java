package org.geneontology.reasoner;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;

/**
 * @author cjm
 *
 */
public class ExtenderReasonerUtils {

    private static Logger LOG = Logger.getLogger(ExtenderReasonerUtils.class);

    /**
     * finds all inferred SubClassOf(C, R some D) using extended reasoner
     * 
     * @param xr
     * @param direct
     * @return inferred axioms
     */
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

   
    /**
     * Returns all axioms (p some c) SubClassOf (q some s)
     * for a given p
     * 
     * @param xr 
     * @param p
     * @return subClassOf axioms between someValuesFrom expressions
     * @throws InconsistentOntologyException
     * @throws ClassExpressionNotInProfileException
     * @throws FreshEntitiesException
     * @throws ReasonerInterruptedException
     * @throws TimeOutException
     */
    public static Set<OWLSubClassOfAxiom> getInferredSubClassOfGCIAxioms(OWLExtendedReasoner xr, OWLObjectProperty p) throws InconsistentOntologyException,
    ClassExpressionNotInProfileException, FreshEntitiesException,
    ReasonerInterruptedException, TimeOutException {
        Set<OWLSubClassOfAxiom> axioms = new HashSet<>();
        int n=0;
        OWLOntology ont = xr.getRootOntology();
        OWLDataFactory df = ont.getOWLOntologyManager().getOWLDataFactory();
        Set<OWLClass> allClasses = ont.getClassesInSignature(Imports.INCLUDED);
        for (OWLClass c : allClasses) {
            n++;
            if (n % 1000 == 0) {
                LOG.info("Class "+n+"/"+allClasses.size());
            }
            for (OWLObjectSomeValuesFrom sc : ((ExpressionMaterializingReasoner)xr).getSomeValuesFromSuperClasses(c, p, false, true)) {
                axioms.add(df.getOWLSubClassOfAxiom(
                        df.getOWLObjectSomeValuesFrom(p, c),
                        sc));
            }
        }
        return axioms;
    }

    public static Set<OWLSubClassOfAxiom> getInferredSubClassOfGCIAxioms(OWLExtendedReasoner xr) throws InconsistentOntologyException,
    ClassExpressionNotInProfileException, FreshEntitiesException,
    ReasonerInterruptedException, TimeOutException {
        OWLOntology ont = xr.getRootOntology();
        OWLDataFactory df = ont.getOWLOntologyManager().getOWLDataFactory();
        Set<OWLSubClassOfAxiom> axioms = new HashSet<>();
        for (OWLObjectProperty p : ont.getObjectPropertiesInSignature()) {
            LOG.info("Calculating svf subsumptions for "+p);
            axioms.addAll(getInferredSubClassOfGCIAxioms(xr, p));
        }
        return axioms;
    }

    public static Set<OWLSubClassOfAxiom> getInferredSubClassOfGCIAxioms(OWLExtendedReasoner xr, 
            Set<OWLObjectProperty> props) throws InconsistentOntologyException,
    ClassExpressionNotInProfileException, FreshEntitiesException,
    ReasonerInterruptedException, TimeOutException {
        OWLOntology ont = xr.getRootOntology();
        OWLDataFactory df = ont.getOWLOntologyManager().getOWLDataFactory();
        Set<OWLSubClassOfAxiom> axioms = new HashSet<>();
        if (props == null)
            props = ont.getObjectPropertiesInSignature();
        for (OWLObjectProperty p : props) {
            LOG.info("Calculating svf subsumptions for "+p);
            axioms.addAll(getInferredSubClassOfGCIAxioms(xr, p));
        }
        return axioms;

    }
}
