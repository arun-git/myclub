package controllers;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.csvreader.CsvReader;

import models.Club;
import models.Member;
import controllers.CRUD.For;
import play.Logger;
import play.data.Upload;
import play.modules.siena.SienaPlugin;

@For(models.Club.class)
public class Clubs extends controllers.CRUD {


	public static void addmember(){
		System.out.println("inside addmember");
		List members = Member.all().fetch();
		List clubs = Club.all().fetch();
		render(members, clubs);
	}

	public static void setmember(){
		String clubId = params.get("selectedClub");
		String memberFirstName = params.get("memb");
		Club club = Club.findById(new Long(clubId));
		Member member = Member.findByFirstName(memberFirstName);
		club.members.asList().add(member);
		SienaPlugin.pm().update(club);
		render();
	}

	/*public static void members(String term){
		List<Member> members = Member.all().filter("upper(fullName) like upper(?)", term + "%").fetch();
		renderJSON(members);
	}*/

	public static void loadmember(){
		List clubs = Club.all().fetch();
		render(clubs);
	}

	public static void setmembers(Upload data){
		String clubId = params.get("selectedClub");
		Club club = Club.findById(new Long(clubId));
		
		Logger.info(data.getContentType());
    	Logger.info(data.getFieldName());
    	Logger.info(data.getFileName());
		
    	try {
			CsvReader members = new CsvReader(data.asStream(),Charset.forName("UTF-8"));
			members.readHeaders();
			while (members.readRecord())
			{
				String firstName = members.get(0);
				String lastName = members.get(1);
				String joinedDate = members.get(2);
		
				Member member = new Member();
				member.firstName = firstName;
				member.lastName = lastName;
				DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
				Date date = (Date)formatter.parse(joinedDate);
				member.joinedDate = date;
				SienaPlugin.pm().save(member);
				club.members.asList().add(member);		
				Logger.info(firstName + ":" + lastName + ":" + joinedDate);
			}
			members.close();
    	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e){
			e.printStackTrace();
		}
		SienaPlugin.pm().update(club);
		render();
	}
}
