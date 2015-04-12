package ca.jonsimpson.docker.model;

public class Tag {
	
	private String repositoryName;
	private String tagName;
	private String hash;
	
	public Tag(String repositoryName, String tagName, String hash) {
		this.repositoryName = repositoryName;
		this.tagName = tagName;
		this.hash = hash;
	}
	
	@Override
	public String toString() {
		return "Tag for repository[" + repositoryName + "] tagName[" + tagName
				+ "] hash[" + hash + "]";
	}
	
}
