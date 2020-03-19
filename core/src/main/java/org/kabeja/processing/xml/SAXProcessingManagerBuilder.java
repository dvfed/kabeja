/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 Copyright 2005 Simon Mieth

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.kabeja.processing.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.kabeja.parser.Parser;
import org.kabeja.processing.Generator;
import org.kabeja.processing.InstanceFactory;
import org.kabeja.processing.PostProcessor;
import org.kabeja.processing.PostProcessorConfig;
import org.kabeja.processing.ProcessPipeline;
import org.kabeja.processing.ProcessingManager;
import org.kabeja.processing.XMLPipeline;
import org.kabeja.xml.AggregatorGenerator;
import org.kabeja.xml.SAXFilter;
import org.kabeja.xml.SAXGenerator;
import org.kabeja.xml.SAXSerializer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 * 
 */
public class SAXProcessingManagerBuilder implements ContentHandler {
	public static final String XMLNS_KABEJA_PROCESSING = "http://kabeja.org/processing/1.0";
	public static final String ELEMENT_CONFIGURATION = "configuration";
	public static final String ELEMENT_PARSER = "parser";
	public static final String ELEMENT_PARSERS = "parsers";
	public static final String ELEMENT_SAXSERIALIZER = "serializer";
	public static final String ELEMENT_SAXSERIALIZERS = "serializers";
	public static final String ELEMENT_SAXFILTER = "filter";
	public static final String ELEMENT_FILTER = "filter";
	public static final String ELEMENT_SAXFILTERS = "filters";
	public static final String ELEMENT_PIPELINE = "pipeline";
	public static final String ELEMENT_PIPELINES = "pipelines";
	public static final String ELEMENT_SERIALIZE = "serialize";
	public static final String ELEMENT_PROPERTY = "property";
	public static final String ELEMENT_POSTPROCESSOR = "postprocessor";
	public static final String ELEMENT_POSTPROCESS = "postprocess";
	public static final String ELEMENT_AGGREGATE = "aggregate";
	public static final String ELEMENT_GENERATOR = "generator";
	public static final String ELEMENT_SAXGENERATOR = "saxgenerator";
	public static final String ELEMENT_GENERATE = "generate";
	public static final String ELEMENT_SAXGENERATE = "saxgenerate";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_FACTORY = "factory";
	public static final String ATTRIBUTE_VALUE = "value";
	public static final String ATTRIBUTE_DESCRIPTION = "description";
	private ProcessingManager manager;
	private AggregatorGenerator aggregator;
	private Map<String, Object> properties;
	private ProcessPipeline pipeline;
	private boolean config = false;
	@SuppressWarnings("unused")
	private boolean aggregate = false;
	protected XMLPipeline xmlPipeline;
	protected Attributes elementAtts;	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (namespaceURI.equals(XMLNS_KABEJA_PROCESSING)) {
	     if (ELEMENT_CONFIGURATION.equals(localName)) {
				this.config = false;	
			} 
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String name) throws SAXException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		manager = new ProcessingManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
	   
		if (namespaceURI.equals(XMLNS_KABEJA_PROCESSING)) {
			if (ELEMENT_SAXFILTER.equals(localName) && this.config) {
				this.properties = new HashMap<String, Object>();	
				SAXFilter saxFilter = (SAXFilter) createInstance(atts,new HashMap<String, String>());
				String name = atts.getValue(ATTRIBUTE_NAME);
				this.manager.addSAXFilter(saxFilter, name);
				saxFilter.setProperties(this.properties);
				//System.out.println("Filter: " + name + " instance: " + this.manager.getSAXFilter(name));
			} else if (ELEMENT_SAXSERIALIZER.equals(localName)) {
				this.properties = new HashMap<String, Object>();
				SAXSerializer saxSerializer = (SAXSerializer) createInstance(atts,new HashMap<String, String>());
				saxSerializer.setProperties(this.properties);
				String name = atts.getValue(ATTRIBUTE_NAME);
				this.manager.addSAXSerializer(saxSerializer,  name);
				//System.out.println("Serializer: " + name + " instance: " + this.manager.getSAXSerializer(name));
			} else if (ELEMENT_POSTPROCESSOR.equals(localName)) {
				this.properties = new HashMap<String, Object>();
				PostProcessor pp = (PostProcessor) createInstance(atts,new HashMap<String, String>());
				pp.setProperties(this.properties);
				String name = atts.getValue(ATTRIBUTE_NAME);
				this.manager.addPostProcessor(pp, name);
				//System.out.println("PostProcessor: " + name + " instance: " + this.manager.getPostProcessor(name));
			} else if (ELEMENT_PIPELINE.equals(localName)) {
				this.aggregate = false;
				String name = atts.getValue(ATTRIBUTE_NAME);
				this.pipeline = new ProcessPipeline();
				this.pipeline.setName(name);
				String des = atts.getValue(ATTRIBUTE_DESCRIPTION);
				if (des != null) {
					this.pipeline.setDescription(des);
				}
				this.manager.addProcessPipeline(this.pipeline);
				//System.out.println("Pipeline: " + name + " instance: " + this.manager.getProcessPipeline(name));
			} else if (ELEMENT_SERIALIZE.equals(localName)) {
				this.properties = new HashMap<String, Object>();
				String name = atts.getValue(ATTRIBUTE_NAME);
				this.xmlPipeline.setSAXSerializer(this.manager.getSAXSerializer(name));
				this.xmlPipeline.setSAXSerializerProperties(this.properties);
				//System.out.println("  Pipeline Serialize: " + name);
			} else if (ELEMENT_FILTER.equals(localName)) {
				this.properties = new HashMap<String, Object>();
				String name = atts.getValue(ATTRIBUTE_NAME);
				SAXFilterConfig config = new SAXFilterConfig(this.properties);
				config.setFilterName(name);
				this.pipeline.addSAXFilterConfig(config);
				//System.out.println("  Pipeline SAXFilterConfig: " + name);
			} else if (ELEMENT_PROPERTY.equals(localName)) {
				this.properties.put(atts.getValue(ATTRIBUTE_NAME), atts.getValue(ATTRIBUTE_VALUE));
			} else if (ELEMENT_POSTPROCESS.equals(localName)) {
				this.properties = new HashMap<String, Object>();
				String name = atts.getValue(ATTRIBUTE_NAME);
				PostProcessorConfig config = new PostProcessorConfig(this.properties);
				config.setPostProcessorName(name);
				this.pipeline.addPostProcessorConfig(config);
				//System.out.println("  Pipeline Postprocess: " + name);
			} else if (ELEMENT_CONFIGURATION.equals(localName)) {
				this.config = true;
			} else if (ELEMENT_GENERATOR.equals(localName)) {
				this.properties = new HashMap<String, Object>();
				String name = atts.getValue(ATTRIBUTE_NAME);
				@SuppressWarnings("unused")
				String clazz = (atts.getValue(ATTRIBUTE_CLASS));
				Generator generator = (Generator) createInstance(atts,new HashMap<String, String>());
				this.manager.addGenerator(generator,name);
				//System.out.println("Generator: " + name + " instance: " + this.manager.getGenerator(name));
			} else if (ELEMENT_GENERATE.equals(localName)) {
				this.properties = new HashMap<String, Object>();
				String name = atts.getValue(ATTRIBUTE_NAME);
				this.pipeline.setGenerator(this.manager.getGenerator(name));
			    this.pipeline.setGeneratorProperties(this.properties);
				//System.out.println("  Pipeline Generate: " + name);
			} else if (ELEMENT_AGGREGATE.equals(localName)) {
				this.aggregate = true;
				this.aggregator = new AggregatorGenerator();
				this.xmlPipeline.setSAXGenerator(this.aggregator);
				//System.out.println("  Pipeline Aggregator");
			}else if(ELEMENT_SAXGENERATOR.equals(localName)){
				this.properties = new HashMap<String, Object>();
				String name = atts.getValue(ATTRIBUTE_NAME);
				Object obj =createInstance(atts,new HashMap<String, String>());
				if(obj instanceof SAXGenerator){
					SAXGenerator gen = (SAXGenerator)obj;
					gen.setProperties(this.properties);
					this.manager.addSAXGenerator(gen, name);
					//System.out.println("Saxgenerator: " + name + " instance: " + this.manager.getSAXGenerator(name));
				}
			}else if(ELEMENT_SAXGENERATE.equals(localName)){
				this.properties = new HashMap<String, Object>();
				String name = atts.getValue(ATTRIBUTE_NAME);
				this.xmlPipeline = new XMLPipeline();
				this.xmlPipeline.setProperties(this.properties);
				this.xmlPipeline.setSAXGenerator(this.manager.getSAXGenerator(name));
				this.pipeline.setGenerator(this.xmlPipeline);
				//System.out.println("  Pipeline Saxgenerate: " + name);
			}else if(ELEMENT_PARSER.equals(localName)){
				String name = atts.getValue(ATTRIBUTE_NAME);
				Object obj =createInstance(atts,new HashMap<String, String>());
				if(obj instanceof Parser){
					this.manager.addParser((Parser)obj, name);
					//System.out.println("Parser: " + name + " instance: " + this.manager.getParser(name));
				}
			}
		} 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	public ProcessingManager getManager() {
		return this.manager;
	}

	protected Object createInstance(Attributes atts, Map<String, String> properties) {
		try {
		    String clazz = atts.getValue(ATTRIBUTE_CLASS);
            String factory = atts.getValue(ATTRIBUTE_FACTORY);
            if(factory != null){
                Class<?> cl = this.getClass().getClassLoader().loadClass(factory);
                Object obj = cl.newInstance();
                if(obj instanceof InstanceFactory){
                    return ((InstanceFactory)obj).createInstance(properties);
                }              
            }else if(clazz != null){
				Class<?> cl = this.getClass().getClassLoader().loadClass(clazz);
                return cl.newInstance();
            }
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return null;
	}

	/**
	 * 
	 * @param in
	 *            the InputStream
	 * @return The ProcessingManager build from the XML description
	 */
	public static ProcessingManager buildFromStream(InputStream in) {
		SAXProcessingManagerBuilder builder = new SAXProcessingManagerBuilder();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);

			// factory.setXIncludeAware(true);
			try {
				factory.setFeature("http://apache.org/xml/features/xinclude",
						true);
			} catch (Exception e) {
				// OK older jaxp
				System.out
						.println("No XInclude support (use JAXP 1.4 or later for XInclude)");
			}

			try {
				XMLReader saxparser = factory.newSAXParser().getXMLReader();

				saxparser.setContentHandler(builder);
				saxparser.parse(new InputSource(in));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return builder.getManager();
	}
}
