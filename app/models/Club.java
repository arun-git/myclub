package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.CRUD.Hidden;

import siena.*;
import siena.core.Many;
import siena.core.Owned;

public class Club extends Model implements Serializable{

	@Id(Generator.AUTO_INCREMENT)
	public Long id;

	@Max(100) @NotNull
	public String name;

	@Owned
	public Many<Member> members;
	
	public Club() {
		super();
	}

	public String toString() {
		return name;
	}
	
	public static Query<Club> all() {
		return Model.all(Club.class);
		}

	public static Club findById(Long id) {
        return  all().filter("id", id).get();
    }
 
    
}
