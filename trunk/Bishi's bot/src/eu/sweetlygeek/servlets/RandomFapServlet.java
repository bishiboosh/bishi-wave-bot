package eu.sweetlygeek.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
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

@SuppressWarnings("serial")
public class RandomFapServlet extends HttpServlet {
	
	private static final String FAP_URL = "http://www.randomfap.com/";
	private static final String XPATH_EXP = "/html/body/div[3]/div/center/object";
	private static final Logger LOGGER = Logger.getLogger(RandomFapServlet.class);
	
	private URLFetchService fetcher;
	private Tidy tidier;
	private XPath xpath;
	private Transformer transformer;
	private DocumentBuilder builder;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			this.fetcher = URLFetchServiceFactory.getURLFetchService();
			this.tidier = new Tidy();
			tidier.setQuiet(true);
			tidier.setShowWarnings(false);
			this.xpath = new DOMXPath(XPATH_EXP);
			TransformerFactory tf = TransformerFactory.newInstance();
			this.transformer = tf.newTransformer();
			this.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			this.builder = dbf.newDocumentBuilder();
		} catch (JaxenException e) {
			LOGGER.error("Error while initiating servlet", e);
			throw new ServletException("Error while initiating servlet", e);
		} catch (TransformerConfigurationException e) {
			LOGGER.error("Error while initiating servlet", e);
			throw new ServletException("Error while initiating servlet", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Error while initiating servlet", e);
			throw new ServletException("Error while initiating servlet", e);
		} catch (TransformerFactoryConfigurationError e) {
			LOGGER.error("Error while initiating servlet", e);
			throw new ServletException("Error while initiating servlet", e);
		} catch (ParserConfigurationException e) {
			LOGGER.error("Error while initiating servlet", e);
			throw new ServletException("Error while initiating servlet", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			HTTPResponse res = fetcher.fetch(new URL(FAP_URL));
			if (res != null)
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(res.getContent());
				Document fapDoc = tidier.parseDOM(bais, null);
				
				Node toEmbedNode = (Node) xpath.selectNodes(fapDoc).get(0);
				
				Source source = new DOMSource(toEmbedNode);
				StringWriter sw = new StringWriter();
				Result result = new StreamResult(sw);
				transformer.transform(source, result);
				
				// TODO : output du xml
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

}
