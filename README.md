[![CI](https://github.com/owlcollab/expression-materializing-reasoner/actions/workflows/ci.yml/badge.svg)](https://github.com/owlcollab/expression-materializing-reasoner/actions/workflows/ci.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.geneontology/expression-materializing-reasoner/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.geneontology/expression-materializing-reasoner)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.geneontology/expression-materializing-reasoner/badge.svg)](http://www.javadoc.io/doc/org.geneontology/expression-materializing-reasoner)

This wraps an existing reasoner to implement OWLExtendedReasoner.
 
It works by materializing expressions of the form "R some Y" as equivalence axioms prior to reasoning.

After reasoning, it can retrieve these anonymous superclasses.

Currently limited to a single level of nesting - in principle it could be extended to expressions of depth k
 
In terms of performance the biggest impact are the number of OWLObjectProperties for which the materialization is required. 
It is usually *NOT* recommended to use all properties of an ontology signature.

This version was designed and implemented by @cmungall

## Releasing

Releases go to Maven Central through the [Central Portal](https://central.sonatype.com). You need publish rights on the `org.geneontology` namespace, a Portal user token in `~/.m2/settings.xml` under server id `central`, and a GPG key published to a public keyserver.

```
mvn versions:set -DnewVersion=X.Y.Z -DgenerateBackupPoms=false
mvn clean deploy -Prelease
```
 
 
