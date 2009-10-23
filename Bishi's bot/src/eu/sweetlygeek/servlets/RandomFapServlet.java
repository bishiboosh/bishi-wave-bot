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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

@SuppressWarnings("serial")
public class RandomFapServlet extends HttpServlet {
	
	private static final String RESIZER_JS = "http://bishibot.appspot.com/_wave/robot/resizer.js";
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
			resp.setContentType("application/xml");
			
			HTTPResponse res = fetcher.fetch(new URL(FAP_URL));
			if (res != null)
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(res.getContent());
				Document fapDoc = tidier.parseDOM(bais, null);
				
				Node toEmbedNode = (Node) xpath.selectNodes(fapDoc).get(0);
				
				StringWriter sw = new StringWriter();
				Source s = new DOMSource(toEmbedNode);
				Result r = new StreamResult(sw);
				transformer.transform(s, r);
				
				Document d = createGadgetDocument(sw.toString());
				
				s = new DOMSource(d);
				r = new StreamResult(resp.getOutputStream());
				transformer.transform(s, r);
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
	
	private Document createGadgetDocument(String toEmbed)
	{
		Document d = builder.newDocument();
		
		d.setXmlVersion("1.0");
		d.setXmlStandalone(true);
		
		Element module = d.createElement("Module");
		d.appendChild(module);
		
		Element mPrefs = d.createElement("ModulePrefs");
		mPrefs.setAttribute("title", "Random Fap Gadget");
		module.appendChild(mPrefs);
		
		mPrefs.appendChild(createRequireElement("dynamic-height", d));
		mPrefs.appendChild(createRequireElement("wave", d));
		
		Element content = d.createElement("Content");
		content.setAttribute("type", "html");
		module.appendChild(content);
		
		StringBuffer buf = new StringBuffer();
		buf.append("<script type='text/javascript' src='");
		buf.append(RESIZER_JS);
		buf.append("'>");
		buf.append("</script>");
		buf.append("\n");
		buf.append(toEmbed);
		
		CDATASection contentData = d.createCDATASection(buf.toString());
		
		content.appendChild(contentData);
		
		return d;
	}
	
	private Element createRequireElement(String require, Document d)
	{
		Element result = d.createElement("Require");
		result.setAttribute("feature", require);
		return result;
	}

}
