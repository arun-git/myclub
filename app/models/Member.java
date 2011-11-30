package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.CRUD.Hidden;

import siena.*;

public class Member extends Model implements Serializable {

	@Id(Generator.AUTO_INCREMENT)
	public Long id;

	@Hidden
	public Club club;
	
	@Max(100) @NotNull
	public String firstName;

	@Max(100) @NotNull
	public String lastName;
	
	@NotNull
	public Date joinedDate;
	

	public String formattedDate() {
		return new SimpleDateFormat("dd/MM/yyyy").format(joinedDate);
	}

	public Member() {
		super();
	}

	public String toString() {
		return firstName +" "+ lastName;
	}
	
	public static Query<Member> all() {
		return Model.all(Member.class);
		}

	public static Member findByFirstName(String name) {
        return all().filter("firstName", name).get();
    }

}
