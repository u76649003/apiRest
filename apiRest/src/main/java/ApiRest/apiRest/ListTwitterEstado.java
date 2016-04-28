package ApiRest.apiRest;

import java.math.BigInteger;
import java.util.List;

import org.springframework.boot.orm.jpa.EntityScan;

@EntityScan
public class ListTwitterEstado {
	
	private BigInteger id;
	private List<TwitterEstado> twitterEstado;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public List<TwitterEstado> getTwitterEstado() {
		return twitterEstado;
	}
	public void setTwitterEstado(List<TwitterEstado> twitterEstado) {
		this.twitterEstado = twitterEstado;
	}
	
	
}