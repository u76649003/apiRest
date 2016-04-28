package ApiRest.apiRest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

@RestController
public class TwitterController {
	private static BigInteger nextIdFriends;
	private static BigInteger nextIdFollowers;
	private static BigInteger nextIdEstados;
	private static BigInteger nextId;
	private static Map<BigInteger, TwitterSentimientos> twitterMap;
	private static Map<BigInteger, Friends> twitterFriends;
	private static Map<BigInteger, Followers> twitterFollowers;
	private static Map<BigInteger, ListTwitterEstado> twitterEstado;
	private static Twitter tweet;
	
	@RequestMapping(value="/api/inicializar", method= RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Twitter> inicializate(@RequestParam("Ckey")String consumerKey, @RequestParam("Csecret")String consumerSecret, @RequestParam("Atoken")String accessToken, @RequestParam("TSecret")String tokenSecret){
		tweet = TwitterClass.crearTwitter(consumerKey,consumerSecret,accessToken,tokenSecret);
		
		return new ResponseEntity<Twitter>(tweet, HttpStatus.OK);
	}
	
	//Ver estados de mi Twitter o de otro Twitter
	@RequestMapping(value="/api/verMenciones", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Status>> verEstados(@RequestParam(value="usuario", required=false)String usuario) throws TwitterException{
		QueryResult result;
		Query query=null;

		if(usuario!=null)
		query= new Query(usuario);
		else
			query=new Query(tweet.getScreenName());
		do {
			result = tweet.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText()+" - "+tweet.getId());
            }
        } while ((query = result.nextQuery()) != null);
		return new ResponseEntity<List<Status>>(result.getTweets(), HttpStatus.OK);
	}
	
	//Almacenar estados de mi Twitter o de otro Twitter
		@RequestMapping(value="/api/almacenarMenciones", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<TwitterEstado> almacenarMenciones(@RequestParam(value="usuario", required=false)String usuario) throws TwitterException{
			if(twitterEstado == null){
				twitterEstado = new HashMap<BigInteger, ListTwitterEstado>();
				nextIdEstados = BigInteger.ONE;
			}
			QueryResult result;
			Query query=null;
			List<TwitterEstado> twitterEstados = new ArrayList<TwitterEstado>();
			
			if(usuario!=null)
			query= new Query(usuario);
			else
				query=new Query(tweet.getScreenName());
			do {
				result = tweet.search(query);
	            List<Status> tweets = result.getTweets();
	            for (Status tweet : tweets) {
	            	TwitterEstado twitter = new TwitterEstado();
	                twitter.setScreenName("@" + tweet.getUser().getScreenName());
	                twitter.setText(tweet.getText());
	                twitter.setIdText(tweet.getId());
	                
	                twitterEstados.add(twitter);
	            }
	            
	        } while ((query = result.nextQuery()) != null);
			
			ListTwitterEstado listTwitterEstado = new ListTwitterEstado();
			listTwitterEstado.setId(nextIdEstados);
			listTwitterEstado.setTwitterEstado(twitterEstados);
			nextIdEstados=nextIdEstados.add(BigInteger.ONE);
			twitterEstado.put(listTwitterEstado.getId(), listTwitterEstado);
			return new ResponseEntity<TwitterEstado>(HttpStatus.OK);
		}
		
		//Ver estados almacenados de mi Twitter o de otro Twitter
		@RequestMapping(value="/api/verEstadosAlmacenados", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Collection<ListTwitterEstado>> getEstados(){
			Collection<ListTwitterEstado> tweets = twitterEstado.values();
			return new ResponseEntity<Collection<ListTwitterEstado>>(tweets, HttpStatus.OK);
		}
		//Ver estados almacenados de mi Twitter o de otro Twitter con la id
				@RequestMapping(value="/api/verEstadosAlmacenados/{id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<ListTwitterEstado> getEstadosId(@PathVariable("id")BigInteger id){
					ListTwitterEstado tweets = twitterEstado.get(id);
					return new ResponseEntity<ListTwitterEstado>(tweets, HttpStatus.OK);
				}
				//Ver estados almacenados de mi Twitter o de otro Twitter con la id
				@RequestMapping(value="/api/verEstadoAlmacenado", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<TwitterEstado> getEstadosIdidText(@RequestParam(value="id")BigInteger id, @RequestParam(value="idText")Long idText){
					ListTwitterEstado tweets = twitterEstado.get(id);
					TwitterEstado tweetEstado = null;
					for(TwitterEstado tw: tweets.getTwitterEstado()){
						System.out.println(tw.getText()+"-"+tw.getIdText()+" "+idText);
						if(tw.getIdText().equals(idText)){tweetEstado=tw;}
					}
					return new ResponseEntity<TwitterEstado>(tweetEstado, HttpStatus.OK);
				}
		//Actualizar Estado de mi Twitter
		@RequestMapping(value="/api/nuevoTweet", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,
				produces= MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<String> EnviarTwitter(@RequestBody String actualizarEstado, @RequestParam(value="usuario", required=false)String usuario){
			try {
				if(usuario!=null)
				tweet.updateStatus(actualizarEstado+" "+usuario);
				else tweet.updateStatus(actualizarEstado);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ResponseEntity<String>(actualizarEstado, HttpStatus.OK);
		}
		@RequestMapping(value="/api/borrarEstado/{id}", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Twitter> borrarEstado(@PathVariable("id")Long id) throws TwitterException{
			tweet.destroyStatus(id);
			//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<Twitter>(HttpStatus.NO_CONTENT);
		}
		@RequestMapping(value="/api/retweetEstado/{id}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Twitter> actualizarEstado(@PathVariable("id")Long id) throws TwitterException{
			tweet.retweetStatus(id);
			//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<Twitter>(HttpStatus.OK);
		}
		@RequestMapping(value="/api/borrarEstadosAlmacenados/{id}", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<String> borrarEstado(@PathVariable("id")BigInteger id) throws TwitterException{
			twitterEstado.remove(id);
			//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<String>("Borrado completado",HttpStatus.OK);
		}
		@RequestMapping(value="/api/borrarEstadoAlmacenado", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<String> borrarEstadoAlmacenado(@RequestParam(value="id")BigInteger id, @RequestParam(value="idText")Long idText) throws TwitterException{
			
			ListTwitterEstado tweets = twitterEstado.get(id);
			twitterEstado.remove(id);
			List<TwitterEstado> tweetEstado = new ArrayList<TwitterEstado>();
			for(TwitterEstado tw: tweets.getTwitterEstado()){
				
				if(tw.getIdText()!=idText){tweetEstado.add(tw);}
			}
			tweets.setTwitterEstado(tweetEstado);
			twitterEstado.put(tweets.getId(), tweets);
			//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<String>("Borrado completado",HttpStatus.OK);
		}
	
		
		
		//Ver amigos de mi twitter o de otro
		@RequestMapping(value="/api/verFriends", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<List<FriendsAndFollower>> verFriends(@RequestParam(value="usuario", required=false)String usuario) throws TwitterException{
			long cursor = -1;
			PagableResponseList<User> friendsIDs =null;
			List<FriendsAndFollower> listFriends = new ArrayList<FriendsAndFollower>();
			long lCursor = -1;
			if(usuario==null)usuario=tweet.getScreenName();
			do{
		    friendsIDs = tweet.getFriendsList(usuario, lCursor);
		    System.out.println("next cursor"+friendsIDs.getNextCursor());
		    for(User user: friendsIDs){
		    	FriendsAndFollower friend = new FriendsAndFollower();
		    	friend.setIdUser(user.getId());
		    	friend.setName(user.getName());
		    	friend.setScreemName("@"+user.getScreenName());
		    	friend.setDescription(user.getDescription());
		    	listFriends.add(friend);
		    }
		    lCursor=friendsIDs.getNextCursor();
		  
			} while (lCursor != 0);
			return new ResponseEntity<List<FriendsAndFollower>>(listFriends, HttpStatus.OK);

		}
		//Almacenar Friends
		@RequestMapping(value="/api/AlmacenarFriends", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,
				produces= MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Friends> almacenarFriends(@RequestParam(value="usuario", required=false)String usuario) throws IllegalStateException, TwitterException{
			if(usuario==null)usuario=tweet.getScreenName();
			ResponseEntity<List<FriendsAndFollower>> listFriends = this.verFriends(usuario);
			if(twitterFriends == null){
				twitterFriends = new HashMap<BigInteger, Friends>();
				nextIdFriends = BigInteger.ONE;
			}
			Friends amigos = new Friends();
			amigos.setId(nextIdFriends);
			amigos.setListFriends(listFriends.getBody());
			amigos.setName(usuario);
			nextIdFriends=nextIdFriends.add(BigInteger.ONE);
			twitterFriends.put(amigos.getId(), amigos);
			return new ResponseEntity<Friends>(amigos, HttpStatus.OK);
		}
		//Ver estados almacenados de mi Twitter o de otro Twitter
		@RequestMapping(value="/api/verFriendsAlmacenados", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Collection<Friends>> getFriends(){
			Collection<Friends> tweets = twitterFriends.values();
			return new ResponseEntity<Collection<Friends>>(tweets, HttpStatus.OK);
		}
		//Ver follower de mi amigo o de otro
		@RequestMapping(value="/api/verFriendsAlmacenado", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Friends> verFriendsAlmacenado(@RequestParam(value="id")BigInteger id) throws TwitterException{
			
			return new ResponseEntity<Friends>(twitterFriends.get(id), HttpStatus.OK);

		}
		
		
		@RequestMapping(value="/api/borrarFriendsAlmacenados/{id}", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<String> borrarFriends(@PathVariable("id")BigInteger id) throws TwitterException{
			twitterFriends.remove(id);
			//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<String>("Borrado completado",HttpStatus.OK);
		}
		@RequestMapping(value="/api/borrarFriendsAlmacenado", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<String> borrarFriendsAlmacenado(@RequestParam(value="id")BigInteger id, @RequestParam(value="idUser")Long idUser) throws TwitterException{
			
			Friends tweets = twitterFriends.get(id);
			twitterFriends.remove(id);
			List<FriendsAndFollower> tweetFriend = new ArrayList<FriendsAndFollower>();
			for(FriendsAndFollower tw: tweets.getListFriends()){
				
				if(tw.getIdUser()!=idUser){tweetFriend.add(tw);}
			}
			tweets.setListFriends(tweetFriend);
			twitterFriends.put(tweets.getId(), tweets);
			//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<String>("Borrado completado",HttpStatus.OK);
		}
		
		
		
		
		
		//Ver follower de mi amigo o de otro
				@RequestMapping(value="/api/verFollower", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<List<FriendsAndFollower>> verFollower(@RequestParam(value="usuario", required=false)String usuario) throws TwitterException{
					long cursor = -1;
					PagableResponseList<User> followersIDs = null;
					List<FriendsAndFollower> listFollowers = new ArrayList<FriendsAndFollower>();
					long lCursor = -1;
					if(usuario==null)usuario=tweet.getScreenName();
					do{
						followersIDs = tweet.getFollowersList(usuario, lCursor);
				    System.out.println("next cursor"+followersIDs.getNextCursor());
				    for(User user: followersIDs){
				    	FriendsAndFollower followers = new FriendsAndFollower();
				    	followers.setIdUser(user.getId());
				    	followers.setName(user.getName());
				    	followers.setScreemName("@"+user.getScreenName());
				    	followers.setDescription(user.getDescription());
				    	listFollowers.add(followers);
				    }
				    lCursor=followersIDs.getNextCursor();
				  
					} while (lCursor != 0);
					return new ResponseEntity<List<FriendsAndFollower>>(listFollowers, HttpStatus.OK);

				}
				//Almacenar Followers
				@RequestMapping(value="/api/AlmacenarFollowers", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,
						produces= MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<Followers> almacenarFollowers(@RequestParam(value="usuario", required=false)String usuario) throws IllegalStateException, TwitterException{
					if(usuario==null)usuario=tweet.getScreenName();
					ResponseEntity<List<FriendsAndFollower>> listFriends = this.verFriends(usuario);
					if(twitterFollowers == null){
						twitterFollowers = new HashMap<BigInteger, Followers>();
						nextIdFollowers = BigInteger.ONE;
					}
					Followers followers = new Followers();
					followers.setId(nextIdFollowers);
					followers.setListFriends(listFriends.getBody());
					followers.setName(usuario);
					nextIdFollowers=nextIdFollowers.add(BigInteger.ONE);
					twitterFollowers.put(followers.getId(), followers);
					return new ResponseEntity<Followers>(followers, HttpStatus.OK);
				}
				//Ver estados almacenados de mi Twitter o de otro Twitter
				@RequestMapping(value="/api/verFollowersAlmacenados", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<Collection<Followers>> getFollowers(){
					Collection<Followers> tweets = twitterFollowers.values();
					return new ResponseEntity<Collection<Followers>>(tweets, HttpStatus.OK);
				}
				//Ver follower de mi amigo o de otro
				@RequestMapping(value="/api/verFollowerAlmacenado", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<Followers> verFollowerAlmacenado(@RequestParam(value="id")BigInteger id) throws TwitterException{
					
					return new ResponseEntity<Followers>(twitterFollowers.get(id), HttpStatus.OK);

				}
		
				@RequestMapping(value="/api/borrarFollowerAlmacenados/{id}", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<String> borrarFollower(@PathVariable("id")BigInteger id) throws TwitterException{
					twitterFollowers.remove(id);
					//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
					return new ResponseEntity<String>("Borrado completado",HttpStatus.OK);
				}
				@RequestMapping(value="/api/borrarFollowerAlmacenado", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<String> borrarFollowerAlmacenado(@RequestParam(value="id")BigInteger id, @RequestParam(value="idUser")Long idUser) throws TwitterException{
					
					Followers tweets = twitterFollowers.get(id);
					twitterFollowers.remove(id);
					List<FriendsAndFollower> tweetFollower = new ArrayList<FriendsAndFollower>();
					for(FriendsAndFollower tw: tweets.getListFollower()){
						
						if(tw.getIdUser()!=idUser){tweetFollower.add(tw);}
					}
					tweets.setListFriends(tweetFollower);
					twitterFollowers.put(tweets.getId(), tweets);
					//if(!delete)return new ResponseEntity<Twitter>(HttpStatus.INTERNAL_SERVER_ERROR);
					return new ResponseEntity<String>("Borrado completado",HttpStatus.OK);
				}
				
				
				
				
			
				private static TwitterSentimientos save(TwitterSentimientos twitterSentimientos){
				
					try {
						List<String> listaTwitter = TwitterClass.buscarTwitter(twitterSentimientos.getName(), tweet);
						List<String> listaPositivos = new ArrayList<String>();
						List<String> listaNegativos = new ArrayList<String>();
						List<String> listaNeutros = new ArrayList<String>();
						for(String cadena: listaTwitter){
							int valorTweet=0;
							try {
								valorTweet = TwitterClass.funcionSentimientos(cadena);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(valorTweet>0)listaPositivos.add(cadena);
							else if(valorTweet<0)listaNegativos.add(cadena);
							else listaNeutros.add(cadena);
						}
						twitterSentimientos.setListaTwitterPositivos(listaPositivos);
						twitterSentimientos.setListaTwitterNegativos(listaNegativos);
						twitterSentimientos.setListaTwitterNeutros(listaNeutros);
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(twitterMap == null){
						twitterMap = new HashMap<BigInteger, TwitterSentimientos>();
						nextId = BigInteger.ONE;
					}
					//Si actualizamos
					if(twitterSentimientos.getId()!=null){
						TwitterSentimientos oldEmployee = twitterMap.get(twitterSentimientos.getId());
						if(oldEmployee==null)return null;
						twitterMap.remove(twitterSentimientos.getId());
						twitterMap.put(twitterSentimientos.getId(), twitterSentimientos);
						return twitterSentimientos;
					}
					twitterSentimientos.setId(nextId);
					nextId=nextId.add(BigInteger.ONE);
					twitterMap.put(twitterSentimientos.getId(), twitterSentimientos);
					return twitterSentimientos;
				}


				@RequestMapping(value="/api/mostrarEstudioSentimientos", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<Collection<TwitterSentimientos>> getEstudioSentimientos(){
					Collection<TwitterSentimientos> twitterSentimientos = twitterMap.values();
					return new ResponseEntity<Collection<TwitterSentimientos>>(twitterSentimientos, HttpStatus.OK);
				}
				@RequestMapping(value="/api/mostrarEstudioSentimientos/{id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<TwitterSentimientos> getEstudioSentimientos(@PathVariable("id") BigInteger id){
					TwitterSentimientos twitterSentimientos= twitterMap.get(id);
					if(twitterSentimientos==null){
						return new ResponseEntity<TwitterSentimientos>(HttpStatus.NOT_FOUND);}
					return new ResponseEntity<TwitterSentimientos>(twitterSentimientos, HttpStatus.OK);
				}
				
				@RequestMapping(value="/api/crearEstudioSentimientos", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,
						produces= MediaType.APPLICATION_JSON_VALUE, headers ={ "Accept=application/json","Content-Type=application/json" 
					    })
				public ResponseEntity<TwitterSentimientos> createEstudioSentimientos(@RequestParam(value="usuario", required=false)String usuario ) throws IllegalStateException, TwitterException{
					TwitterSentimientos twitterEstudioSentimientos = new TwitterSentimientos();
					if(usuario==null)usuario=tweet.getScreenName();
					twitterEstudioSentimientos.setName(usuario);
					TwitterSentimientos savedTwitter = save(twitterEstudioSentimientos);
					return new ResponseEntity<TwitterSentimientos>(savedTwitter, HttpStatus.CREATED);
				}

				
				@RequestMapping(value="/api/actualizarEstudio/{id}", method=RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<TwitterSentimientos> updateEstudio(@PathVariable("id")BigInteger id){
					TwitterSentimientos estudio = twitterMap.get(id);
					TwitterSentimientos updateEstudio = save(estudio);
					if(updateEstudio==null)return new ResponseEntity<TwitterSentimientos>(HttpStatus.INTERNAL_SERVER_ERROR);
					return new ResponseEntity<TwitterSentimientos>(updateEstudio,HttpStatus.OK);
				}
				
				private static boolean delete(BigInteger id){
					TwitterSentimientos deleteEmployee = twitterMap.remove(id);
					if(deleteEmployee==null)return false;
					return true;
				}
				
				@RequestMapping(value="/api/eliminarEstudioSentimientos", method=RequestMethod.DELETE, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<TwitterSentimientos> deleteEmployee(@RequestParam("id")BigInteger id){
					boolean delete = delete(id);
					if(!delete)return new ResponseEntity<TwitterSentimientos>(HttpStatus.INTERNAL_SERVER_ERROR);
					return new ResponseEntity<TwitterSentimientos>(HttpStatus.NO_CONTENT);
				}
				@RequestMapping(value="/api/mostrarUltimosTweet", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
				public ResponseEntity<List<String>> getMostrarUltimosTweet(@RequestParam(value="count")Integer count) throws TwitterException{
					
					List<String> listaComentarios = new ArrayList<String>();
				//Recuperar listado de ultimos tweets escritos por tus seguidores
			    Paging pagina = new Paging();
			    pagina.setCount(count);
			    ResponseList<Status> listado = tweet.getHomeTimeline(pagina);
			    for (int i = 0; i < listado.size(); i++) {
			    	String comentario=""+listado.get(i).getUser()
						    +" "+((Status) listado.get(i)).getText();
			    listaComentarios.add(comentario);
			    
			    }
			    return new ResponseEntity<List<String>>(listaComentarios, HttpStatus.OK);
				}
				
				
				
		
		
		
}
