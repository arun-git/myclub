package controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import models.Club;
import models.Member;
import play.Logger;
import play.modules.siena.SienaPlugin;
import controllers.CRUD.For;

@For(models.Member.class)
public class Members extends controllers.CRUD {


	public static void search() throws CorruptIndexException, IOException, ParseException{
		String result="";
		String term = params.get("searchTerm");
		Logger.info("inside search members with term =: "+term);

		RAMDirectory directory = new RAMDirectory();
		Analyzer analyzer =  new StandardAnalyzer(Version.LUCENE_34);
		
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, analyzer);
		IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
		
		//IndexWriter writer = new IndexWriter(directory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

		List<Member> members = Member.all().fetch();
			if(members !=null){
				Logger.info("size of members to be loaded :::"+members.size());
			for(Member member: members){
				Document doc = new Document(); 
				String nameValue = member.firstName +" "+member.lastName;
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String joinedDate = formatter.format(member.joinedDate);
				doc.add(new Field("name", nameValue, Field.Store.YES, Field.Index.NOT_ANALYZED));   
				doc.add(new Field("joined", joinedDate, Field.Store.YES, Field.Index.NOT_ANALYZED)); 
				writer.addDocument(doc);
			}
			writer.optimize();
			writer.close();
			}else{
				Logger.info("no member found to load");
			}

			if(term!=null && !term.isEmpty()){
				//manupulating the search term with wildcards
				term = term.replace(' ', '*');
				term = term+"*";

				Logger.info("final term "+term);

				IndexSearcher searcher = new IndexSearcher(directory);
				String[] fieldsArray = {"name","joined"};

				MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_34, fieldsArray, analyzer);
				parser.setLowercaseExpandedTerms(false);
				Query query = parser.parse(term);

				Logger.info("Query :::"+query.toString());

				TopDocs rs = searcher.search(query, null, 10);
				if(rs !=null && rs.scoreDocs.length>0){

					Logger.info("total hits :"+rs.totalHits);
					Document firstHit = searcher.doc(rs.scoreDocs[0].doc);
					result = firstHit.getFieldable("name").stringValue();
					Logger.info("First result :::"+result);
				}else{
					Logger.info("Result is empty");
				}
			}
		render(result);
	}
}