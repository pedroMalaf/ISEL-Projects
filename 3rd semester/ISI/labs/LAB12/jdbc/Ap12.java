/** 
ISEL - DEETC
Introdução a Sistemas de Informação
MP,ND, 2014-2022
*/

package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Calendar;


interface DbWorker 
{
	void doWork();
}	
class App
{
	private enum Option
	{
		Unknown,
		Exit,
		ListDepartment,
		ListEmployee,
		ListManagerDepartment,
		RegisterDepartment
	}
	private static App __instance = null;
	private String __connectionString;
	private HashMap<Option,DbWorker> __dbMethods;
	private static final String SELECT_CMD = "select * from departament";
	
	private App()
	{
			__dbMethods = new HashMap<Option,DbWorker>();
			__dbMethods.put(Option.ListDepartment, new DbWorker() {public void doWork() {
				try { App.this.ListDepartment(); } catch (java.sql.SQLException e) { }	
			}});
			__dbMethods.put(Option.ListEmployee, new DbWorker() {public void doWork() {
				try { App.this.ListEmployee(); } catch (java.sql.SQLException e) { }	
			}});
			__dbMethods.put(Option.ListManagerDepartment, new DbWorker() {public void doWork() {
				try { App.this.ListManagerDepartment(); } catch (java.sql.SQLException e) { }
			}});
			__dbMethods.put(Option.RegisterDepartment, new DbWorker() {public void doWork() {
				try { App.this.RegisterDepartment(); } catch (java.sql.SQLException e) { 
					System.err.println("Got an exception!");
      				System.err.println(e.getMessage());
				}
			}});
		

	}
	public static App getInstance() 
	{
		if(__instance == null) 
		{
			__instance = new App();
		}
		return __instance;
	}

	private Option DisplayMenu()
	{ 
		Option option=Option.Unknown;
		try
		{
			System.out.println("Course management");
			System.out.println();
			System.out.println("1. Exit");
			System.out.println("2. List departaments");
			System.out.println("3. List employees who have a higher than average annual salary in your department");
			System.out.println("4. List employees who are department heads and have no dependents");
			System.out.println("5. Register a novel department");
			System.out.print(">");
			Scanner s = new Scanner(System.in);
			int result = s.nextInt();
			option = Option.values()[result];			
		}
		catch(RuntimeException ex)
		{
			//nothing to do. 
		}
		
		return option;
		
	}
	private final static void clearConsole() throws Exception
	{
	    for (int y = 0; y < 25; y++) //console is 80 columns and 25 lines
	    System.out.println("\n");

	}
	private void Login() throws java.sql.SQLException
	{

		Connection con = DriverManager.getConnection(getConnectionString());
		if(con != null)
			con.close(); 
		
	}
	public void Run() throws Exception
	{
		Login ();
		Option userInput = Option.Unknown;
		do
		{
			clearConsole();
			userInput = DisplayMenu();
			clearConsole();		  	
			try
			{		
				__dbMethods.get(userInput).doWork();		
				System.in.read();		
				
			}
			catch(NullPointerException ex)
			{
				//Nothing to do. The option was not a valid one. Read another.
			}
			
		}while(userInput!=Option.Exit);
	}

	public String getConnectionString() 
	{
		return __connectionString;			
	}
	public void setConnectionString(String s) 
	{
		__connectionString = s;
	}

	/**
		To implement from this point forward. Do not need to change the code above.
	-------------------------------------------------------------------------------		
		IMPORTANT:
	--- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW ---
	-------------------------------------------------------------------------------
	
	*/

	private void printResults(ResultSet rs) throws java.sql.SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int col_number = rsmd.getColumnCount();

		for (int i = 1; i <= col_number; i++) {
			System.out.print(rsmd.getColumnName(i));
			System.out.print("\t");
		}

		System.out.print("\n");

		int tabs_count = col_number * 2;
		int letters_count = col_number * 15;
		for (int i = 0; i < tabs_count + letters_count; i++) {
			System.out.print("-");
		}

		System.out.print("\n");

		while (rs.next()) {
		    for (int i = 1; i <= col_number; i++) {
		        String columnValue = rs.getString(i);
		        System.out.print(columnValue + "\t");
		    }
		    System.out.println("");
		}

		/*Result must be similar like:
		dname   		dnumber		mgrssn      mgrstartdate            
		-----------------------------------------------------
		Research		5  			333445555 	1988-05-22            
		Administration	4    		987654321	1995-01-01
	 */ 
	}
	private void ListDepartment() throws java.sql.SQLException
	{
		String query = "SELECT * FROM department;";
		Connection con = DriverManager.getConnection(getConnectionString());
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(query);
		printResults(rs);
      	st.close();
		con.close();

	}
	private void ListEmployee() throws java.sql.SQLException
	{
		String query = """
			select fname from employee e 
			where salary > (select avg(salary) from employee where dno = e.dno);
		"""; // TODO: query mal
		Connection con = DriverManager.getConnection(getConnectionString());
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(query);
		printResults(rs);
      	st.close();
		con.close();
	}
	private void ListManagerDepartment() throws java.sql.SQLException
	{
		String query = """
		select * from 
    	(
    		select * from employee e 
    		inner join department d 
    		on e.ssn = d.mgrssn
    	) as e 
		where e.superssn is NULL
		""";
		Connection con = DriverManager.getConnection(getConnectionString());
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(query);
		printResults(rs);
      	st.close();
		con.close();
	}
	private void RegisterDepartment() throws java.sql.SQLException
	{
		Connection con = DriverManager.getConnection(getConnectionString());
		String query = """
			insert into department (dname, dnumber, mgrssn, mgrstartdate)
			values (?, ?, ?, ?)
		""";

		Calendar calendar = Calendar.getInstance();
      	java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());


		PreparedStatement st = con.prepareStatement(query);

		// TODO: ler valores da consola
		// readln() e afins

		st.setString(1, "DepNovo");
		st.setInt(2, 10);
		st.setString (3, "123666123");
		st.setDate(4, date);

		st.execute();
		con.close();

		System.out.println("Dados inseridos!");
	}
	
}

public class Ap12
{
	public static void main(String[] args) throws SQLException,Exception
	{
		String url =  "jdbc:postgresql://10.62.73.73:5432/?user=mp38&password=mp38&ssl=false";
		App.getInstance().setConnectionString(url);
		App.getInstance().Run();
	}
}

/* -------------------------------------------------------------------------------- 
private class Connect {
	private java.sql.Connection con = null;
    private final String url = "jdbc:sqlserver://";
    private final String serverName = "localhost";
    private final String portNumber = "1433";
    private final String databaseName = "aula12";
    private final String userName = "xxxx";
    private final String password = "xxxxxxx";

    // Constructor
    public Connect() {
    }

    private java.sql.Connection getConnection() {
        try {
            con = java.sql.DriverManager.getConnection(url, user, pwd);
            if (con != null) {
                System.out.println("Connection Successful!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Trace in getConnection() : " + e.getMessage());
        }
        return con;
    }

    private void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
            con = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
 --------------------------------------------------------------------------------
 */