This wraps an existing reasoner to implement OWLExtendedReasoner.
 
It works by materializing expressions of the form "R some Y" as equivalence axioms prior to reasoning.

After reasoning, it can retrieve these anonymous superclasses.

Currently limited to a single level of nesting - in principle it could be extended to expressions of depth k
 
In terms of performance the biggest impact are the number of OWLObjectProperties for which the materialization is required. 
It is usually *NOT* recommended to use all properties of an ontology signature.

This version was designed and implemented by @cmungall
 
 