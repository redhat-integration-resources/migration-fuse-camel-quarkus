package org.custom;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.value.ObjectValue;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.SequenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Produce;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tree.tiny.TinyDocumentImpl;


public class MyExtensionFunction1 extends ExtensionFunctionDefinition {

    //Camel producer to invoke the route to convert XML into JSON
    @Produce
    protected ProducerTemplate template;

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(MyExtensionFunction1.class);

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("", "http://custom/extension/functions", "xml-to-json");
    }

    @Override
    public int getMinimumNumberOfArguments() {
        return 1;
    }

    @Override
    public int getMaximumNumberOfArguments() {
        return 1;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{SequenceType.SINGLE_NODE};
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        int resultCardinality = 1;
        return SequenceType.makeSequenceType(SequenceType.SINGLE_STRING.getPrimaryType(), resultCardinality);
    }

	@Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            private static final long serialVersionUID = 1L;

            @Override
            public Sequence call(XPathContext xPathContext, Sequence[] arguments) throws XPathException {
                // 1st argument (mandatory, index 0)
                Object base = arguments[0].iterate().next();

                String xml="<map/>";
                try{
                    Processor processor = new Processor(false); // False = does not required a feature from a licensed version of Saxon.
                    Serializer serializer = processor.newSerializer();
                    // Other properties found here: http://www.saxonica.com/html/documentation/javadoc/net/sf/saxon/s9api/Serializer.Property.html
                    serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
                    serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
                    XdmNode xdmNode = new XdmNode(((TinyDocumentImpl)base).getDocumentRoot());
                    xml = serializer.serializeNodeToString(xdmNode);
                    // System.out.println("serialize result: "+xml);
                }catch(Exception e){
                    e.printStackTrace();
                    throw new XPathException("custom function failure: "+e.getMessage());
                }

                //invoke Camel route to resolve XML to JSON
                Object response = template.requestBody("direct:xml2json", xml);

                Item result = new StringValue(response.toString());
                return SequenceTool.toLazySequence(SingletonIterator.makeIterator(result));
            }
        };
    }
}
