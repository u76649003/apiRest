package ApiRest.apiRest;

import java.io.BufferedReader;

import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClass {
	private TwitterClass(){}
	public static Twitter crearTwitter(){
		//Obtenccion del certificado para poder trabajar con twitter
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true)
    	  .setOAuthConsumerKey("WX0FvnWENusasa09R8IVG6FFm")
    	  .setOAuthConsumerSecret("Z6cXLTiqkU6NvlhqEHD7Br3S0jhmtUiLPvOPXCqLcrhQKFMYNr")
    	  .setOAuthAccessToken("509341639-6R41f6i6WUAQn1xfzI8uNbPeyIhQvkMnXYaszNv6")
    	  .setOAuthAccessTokenSecret("We08mCgnLMPXn6zl8UrKwGegac0Z4ksrI0LwhAbBoxp5q");
    	TwitterFactory tf = new TwitterFactory(cb.build());
    	return tf.getInstance();
	}
	public static Twitter crearTwitter(String ConsumerKey, String ConsumerSecret, String AccessToken, String TokenSecret){
		//Obtenccion del certificado para poder trabajar con twitter
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true)
    	  .setOAuthConsumerKey(ConsumerKey)
    	  .setOAuthConsumerSecret(ConsumerSecret)
    	  .setOAuthAccessToken(AccessToken)
    	  .setOAuthAccessTokenSecret(TokenSecret);
    	TwitterFactory tf = new TwitterFactory(cb.build());
    	return tf.getInstance();
	}
	public static List<String> buscarTwitter(String nombre, Twitter twitter) throws TwitterException{
		ArrayList<String> listaMensajes = new ArrayList<String>();
    	//Buscar Twitter por usuario, @delta, hashtag #delta, o la palabra delta
		Query query = new Query(nombre);
        QueryResult result;
        int n=0;
        do {
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                String mensajeTweet="@" + tweet.getUser().getScreenName() + " - " + tweet.getText();
                listaMensajes.add(mensajeTweet);
                //System.out.println(mensajeTweet);
            }
            n++;
            System.out.println(n);
        } while (n<15 && (query = result.nextQuery()) != null);
		
	
        return listaMensajes;
	}
		
	public static int funcionSentimientos(String cadena) throws Exception{
		int sentimientos = 0;
		//Separador de palabras
		String delimitadores= "[ .,;?!¡¿\'\"\\[\\]]+";
		
		//		String cadenaTraducida = Translate.execute(cadena, Language.SPANISH, Language.ENGLISH);

        String[] palabrasSeparadas = cadena.split(delimitadores);
        for(String palabra: palabrasSeparadas)
        	sentimientos = sentimientos + BuscadorPalabras(palabra, "../apiRest/src/main/resources/positive.txt") - BuscadorPalabras(palabra, "../apiRest/src/main/resources/negative.txt"); 
		return sentimientos;
	}
	public static int BuscadorPalabras(String palabra, String fichero) throws Exception{
		FileReader fr = new FileReader(fichero);
        BufferedReader br = new BufferedReader(fr);
        String linea;
       
        
        while((linea = br.readLine())!= null)
        	
        	if(linea.equalsIgnoreCase(palabra))return 1;
     
        return 0;
        }
	
}
