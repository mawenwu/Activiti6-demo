package com.models.user.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * UserInfo .
 *
 * @author Lingo
 */
@Entity
@Table(name="USER_INFO"
)
public class UserInfo  implements java.io.Serializable {

    private static final long serialVersionUID = 0L;

    /** null. */
    private Long id;
    /** null. */
    private String username;
    /** null. */
    private String password;
    /** null. */
  
    public UserInfo() {
    }

	
    public UserInfo(Long id) {
        this.id = id;
    }
    
    public UserInfo(Long id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}


	@Id 
    @Column(name="ID", unique=true, nullable=false)
    public Long getId() {
        return this.id;
    }
    /** @param id null. */
    public void setId(Long id) {
        this.id = id;
    }
    /** @return null. */
    @Column(name="USERNAME", length=50)
    public String getUsername() {
        return this.username;
    }
    /** @param username null. */
    public void setUsername(String username) {
        this.username = username;
    }
    /** @return null. */
    @Column(name="PASSWORD", length=50)
    public String getPassword() {
        return this.password;
    }
    /** @param password null. */
    public void setPassword(String password) {
        this.password = password;
    }
}