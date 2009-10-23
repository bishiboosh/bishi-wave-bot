package eu.sweetlygeek.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.wave.api.Gadget;
import com.google.wave.api.Wavelet;

public class RandomFapParser extends BlipParser {
	
	private static final String FAP_TAG = "bot:fap";
	
	private static final String EMBEDDER_URL = "http://wave-ide.appspot.com/html.xml";
	private static final String FAP_URL = "http://www.randomfap.com/";
	private static final String XPATH_EXP = "/html/body/div[3]/div/center/object";
	private static final Logger LOGGER = Logger.getLogger(RandomFapParser.class);
	
	private URLFetchService fetcher;
	private Tidy tidier;
	private XPath xpath;
	private Transformer transformer;
	
	public RandomFapParser() throws JaxenException, TransformerConfigurationException
	{
		this.fetcher = URLFetchServiceFactory.getURLFetchService();
		this.tidier = new Tidy();
		tidier.setQuiet(true);
		tidier.setShowWarnings(false);
		this.xpath = new DOMXPath(XPATH_EXP);
		TransformerFactory tf = TransformerFactory.newInstance();
		this.transformer = tf.newTransformer();
		this.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	};
	
	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		try {
			HTTPResponse res = fetcher.fetch(new URL(FAP_URL));
			if (res != null)
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(res.getContent());
				Document doc = tidier.parseDOM(bais, null);
				
				Node toEmbedNode = (Node) xpath.selectNodes(doc).get(0);
				
				Source source = new DOMSource(toEmbedNode);
				StringWriter sw = new StringWriter();
				Result result = new StreamResult(sw);
				transformer.transform(source, result);
				
				Gadget videoGadget = new Gadget(EMBEDDER_URL);
				videoGadget.setField("code", sw.toString());
				currentWavelet.appendBlip().getDocument().appendElement(videoGadget);
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (JaxenException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (IOException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (TransformerException e) {
			LOGGER.error("Error while analyzing request", e);
		}
	}

	@Override
	protected String getTag() {
		return FAP_TAG;
	}

}
