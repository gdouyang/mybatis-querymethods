package jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Address
{
	
	@Id
	@Column(name = "add_id")
	private long addId;
	
	private String name;
	
	public long getAddId()
	{
		return addId;
	}
	
	public void setAddId(long addId)
	{
		this.addId = addId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public Address(long addId, String name)
	{
		super();
		this.addId = addId;
		this.name = name;
	}
	
	public Address()
	{
	}
	
	/*
	 * @OneToOne
	 * 
	 * @PrimaryKeyJoinColumn(name="OWNER_ID", referencedColumnName="EMP_ID")
	 * private Employee owner;
	 * 
	 * public void setOwner(Employee owner) { this.owner = owner; this.ownerId =
	 * owner.getId(); }
	 */
}
