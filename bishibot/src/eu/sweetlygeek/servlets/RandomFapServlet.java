package eu.sweetlygeek.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.lang.StringUtils;
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

import eu.sweetlygeek.persistent.GeneratedResponse;
import eu.sweetlygeek.utils.Utils;

/** This servlets gets a video from http://randomfap.com and generates a gadget xml out of it
 * Each generated xml is stocked in base.
 * @author bishiboosh
 *
 */
@SuppressWarnings("serial")
public class RandomFapServlet extends HttpServlet {
	
	public static final String QUERY_URL = "select from " + GeneratedResponse.class.getName() + " where url == cUrl parameters String cUrl";
	
	private static final String RESIZER_JS = "http://bishibot.appspot.com/_wave/robot/resizer.js";
	private static final String FAP_URL = "http://www.randomfap.com/";
	private static final String XPATH_EXP = "/html/body/div[3]/div/center/object";
	private static final Logger LOGGER = Logger.getLogger(RandomFapServlet.class);	
	
	private URLFetchService fetcher;
	private Tidy tidier;
	private XPath xpath;
	private DocumentBuilder builder;
	private PersistenceManager responseManager;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			this.fetcher = URLFetchServiceFactory.getURLFetchService();
			this.tidier = new Tidy();
			tidier.setQuiet(true);
			tidier.setShowWarnings(false);
			this.xpath = new DOMXPath(XPATH_EXP);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			this.builder = dbf.newDocumentBuilder();
			this.responseManager = Utils.getInstance().getPersistenceManager();
		} catch (JaxenException e) {
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

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			resp.setContentType("application/xml");
			String requestURL = req.getRequestURI();
			String xml = getOrGenerate(requestURL);
			if (xml != null)
			{
				resp.getWriter().write(xml);
			}
		} catch (JaxenException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (IOException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (TransformerException e) {
			LOGGER.error("Error while analyzing request", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private String getOrGenerate(String requestURL) throws JaxenException, IOException, TransformerFactoryConfigurationError, TransformerException
	{
		Query query = responseManager.newQuery(QUERY_URL);
		
		List<GeneratedResponse> xmls = (List<GeneratedResponse>) query.execute(requestURL); 
		
		GeneratedResponse response = xmls.isEmpty() ? null : xmls.get(0);
		
		if (response == null)
		{
			String xml = generateContent();
			response = new GeneratedResponse(requestURL, xml);
			responseManager.makePersistent(response);
		}
		
		return response.getXml();
	}
	
	private String generateContent() throws IOException, JaxenException, TransformerFactoryConfigurationError, TransformerException
	{
		HTTPResponse res = fetcher.fetch(new URL(FAP_URL));
		if (res != null)
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(res.getContent());
			Document fapDoc = tidier.parseDOM(bais, null);
			
			Node toEmbedNode = (Node) xpath.selectNodes(fapDoc).get(0);
			
			String toEmbed = Utils.DOMToString(toEmbedNode, false);
			
			Document d = createGadgetDocument(toEmbed);
			
			String xml = Utils.DOMToString(d, true);
			
			return StringUtils.replace(xml, "&amp;", "&");
		}
		return null;
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
