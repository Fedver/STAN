package it.disco.unimib.stan.rmlProcessor;

import it.disco.unimib.stan.core.LogEvents;

import java.util.HashMap;
import java.util.List;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author andimou
 */
public class SimpleReferencePerformer extends NodeRMLPerformer {
    
	private static LogEvents log = LogEvents.stan();
	private Resource subject;
    private URI predicate;
    
    public SimpleReferencePerformer(RMLProcessor processor, Resource subject, URI predicate) {
        super(processor);
        this.subject = subject;
        this.predicate = predicate;
    }
    
    @Override
    public void perform(HashMap<String, String> node, SesameDataSet dataset, TriplesMap map) {
        if(map.getSubjectMap().getTermType() == it.disco.unimib.stan.rmlProcessor.TermType.BLANK_NODE || map.getSubjectMap().getTermType() == it.disco.unimib.stan.rmlProcessor.TermType.IRI){
            RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
            RMLProcessor subprocessor = factory.create(map.getLogicalSource().getReferenceFormulation());
            RMLPerformer performer = new NodeRMLPerformer(subprocessor);            
            Resource object = processor.processSubjectMap(dataset, map.getSubjectMap(), node); 
            if (object != null) {
                dataset.add(subject, predicate, object);
                log.debug("[SimpleReferencePerformer:addTriples] Subject "
                        + subject + " Predicate " + predicate + "Object " + object.toString());

                if ((map.getLogicalSource().getReferenceFormulation().toString().equals("CSV"))
                        || (map.getLogicalSource().getReference().equals(map.getLogicalSource().getReference()))) {
                    performer.perform(node, dataset, map, object);
                } else {
                    int end = map.getLogicalSource().getReference().length();
                    log.info("[SimpleReferencePerformer:perform] reference " + map.getLogicalSource().getReference().toString());
                    String expression = "";
                    switch (map.getLogicalSource().getReferenceFormulation().toString()) {
                        case "XPath":
                            expression = map.getLogicalSource().getReference().toString().substring(end);
                            break;
                        case "JSONPath":
                            expression = map.getLogicalSource().getReference().toString().substring(end + 1);
                            break;
                    }
                    processor.execute_node(dataset, expression, map, performer, node, object);
                }
            }
            else
                log.debug("[SimpleReferencePerformer] object of " + map.getName() + 
                        "Triples Map for " + node.toString() + "row was null. Triple was not ");
        }
        else{
            List<String> values = processor.processTermMap(map.getSubjectMap(), node);    
            for(String value : values){
                Resource object = new URIImpl(value);

                dataset.add(subject, predicate, object);
                log.debug("[SimpleReferencePerformer:addTriples] Subject "
                        + subject + " Predicate " + predicate + "Object " + object.toString());
            }   
        }    
    }
}
