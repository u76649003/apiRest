package ApiRest.apiRest;

import java.math.BigInteger;
import java.util.List;

import org.springframework.boot.orm.jpa.EntityScan;

@EntityScan
public class Friends {
	private BigInteger id;
	private String name;
	private List<FriendsAndFollower> listFriends;
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public List<FriendsAndFollower> getListFriends() {
		return listFriends;
	}
	public void setListFriends(List<FriendsAndFollower> listFriends) {
		this.listFriends = listFriends;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
