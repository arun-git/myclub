package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	redirect("/admin");
    }
    
    public static void members(String term){
    	System.out.println("inside members fetch == > "+term);
    	
    	String searchTerm = term+"*";
    	System.out.println("search term is == > "+searchTerm);
    	
    	List<Member> members = Member.all().search(searchTerm, "firstName").fetch();
    	// .filter("upper(firstName) like upper(?)", term + "%").fetch();
    	
    	System.out.println("inside members fetch after query fetch :" + members);
    	
    	renderJSON(members);
    	
    }

}