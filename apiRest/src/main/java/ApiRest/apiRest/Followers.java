package ApiRest.apiRest;

import java.math.BigInteger;
import java.util.List;

import org.springframework.boot.orm.jpa.EntityScan;

@EntityScan
public class Followers  {

	private BigInteger id;
	private String name;
	private List<FriendsAndFollower> listFollower;
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public List<FriendsAndFollower> getListFollower() {
		return listFollower;
	}
	public void setListFriends(List<FriendsAndFollower> listFollower) {
		this.listFollower = listFollower;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
