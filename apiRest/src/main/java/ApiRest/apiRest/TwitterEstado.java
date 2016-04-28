package ApiRest.apiRest;

import java.math.BigInteger;
import java.util.List;

import org.springframework.boot.orm.jpa.EntityScan;
@EntityScan
public class TwitterEstado {
	private String text;
	private String screenName;
	private Long idText;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public Long getIdText() {
		return idText;
	}
	public void setIdText(Long idText) {
		this.idText = idText;
	}

	
}

