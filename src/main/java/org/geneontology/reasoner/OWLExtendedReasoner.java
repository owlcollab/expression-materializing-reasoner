package org.geneontology.reasoner;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;

/**
 * Extends standard OWL reasoning to allow for querying of ancestor class expressions
 * 
 * @author cjm
 *
 */
public interface OWLExtendedReasoner extends OWLReasoner {
	
    /**
     * Materialize expressions involving the property p
     * 
     * Different implementations may handle this differently. Currently the only implementation generates
     * SomeValuesFrom expressions
     * 
     * @param p
     */
    public void materializeExpressions(OWLObjectProperty p);
    
	/**
	 * Finds all superclasses of a class expression ce, where the returned
	 * classes may be either (a) a named (non-anonymous) superClass or (b)
	 * an anomymous class expression, typically of the form "P SOME Y"
	 * 
	 * 
	 * Note that this is not a standard reasoner method. The standard
	 * OWLReasoner API provides getSuperClasses, corresponding to (a) above.
	 * 
	 * @param ce
	 * @param direct
	 * @return all superclasses, where superclasses can include anon class expressions
	 * @throws InconsistentOntologyException
	 * @throws ClassExpressionNotInProfileException
	 * @throws FreshEntitiesException
	 * @throws ReasonerInterruptedException
	 * @throws TimeOutException
	 */
	public Set<OWLClassExpression> getSuperClassExpressions(OWLClassExpression ce,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException;
	
	/**
	 * Note that this is not a standard reasoner method.
	 * 
	 * @param ce
	 * @param p
	 * @param direct
	 * @return all superclasses, where superclasses can include anon class expressions using p
	 * @throws InconsistentOntologyException
	 * @throws ClassExpressionNotInProfileException
	 * @throws FreshEntitiesException
	 * @throws ReasonerInterruptedException
	 * @throws TimeOutException
	 */
	public Set<OWLClass> getSuperClassesOver(OWLClassExpression ce,
			OWLObjectProperty p,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException;
	   

	/**
	 * Given a property p, and a base class c, find all classes s such that
	 * (p some c) SubClassOf (p some s)
	 * 
	 * 
	 * 
	 * @param baseClass
	 * @param p
	 * @param direct
	 * @return classes
	 * @throws InconsistentOntologyException
	 * @throws ClassExpressionNotInProfileException
	 * @throws FreshEntitiesException
	 * @throws ReasonerInterruptedException
	 * @throws TimeOutException
	 */
	public Set<OWLObjectSomeValuesFrom> getSomeValuesFromSuperClasses(OWLClass baseClass,
            OWLObjectProperty p,
            boolean direct)
                    throws InconsistentOntologyException,
                    ClassExpressionNotInProfileException, FreshEntitiesException,
                    ReasonerInterruptedException, TimeOutException;
	
    /**
     * Returns all axioms (p some c) SubClassOf (q some s)
     * for a given p
     * 
     * @param p
     * @return subClassOf axioms between someValuesFrom expressions
     * @throws InconsistentOntologyException
     * @throws ClassExpressionNotInProfileException
     * @throws FreshEntitiesException
     * @throws ReasonerInterruptedException
     * @throws TimeOutException
     */
    public Set<OWLSubClassOfAxiom> getSomeValuesFromSubsumptions(OWLObjectProperty p) 
            throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, FreshEntitiesException,
            ReasonerInterruptedException, TimeOutException;
    
    /**
     * Returns all axioms (p some c) SubClassOf (q some s)
     * 
     * @return subClassOf axioms between someValuesFrom expressions
     * @throws InconsistentOntologyException
     * @throws ClassExpressionNotInProfileException
     * @throws FreshEntitiesException
     * @throws ReasonerInterruptedException
     * @throws TimeOutException
     */
    public Set<OWLSubClassOfAxiom> getInferredSubClassOfGCIAxioms() 
            throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, FreshEntitiesException,
            ReasonerInterruptedException, TimeOutException;

    /**
     * as 
     * 
     * @param props
     * @return
     * @throws InconsistentOntologyException
     * @throws ClassExpressionNotInProfileException
     * @throws FreshEntitiesException
     * @throws ReasonerInterruptedException
     * @throws TimeOutException
     */
    public Set<OWLSubClassOfAxiom> getInferredSubClassOfGCIAxioms(
            Set<OWLObjectProperty> props) throws InconsistentOntologyException,
    ClassExpressionNotInProfileException, FreshEntitiesException,
    ReasonerInterruptedException, TimeOutException;

}
