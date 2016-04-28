package ApiRest.apiRest;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.boot.orm.jpa.EntityScan;
@EntityScan
public class TwitterSentimientos {
 
	BigInteger id;
    String name;

    List<String> listaTwitterPositivos;
    List<String> listaTwitterNegativos;
    List<String> listaTwitterNeutros;
    
    public List<String> getListaTwitterPositivos() {
		return listaTwitterPositivos;
	}

	public void setListaTwitterPositivos(List<String> listaTwitterPositivos) {
		this.listaTwitterPositivos = listaTwitterPositivos;
	}

	public List<String> getListaTwitterNegativos() {
		return listaTwitterNegativos;
	}

	public void setListaTwitterNegativos(List<String> listaTwitterNegativos) {
		this.listaTwitterNegativos = listaTwitterNegativos;
	}

	public List<String> getListaTwitterNeutros() {
		return listaTwitterNeutros;
	}

	public void setListaTwitterNeutros(List<String> listaTwitterNeutros) {
		this.listaTwitterNeutros = listaTwitterNeutros;
	}

	

	public String getName() {
   	 return name;
    }
 
    public void setName(String name) {
   	 this.name = name;
    }
 
    public BigInteger getId() {
      	 return id;
       }
    
       public void setId(BigInteger id) {
      	 this.id = id;
       }
 
    public TwitterSentimientos() {
    }
 
}