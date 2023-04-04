package mybatis.join;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_account")
public class Account {

    @Id
    private Long id;

    private String userName;

    private Date birthday;

    private int sex;

    private Integer age;

    private boolean isNormal;

    private Boolean isDelete;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

	@Override
	public String toString() {
		return "Account [id=" + id + ", userName=" + userName + ", birthday=" + birthday + ", sex=" + sex + ", age="
				+ age + ", isNormal=" + isNormal + ", isDelete=" + isDelete + "]";
	}
}
