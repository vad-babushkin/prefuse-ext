//package prefuse.demos.yamvia;
//
//import infovis07contest.data.*;
//import java.sql.*;
//import java.util.List;
//import java.util.Map;
//import java.util.HashMap;
//
//
//public class XmlImport
//{
//    public static IvipiSql iSql = null;
//    public static final String xmlFileName = "ressources/moviedb.xml";
//    public HashMap<String,Integer> dico_roles = new HashMap();
//
//
//    /**
//     * Flush all the table
//     * @return true if success, false else
//     */
//    public boolean flushTable ()
//    {
//    	// Clean database
//		String queryCleanMovie = "DELETE FROM \"Movie\"";
//		String queryCleanPerson = "DELETE FROM \"Person\"";
//		String queryCleanRole = "DELETE FROM \"Role\"";
//		String queryCleanGMInvolvement = "DELETE FROM \"GMInvolvement\"";
//		String queryCleanMPRInvolvement = "DELETE FROM \"MPRInvolvement\"";
//
//		try
//		{
//			System.out.println ("CLEAN TABLE Movie");
//			iSql.sql.execute(queryCleanMovie);
//			System.out.println ("CLEAN TABLE Person");
//			iSql.sql.execute(queryCleanPerson);
//			System.out.println ("CLEAN TABLE Role");
//			iSql.sql.execute(queryCleanRole);
//			System.out.println ("CLEAN TABLE GMInvolvement");
//			iSql.sql.execute(queryCleanGMInvolvement);
//			System.out.println ("CLEAN TABLE MPRInvolvement");
//			iSql.sql.execute(queryCleanMPRInvolvement);
//		}
//		catch (SQLException e)
//		{
//			System.out.println (e);
//			//return false;
//		}
//
//		return true;
//    }
//
//
//    /**
//     * Insert roles
//     * @return true if success, false else
//     */
//    public boolean insertRole ()
//    {
//    	try
//		{
//    		String queryGetOidRole = "SELECT oid FROM \"Role\" WHERE rname = ";
//    		Integer oidRole = null;
//
//			iSql.sql.execute("INSERT INTO \"Role\" (rname) VALUES ('director')");
//			ResultSet rsRoleDirector = iSql.sql.executeQuery(queryGetOidRole+"'director'");
//			rsRoleDirector.next();
//			oidRole = rsRoleDirector.getInt("oid");
//			this.dico_roles.put(new String("director"), oidRole);
//
//			iSql.sql.execute("INSERT INTO \"Role\" (rname) VALUES ('actor')");
//			ResultSet rsRoleActor = iSql.sql.executeQuery(queryGetOidRole+"'actor'");
//			rsRoleActor.next();
//			oidRole = rsRoleActor.getInt("oid");
//			this.dico_roles.put(new String("actor"), oidRole);
//
//			iSql.sql.execute("INSERT INTO \"Role\" (rname) VALUES ('cinematographer')");
//			ResultSet rsRole = iSql.sql.executeQuery(queryGetOidRole+"'cinematographer'");
//			rsRole.next();
//			oidRole = rsRole.getInt("oid");
//			this.dico_roles.put(new String("cinematographer"), oidRole);
//		}
//		catch (SQLException e)
//		{
//			System.out.println (e);
//			//return false;
//		}
//
//		return true;
//    }
//
//
//    /**
//     * Insert perons
//     * @return true, or false if fail
//     */
//    public boolean insertPerson ()
//    {
//    	Object[] keyValuePerson = MovieDB.instance.persons.entrySet().toArray();
//		int person_size = MovieDB.instance.persons.size();
//
//		Map.Entry entry;
//		String pname;
//		char gender = 'U';
//		Person person;
//		String query = "INSERT INTO \"Person\" (pname, gender) VALUES ";
//
//		for (int i = 0; i < person_size; i++)
//		{
//			entry = (Map.Entry) keyValuePerson[i];
//			pname = (String) entry.getKey();
//			person = (Person) entry.getValue();
//			gender = person.sex.toString().charAt(0);
//
//			try
//			{
//				iSql.sql.execute(query+"('"+pname.replace("'", "\\\'")+"', '"+gender+"')");
//			}
//			catch (SQLException e)
//			{
//				System.out.println ("INSERT PERSON : " + e);
//			}
//		}
//
//		return true;
//    }
//
//
//    /**
//     * Insert fucking movie
//     * @return
//     */
//    public boolean insertMovie ()
//    {
//    	Object[] keyValuePairs2 = MovieDB.instance.movies.entrySet().toArray();
//		int movie_size = MovieDB.instance.movies.size();
//
//		Map.Entry entry; // Movie represented in the object Movie
//		String title; // Title of the current movie
//		int year = 0;
//		float rating = 0;
//		List genres, actors, directors, cinematographers;
//		Movie movie;
//		int oidmovie = -1;
//
//
//		String queryInsertMovie = "INSERT INTO \"Movie\" (mname, myear, mrating) VALUES ";
//		String queryGetOidMovie = "SELECT MAX(oid) as max FROM \"Movie\""; // Get oid of this movie
//
//
//		// Object used for the research of Person
//		Person person; // Person current
//		Integer oidperson = null;
//
//
//		/* loop on the map that contains movies */
//		for (int i = 0; i < movie_size; i++)
//		{
//			entry = (Map.Entry) keyValuePairs2[i];
//			title = (String) entry.getKey();
//			movie = (Movie) entry.getValue();
//			year = movie.year;
//			genres = movie.genres;
//			actors = movie.actors;
//			directors = movie.directors;
//			cinematographers = movie.cinematographers;
//			rating = movie.imdbRating;
//
//			try
//			{
//				// Reccord Movie
//				iSql.sql.execute(queryInsertMovie+"('"+title.replace("'", "\\\'")+"', '"+year+"', '"+rating+"')");
//				// Get oid of this movie
//				ResultSet rsGetOidMovie = iSql.sql.executeQuery(queryGetOidMovie);
//				rsGetOidMovie.next();
//				oidmovie = rsGetOidMovie.getInt("max");
//			}
//			catch (SQLException e)
//			{
//				System.out.println ("INSERT MOVIE : " + e);
//			}
//
//			// On traite les genre
//			for (int genres_counter =0; genres_counter<genres.size(); genres_counter++)
//			{
//				String gname = genres.get(genres_counter).toString();
//				String queryGetOidGenre = "SELECT oid FROM \"Genre\" WHERE gname='"+gname+"'";
//				int oidgenre;
//
//				try
//				{
//					ResultSet rsGetGenre = iSql.sql.executeQuery(queryGetOidGenre);
//					if (rsGetGenre.next())
//						oidgenre = rsGetGenre.getInt("oid");
//					else
//					{
//						String queryInsertGenre = "INSERT INTO \"Genre\" (gname) VALUES ('"+gname.replace("'", "\\\'")+"')";
//						iSql.sql.execute(queryInsertGenre);
//						rsGetGenre = iSql.sql.executeQuery("SELECT MAX(oid) as max FROM \"Genre\"");
//						rsGetGenre.next();
//						oidgenre = rsGetGenre.getInt ("max");
//					}
//					String queryInsertInvolvGM = "INSERT INTO \"GMInvolvement\" (oidmovie, oidgenre) VALUES ("+ oidmovie +","+ oidgenre + ")";
//					iSql.sql.execute(queryInsertInvolvGM);
//				}
//				catch (SQLException e)
//				{
//					System.out.println ("INSERT GENRE : " + e);
//					//return false;
//				}
//			}
//
//
//			// Treat Actor
//			for (int actorsCounter = 0; actorsCounter<actors.size(); actorsCounter++)
//			{
//				person = (Person) actors.get(actorsCounter);
//				String queryGetOidPerson = "SELECT oid FROM \"Person\" WHERE pname = '"+person.name.replace("'", "\\\'")+"'";
//
//				try
//				{
//					ResultSet rsGetPerson = iSql.sql.executeQuery(queryGetOidPerson);
//					rsGetPerson.next();
//					oidperson = rsGetPerson.getInt("oid");
//
//					String queryInsertMPRInvolvement = "INSERT INTO \"MPRInvolvement\" (oidmovie, oidperson, oidrole) VALUES ("+oidmovie+","+oidperson.toString()+","+this.dico_roles.get(new String("actor"))+")";
//					iSql.sql.execute(queryInsertMPRInvolvement);
//				}
//				catch (SQLException e)
//				{
//					System.out.println ("INSERT ACTOR : " + e);
//				}
//			}
//
//			// Treat Director
//			for (int directorsCounter = 0; directorsCounter<directors.size(); directorsCounter++)
//			{
//				person = (Person) directors.get(directorsCounter);
//				String queryGetOidPerson = "SELECT oid FROM \"Person\" WHERE pname = '"+person.name.replace("'", "\\\'")+"'";
//
//				try
//				{
//					ResultSet rsGetPerson = iSql.sql.executeQuery(queryGetOidPerson);
//					rsGetPerson.next();
//					oidperson = rsGetPerson.getInt("oid");
//
//					String queryInsertMPRInvolvement = "INSERT INTO \"MPRInvolvement\" (oidmovie, oidperson, oidrole) VALUES ("+oidmovie+","+oidperson.toString()+","+this.dico_roles.get(new String("director"))+")";
//					iSql.sql.execute(queryInsertMPRInvolvement);
//				}
//				catch (SQLException e)
//				{
//					System.out.println ("INSERT DIRECTOR : " + e);
//				}
//			}
//
//			// Treat Cinematographer
//			for (int cinematographersCounter = 0; cinematographersCounter<cinematographers.size(); cinematographersCounter++)
//			{
//				person = (Person) cinematographers.get(cinematographersCounter);
//				String queryGetOidPerson = "SELECT oid FROM \"Person\" WHERE pname = '"+person.name.replace("'", "\\\'")+"'";
//				try
//				{
//					ResultSet rsGetPerson = iSql.sql.executeQuery(queryGetOidPerson);
//					rsGetPerson.next();
//					oidperson = rsGetPerson.getInt("oid");
//
//					String queryInsertMPRInvolvement = "INSERT INTO \"MPRInvolvement\" (oidmovie, oidperson, oidrole) VALUES ("+oidmovie+","+oidperson.toString()+","+this.dico_roles.get(new String("cinematographer"))+")";
//					iSql.sql.execute(queryInsertMPRInvolvement);
//				}
//				catch (SQLException e)
//				{
//					System.out.println ("INSERT CINEMATOGRAPHER : " + e);
//				}
//			}
//		}
//
//		return true;
//    }
//
//
//	public static void main(String[] args)
//	{
//		// This
//		XmlImport xml = new XmlImport ();
//
//		// Connection to the database
//    	try {
//			iSql = new IvipiSql();
//		}
//    	catch (Exception ex){
//			System.out.println("Exception: "+ex.getMessage());
//		}
//
//		MovieDB.readXMLFile(xmlFileName);
//
//		// flush table
//		xml.flushTable ();
//
//		// Treat Roles (STATIC)
//		System.out.println ("INSERT ROLES");
//		xml.insertRole ();
//
//		// Treat Persons
//		System.out.println ("INSERT PERSONS");
//		xml.insertPerson ();
//
//		// Treat Movie
//		System.out.println ("INSERT MOVIES");
//		xml.insertMovie ();
//	}
//
//}
