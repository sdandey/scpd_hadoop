package com.scpd.cs246.hw1;

import java.util.List;

public class UserIntersectionFriend implements Comparable<UserIntersectionFriend> {

	private String friendId;
	
	private List<String> mutualFriends;
	
	private List<String> potentialFriends;
	
	private Integer mutualFriendsCount;

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public List<String> getMutualFriends() {
		return mutualFriends;
	}

	public void setMutualFriends(List<String> mutualFriends) {
		this.mutualFriends = mutualFriends;
	}

	public List<String> getPotentialFriends() {
		return potentialFriends;
	}

	public void setPotentialFriends(List<String> potentialFriends) {
		this.potentialFriends = potentialFriends;
	}

	public Integer getMutualFriendsCount() {
		return mutualFriendsCount;
	}

	public void setMutualFriendsCount(Integer mutualFriendsCount) {
		this.mutualFriendsCount = mutualFriendsCount;
	}

	@Override
	public String toString() {
		return "friendId=" + friendId + "| mutualFriendsCount="+mutualFriendsCount
				+ "| mutualFriends=" + mutualFriends + "| potentialFriends="
				+ potentialFriends;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((friendId == null) ? 0 : friendId.hashCode());
		result = prime * result
				+ ((mutualFriends == null) ? 0 : mutualFriends.hashCode());
		result = prime
				* result
				+ ((mutualFriendsCount == null) ? 0 : mutualFriendsCount
						.hashCode());
		result = prime
				* result
				+ ((potentialFriends == null) ? 0 : potentialFriends.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserIntersectionFriend other = (UserIntersectionFriend) obj;
		if (friendId == null) {
			if (other.friendId != null)
				return false;
		} else if (!friendId.equals(other.friendId))
			return false;
		if (mutualFriends == null) {
			if (other.mutualFriends != null)
				return false;
		} else if (!mutualFriends.equals(other.mutualFriends))
			return false;
		if (mutualFriendsCount == null) {
			if (other.mutualFriendsCount != null)
				return false;
		} else if (!mutualFriendsCount.equals(other.mutualFriendsCount))
			return false;
		if (potentialFriends == null) {
			if (other.potentialFriends != null)
				return false;
		} else if (!potentialFriends.equals(other.potentialFriends))
			return false;
		return true;
	}

	public int compareTo(UserIntersectionFriend o) {
		return mutualFriendsCount - o.mutualFriendsCount;
	}

	
	
}
