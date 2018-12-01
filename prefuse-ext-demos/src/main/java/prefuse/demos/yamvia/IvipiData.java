package prefuse.demos.yamvia;

import java.util.Vector;
import java.sql.*;

/**
 * Common data of Ivipi program
 */
public final class IvipiData 
{
	public static String [] choiceMainCat = {"ACTORS", "ACTRESS", "DIRECTORS", "CINEMATOGRAPHERS", "GENRES"};
	

	private String getSqlPerson (String pame)
	{
		Integer oidPerson = null;
		
		// Temporary
		String query_tmp = "SELECT oid FROM \"Person\" WHERE pname='"+pame+"'";
		try	{
			ResultSet rs_tmp = Ivipi.iSql.sql.executeQuery(query_tmp);
			rs_tmp.next();
			oidPerson = rs_tmp.getInt("oid");
		} catch (SQLException e) {
			System.out.println ("SELECT TMP "+e);
		}
		
		String query = "SELECT \"M\".oid FROM \"Movie\" \"M\", \"MPRInvolvement\" \"MPRI\" ";
		query += "WHERE \"MPRI\".oidPerson = "+oidPerson+" ";
		query += "AND \"MPRI\".oidmovie = \"M\".oid";
		
		return query;
	}
	
	private String getSqlGenre (String genreName)
	{
		Integer oid = null;
		
		// Temporary
		String query_tmp = "SELECT oid FROM \"Genre\" WHERE gname='"+genreName+"'";
		try
		{
			ResultSet rs_tmp = Ivipi.iSql.sql.executeQuery(query_tmp);
			rs_tmp.next();
			oid = rs_tmp.getInt("oid");
		}catch (SQLException e)
		{
			System.out.println ("SELECT TMP "+e);
		}
		
		String query = "SELECT \"GMI\".oidmovie FROM \"GMInvolvement\" \"GMI\" ";
		query += "WHERE \"GMI\".oidgenre = "+oid+" ";
		
		return query;
	}

	public String getQueryMovies (Vector<String> input)
	{
		String query = "";
		String mainWhereClause = constructMainWhere(input);
		
		query = "SELECT mname, mrating, myear ";
		query += ",(SELECT oidgenre FROM \"GMInvolvement\" WHERE oidmovie=\"M\".oid LIMIT 1) as oidgenre ";
		query += "FROM \"Movie\" \"M\" ";
		//query += "INNER JOIN \"GMInvolvement\" \"GM\" ON \"M\".oid = \"GM\".oidmovie ";
		if (mainWhereClause != "") 
			query += "WHERE \"M\".oid IN ("+mainWhereClause+") ";
		
		return query;
	}
	
	private String constructMainWhere (Vector<String> input)
	{
		String query = "";
		int i;

		for (i=0; i < input.size(); i++)
		{
			String entry = (String)input.get(i);
			
			if (entry.compareTo(new String("ACTORS"))==0)
			{
				if (input.size()<=i+2)
					break;
				
				if (query!="") query += "INTERSECT ";
				query += "("+getSqlPerson(input.get(i+2))+")";
				i+=2;
			}
			if (entry.compareTo(new String("DIRECTORS"))==0)
			{
				if (input.size()<=i+2)
					break;
				
				if (query!="") query += "INTERSECT ";
				query += "("+getSqlPerson(input.get(i+2))+")";
				i+=2;
			}
			if (entry.compareTo(new String("CINEMATOGRAPHER"))==0)
			{
				if (input.size()<=i+2)
					break;
				
				if (query!="") query += "INTERSECT ";
				query += "("+getSqlPerson(input.get(i+2))+")";
				i+=2;
			}
			else if (entry.compareTo(new String("ACTRESS"))==0)
			{
				if (input.size()<=i+2)
					break;
				
				if (query!="") query += "INTERSECT ";
				query += "("+getSqlPerson(input.get(i+2))+")";
				i+=2;
			}
			else if (entry.compareTo(new String("GENRES")) == 0)
			{
				if (input.size()<=i+1)
					break;
				
				if (query!="") query += "INTERSECT ";
				query += "("+getSqlGenre(input.get(i+1))+")";
				i+=1;
			}
			else{
				System.out.println ("ENTRY NON DEFINIE");
			}
		}
		
		return query;
	}
	
	
	public Vector<String> getPersons (Vector<String> input, String role, String inputName, int top)
	{
		String mainWhereClause = constructMainWhere(input);
		String queryPersons = "";
		Vector<String> result = new Vector ();
		
		
		if (role == "actors")
		{
			queryPersons = queryPersons ("actor", inputName);
			queryPersons += "AND \"P\".gender = 'm' ";
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;
		}
		else if (role == "actress")
		{
			queryPersons = queryPersons ("actor", inputName);
			queryPersons += "AND \"P\".gender = 'f' ";
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;
		}
		else if (role == "directors")
		{
			queryPersons = queryPersons ("director", inputName);
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;
		}
		else if (role == "cinematographers")
		{
			queryPersons = queryPersons ("cinematographer", inputName);
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;
		}
			
		System.out.println (queryPersons);
		
		try
		{
			ResultSet rs_tmp = Ivipi.iSql.sql.executeQuery(queryPersons);
			while (rs_tmp.next())
			{
				result.add(rs_tmp.getString("pname"));
			}
		}catch (SQLException e)
		{
			System.out.println ("SELECT TMP "+e);
		}
		
		return result;
	}
	
	/*
	public Vector<String> getActors (Vector<String> input, char pgender, int top)
	{
		String mainWhereClause = constructMainWhere(input);
		String queryActors = "";
		
		if (mainWhereClause == "")
		{
			queryActors = "SELECT pname FROM \"Person\" LIMIT "+top;
		}
		else
		{
			queryActors = "Select pname FROM \"Person\" \"P\", \"MPRInvolvement\" \"MPRI\" ";
			queryActors += "WHERE \"P\".oid = \"MPRI\".oidperson ";
			queryActors += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryActors += "LIMIT "+top;
		}
			
		System.out.println (queryActors);
		
		Vector<String> result = new Vector ();
		
		try
		{
			ResultSet rs_tmp = Ivipi.iSql.sql.executeQuery(queryActors);
			while (rs_tmp.next())
			{
				result.add(rs_tmp.getString("pname"));
			}
		}catch (SQLException e)
		{
			System.out.println ("SELECT TMP "+e);
		}
		
		return result;
	}
	*/
	
	public String queryPersons (String role)
	{
		String query = "Select DISTINCT pname FROM \"Person\" \"P\", \"MPRInvolvement\" \"MPRI\", \"Role\" \"R\" ";
		query += "WHERE \"R\".rname = '"+role+"' ";
		query += "AND \"R\".oid = \"MPRI\".oidrole ";
		query += "AND \"P\".oid = \"MPRI\".oidperson ";
		return query;
	}	
	
	public String queryPersons (String role, String input)
	{
		String query = "Select DISTINCT pname FROM \"Person\" \"P\", \"MPRInvolvement\" \"MPRI\", \"Role\" \"R\" ";
		query += "WHERE \"R\".rname = '"+role+"' ";
		query += "AND \"R\".oid = \"MPRI\".oidrole ";
		query += "AND \"P\".oid = \"MPRI\".oidperson ";
		query += "AND LOWER(\"P\".pname) LIKE LOWER('%"+input+"%')";
		return query;
	}
	
	
	public Vector<String> getPersons (Vector<String> input, String role, int top)
	{
		String mainWhereClause = constructMainWhere(input);
		String queryPersons = "";
		Vector<String> result = new Vector ();
		
		// To slow -> Optimized
		if (role == "actors")
		{
			queryPersons = queryPersons ("actor");
			queryPersons += "AND \"P\".gender = 'm' ";
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;				
		}
		else if (role == "actress")
		{
			queryPersons = queryPersons ("actor");
			queryPersons += "AND \"P\".gender = 'f' ";
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;
		}
		else if (role == "directors")
		{
			queryPersons = queryPersons ("director");
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;
		}
		else if (role == "cinematographers")
		{
			queryPersons = queryPersons ("cinematographer");
			if (mainWhereClause != "") 
				queryPersons += "AND \"MPRI\".oidmovie IN ("+mainWhereClause+") ";
			queryPersons += "ORDER BY pname ";	
			queryPersons += "LIMIT "+top;
		}
		
			
		System.out.println ("QUERY PERSONS " +queryPersons);
		
		
		try
		{
			ResultSet rs_tmp = Ivipi.iSql.sql.executeQuery(queryPersons);
			while (rs_tmp.next())
			{
				result.add(rs_tmp.getString("pname"));
			}
		}catch (SQLException e)
		{
			System.out.println ("SELECT TMP "+e);
		}
		
		return result;
	}
	
	
	public Vector<String> getGenres (Vector<String> input)
	{
		String mainWhereClause = constructMainWhere(input);
		String queryGenre = "";
		Vector<String> result = new Vector ();
		
		
		queryGenre = "SELECT DISTINCT gname FROM \"Genre\" \"G\", \"Movie\" \"M\", \"GMInvolvement\" \"GMI\" ";
		queryGenre += "WHERE \"G\".oid = \"GMI\".oidgenre ";
		queryGenre += "AND \"M\".oid = \"GMI\".oidmovie ";
		if (mainWhereClause != "") 
			queryGenre += "AND \"M\".oid IN ("+mainWhereClause+") ";
		queryGenre += "ORDER BY gname ";
			
		System.out.println (queryGenre);
		
		
		try
		{
			ResultSet rs_tmp = Ivipi.iSql.sql.executeQuery(queryGenre);
			while (rs_tmp.next())
			{
				result.add(rs_tmp.getString("gname"));
			}
		}catch (SQLException e)
		{
			System.out.println ("SELECT TMP "+e);
		}
		
		return result;
	}

	
	
/*	public Vector<String> getAToZActors (Vector<String> input, char pgender)
	{
		String mainWhereClause = constructMainWhere(input);
		String queryActors = "";
		Vector<String> result = new Vector ();
		
		
		if (mainWhereClause == "")
		{
			queryActors = "SELECT DISTINCT UPPER(SUBSTRING(pname,1,1)) as aToZ FROM \"Person\" WHERE gender='"+pgender+"'";
		}
		else
		{
			queryActors = "Select DISTINCT UPPER(SUBSTRING(pname,1,1)) as aToZ FROM \"Person\" \"P\", \"MPRInvolvement\" \"MPRI\" ";
			queryActors += "WHERE \"P\".oid = \"MPRI\".oidperson ";
			queryActors += "AND \"MPRI\".oidmovie IN ("+constructMainWhere(input)+") ";
			queryActors += "AND gender='"+pgender+"'";
		}
			
		System.out.println (queryActors);
		
		try
		{
			ResultSet rs_tmp = Ivipi.iSql.sql.executeQuery(queryActors);
			while (rs_tmp.next())
			{
				result.add(rs_tmp.getString("aToZ"));
			}
		}catch (SQLException e)
		{
			System.out.println ("SELECT TMP "+e);
		}
		
		return result;
	}*/
}
