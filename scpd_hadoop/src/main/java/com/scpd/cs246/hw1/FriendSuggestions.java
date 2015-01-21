package com.scpd.cs246.hw1;

public class FriendSuggestions {
	
	private Integer mutualFriend;
	
	private Integer potentialFriend;

	
	
	public FriendSuggestions(Integer mutualFriend, Integer potentialFriend) {
		super();
		this.mutualFriend = mutualFriend;
		this.potentialFriend = potentialFriend;
	}


	public Integer getMutualFriend() {
		return mutualFriend;
	}

	
	public void setMutualFriend(Integer mutualFriend) {
		this.mutualFriend = mutualFriend;
	}

	public Integer getPotentialFriend() {
		return potentialFriend;
	}

	public void setPotentialFriend(Integer potentialFriend) {
		this.potentialFriend = potentialFriend;
	}

	@Override
	public String toString() {
		return "User [mutualFriend=" + mutualFriend + ", potentialFriend="
				+ potentialFriend + "]";
	}

	
}
