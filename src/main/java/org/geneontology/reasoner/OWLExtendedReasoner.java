package org.geneontology.reasoner;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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
	 * Note that this is not a standard reasoner method.
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
	   

}
