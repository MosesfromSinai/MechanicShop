/* Mechanic Shop
 *
 * Database Management Systems
 * Department of Computer Science & Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class MechanicShop {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of MechanicShop
    *
    * @param dbname the name of the database
    * @param dbport the port of the database
    * @param user the user name used to login to the database
    * @param passwd the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public MechanicShop (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end MechanicShop

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
         if(outputHeader){
            for(int i = 1; i <= numCol; i++){
               System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this includes the <dbname> <port> <user>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            MechanicShop.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      MechanicShop esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the MechanicShop object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new MechanicShop (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Add Customer");
            System.out.println("2. Add Mechanic");
            System.out.println("3. Add Car");
            System.out.println("4. Initiate a Service Request");
            System.out.println("5. Close a Service Request");
            System.out.println("6. List date, comment, and bill for all closed requests with bill lower than 100");
            System.out.println("7. List first and last name of customers having more than 20 different cars");
            System.out.println("8. List Make, Model, and Year of all cars built before 1995 having less than 50000 miles");
            System.out.println("9. List the make, model and number of service requests for the first k cars with the highest number of service orders");
            System.out.println("10. List the first name, last name and total bill of customers in descending order of their total bill");
            System.out.println("11. < EXIT");

            switch (readChoice()){
               case 1: AddCustomer(esql); break;
               case 2: AddMechanic(esql); break;
               case 3: AddCar(esql); break;
               case 4: InitiateServiceRequest(esql); break;
               case 5: CloseServiceRequest(esql); break;
               case 6: ListClosedRequestsUnder100(esql); break;
               case 7: ListCustomersWithMoreThan20Cars(esql); break;
               case 8: ListCarsBefore1995Under50kMiles(esql); break;
               case 9: ListTopKServiceOrders(esql); break;
               case 10: ListCustomersByTotalBill(esql); break;
               case 11: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              Mechanic Shop Management System          \n" +
         "*******************************************************\n");
   }//end Greeting

   public static int readChoice() {
      int input;
      do {
         System.out.print("Please make your choice: ");
         try {
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   public static void AddCustomer(MechanicShop esql){
      try{
         String query = "SELECT MAX(id) FROM Customer";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(query);
         int newId = 1;
         if(rs.next()){
            newId = rs.getInt(1) + 1;
         }
         rs.close();
         stmt.close();

         System.out.print("\tEnter first name: ");
         String fname = in.readLine();
         if(fname.length() <= 0 || fname.length() > 40){
            System.out.println("Error: First name must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter last name: ");
         String lname = in.readLine();
         if(lname.length() <= 0 || lname.length() > 40){
            System.out.println("Error: Last name must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter phone number: ");
         String phone = in.readLine();
         if(phone.length() <= 0 || phone.length() > 40){
            System.out.println("Error: Phone number must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter address: ");
         String address = in.readLine();
         if(address.length() <= 0 || address.length() > 40){
            System.out.println("Error: Address must be between 1 and 40 characters.");
            return;
         }

         String insertQuery = "INSERT INTO Customer (id, fname, lname, phone, address) VALUES (" +
            newId + ", '" + fname + "', '" + lname + "', '" + phone + "', '" + address + "')";

         esql.executeUpdate(insertQuery);
         System.out.println("Customer successfully added! (ID: " + newId + ")");

      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }//end AddCustomer

   public static void AddMechanic(MechanicShop esql){
      try{
         String query = "SELECT MAX(id) FROM Mechanic";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(query);
         int newId = 1;
         if(rs.next()){
            newId = rs.getInt(1) + 1;
         }
         rs.close();
         stmt.close();

         System.out.print("\tEnter first name: ");
         String fname = in.readLine();
         if(fname.length() <= 0 || fname.length() > 40){
            System.out.println("Error: First name must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter last name: ");
         String lname = in.readLine();
         if(lname.length() <= 0 || lname.length() > 40){
            System.out.println("Error: Last name must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter years of experience: ");
         String expInput = in.readLine();
         int experience = Integer.parseInt(expInput);
         if(experience < 0){
            System.out.println("Error: Experience must be a non-negative integer.");
            return;
         }

         String insertQuery = "INSERT INTO Mechanic (id, fname, lname, experience) VALUES (" +
            newId + ", '" + fname + "', '" + lname + "', " + experience + ")";

         esql.executeUpdate(insertQuery);
         System.out.println("Mechanic successfully added! (ID: " + newId + ")");

      }catch(NumberFormatException e){
         System.out.println("Error: Experience must be a valid integer.");
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }//end AddMechanic

   public static void AddCar(MechanicShop esql){
      try{
         System.out.print("\tEnter VIN: ");
         String vin = in.readLine();
         if(vin.length() <= 0 || vin.length() > 40){
            System.out.println("Error: VIN must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter year: ");
         String yearInput = in.readLine();
         int year = Integer.parseInt(yearInput);
         if(year < 1900 || year > 2026){
            System.out.println("Error: Year must be between 1900 and 2026.");
            return;
         }

         System.out.print("\tEnter make: ");
         String make = in.readLine();
         if(make.length() <= 0 || make.length() > 40){
            System.out.println("Error: Make must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter model: ");
         String model = in.readLine();
         if(model.length() <= 0 || model.length() > 40){
            System.out.println("Error: Model must be between 1 and 40 characters.");
            return;
         }

         System.out.print("\tEnter customer ID (owner): ");
         String custInput = in.readLine();
         int customerId = Integer.parseInt(custInput);

         // verify customer exists
         String checkQuery = "SELECT id FROM Customer WHERE id = " + customerId;
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(checkQuery);
         if(!rs.next()){
            System.out.println("Error: Customer with ID " + customerId + " does not exist.");
            rs.close();
            stmt.close();
            return;
         }
         rs.close();
         stmt.close();

         String insertQuery = "INSERT INTO Car (vin, year, make, model, customer_id) VALUES ('" +
            vin + "', " + year + ", '" + make + "', '" + model + "', " + customerId + ")";

         esql.executeUpdate(insertQuery);
         System.out.println("Car successfully added! (VIN: " + vin + ")");

      }catch(NumberFormatException e){
         System.out.println("Error: Year and Customer ID must be valid integers.");
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
   }//end AddCar

   public static void InitiateServiceRequest(MechanicShop esql){
      try{
         // search for customer by last name
         System.out.print("\tEnter customer last name: ");
         String lname = in.readLine();

         String query = "SELECT id, fname, lname, phone FROM Customer WHERE lname = '" + lname + "'";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(query);

         // store matching customers
         java.util.List<Integer> custIds = new java.util.ArrayList<Integer>();
         java.util.List<String> custNames = new java.util.ArrayList<String>();
         while(rs.next()){
            custIds.add(rs.getInt(1));
            custNames.add(rs.getString(2).trim() + " " + rs.getString(3).trim() + " | Phone: " + rs.getString(4).trim());
         }
         rs.close();
         stmt.close();

         int customerId;

         if(custIds.size() == 0){
            System.out.println("No customer found with last name:" + lname);
         }
   }//end InitiateServiceRequest

   public static void CloseServiceRequest(MechanicShop esql){
      //TODO
   }//end CloseServiceRequest

   public static void ListClosedRequestsUnder100(MechanicShop esql){
      //TODO
   }//end ListClosedRequestsUnder100

   public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){
      //TODO
   }//end ListCustomersWithMoreThan20Cars

   public static void ListCarsBefore1995Under50kMiles(MechanicShop esql){
      //TODO
   }//end ListCarsBefore1995Under50kMiles

   public static void ListTopKServiceOrders(MechanicShop esql){
      //TODO
   }//end ListTopKServiceOrders

   public static void ListCustomersByTotalBill(MechanicShop esql){
      //TODO
   }//end ListCustomersByTotalBill

}//end MechanicShop