package eu.sweetlygeek.persistent;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/** Persistent object stocking urls and generated xmls
 * @author bishiboosh
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GeneratedResponse {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String url;
	@Persistent
	private Text xml;
	
	public GeneratedResponse(String url, String xml)
	{
		this.url = url;
		this.xml = new Text(xml);
	}

	public String getUrl() {
		return url;
	}

	public String getXml() {
		return xml.getValue();
	}

}
