package eu.sweetlygeek.utils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

/** Global utils
 * @author bishiboosh
 *
 */
public class Utils {

	public static final String BOT_ID = "bishibot@appspot.com";

	private static final Random random = new Random();
	
	private static PersistenceManager persistenceManager;
	private static Utils instance;
	
	private Utils()
	{
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");
		persistenceManager = pmf.getPersistenceManager(); 
	}

	/** Return random values from a value
	 * @param <T> key type
	 * @param <U> value type
	 * @param map
	 * @param nb number of random values
	 * @return map with nb random values of input map
	 */
	public static <T, U> Map<T, U> pickAtRandom(Map<T, U> map, int nb)
	{
		int size = map.size();
		if (size < nb)
		{
			return map;
		}
		else
		{
			Map<T, U> result = new HashMap<T, U>();

			List<T> keys = new ArrayList<T>(map.keySet());
			while (result.size() < nb)
			{
				int choice = random.nextInt(size);
				T key = keys.get(choice);
				result.put(key, map.get(key));
			}

			return result;
		}
	}

	/** Copy some keys and their value from a map to another
	 * @param <T>
	 * @param <U>
	 * @param from
	 * @param keys
	 * @return
	 */
	public static <T, U> Map<T, U> copyMapsFromKey(Map<T, U> from, Set<T> keys)
	{
		Map<T, U> result = new HashMap<T, U>();
		for (T key : keys)
		{
			result.put(key, from.get(key));
		}
		return result;
	}
	
	/** Transform a DOM node to a string
	 * @param node
	 * @param withXMLDeclaration
	 * @return
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static String DOMToString(Node node, boolean withXMLDeclaration) throws TransformerFactoryConfigurationError, TransformerException
	{
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, withXMLDeclaration ? "no" : "yes");
		StringWriter sw = new StringWriter();
		Source s = new DOMSource(node);
		Result r = new StreamResult(sw);
		t.transform(s, r);
		return sw.toString();
	}
	
	public PersistenceManager getPersistenceManager()
	{
		return persistenceManager;
	}
	
	public synchronized static Utils getInstance()
	{
		if (instance == null)
		{
			instance = new Utils();
		}
		return instance;
	}
}
