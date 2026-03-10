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
      try {
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      } catch (Exception e) {
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
      while (rs.next()) {
         if(outputHeader) {
            for(int i = 1; i <= numCol; i++) {
               System.out.printf("%-30s", rsmd.getColumnName(i));
            }
            System.out.println();
            System.out.println("-".repeat(30 * numCol));
            outputHeader = false;
         }
         for (int i=1; i<=numCol; ++i)
            System.out.printf("%-30s", rs.getString(i).trim());
         System.out.println();
         ++rowCount;
      }
      stmt.close();
      return rowCount;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup() {
      try {
         if (this._connection != null) {
            this._connection.close ();
         }//end if
      } catch (SQLException e) {
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
      try {
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
            System.out.println();
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

            switch (readChoice()) {
            case 1:
               AddCustomer(esql);
               break;
            case 2:
               AddMechanic(esql);
               break;
            case 3:
               AddCar(esql);
               break;
            case 4:
               InitiateServiceRequest(esql);
               break;
            case 5:
               CloseServiceRequest(esql);
               break;
            case 6:
               ListClosedRequestsUnder100(esql);
               break;
            case 7:
               ListCustomersWithMoreThan20Cars(esql);
               break;
            case 8:
               ListCarsBefore1995Under50kMiles(esql);
               break;
            case 9:
               ListTopKServiceOrders(esql);
               break;
            case 10:
               ListCustomersByTotalBill(esql);
               break;
            case 11:
               keepon = false;
               break;
            default :
               System.out.println("Unrecognized choice!");
               break;
            }//end switch
         }//end while
      } catch(Exception e) {
         System.err.println (e.getMessage ());
      } finally {
         try {
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         } catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting() {
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
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      } while (true);
      return input;
   }//end readChoice

   public static boolean isValidName(String name) {
      if(name.length() <= 0 || name.length() > 40) return false;
      if(!name.matches("[a-zA-Z ]+")) return false;
      return true;
   }

   public static boolean isValidPhone(String phone) {
      if(phone.length() <= 0 || phone.length() > 40) return false;
      if(!phone.matches("[0-9\\-]+")) return false;
      return true;
   }

   public static boolean isValidDate(String date) {
      if(!date.matches("\\d{4}-\\d{2}-\\d{2}")) return false;
      return true;
   }

   public static void AddCustomer(MechanicShop esql) {
      try {
         String query = "SELECT MAX(id) FROM Customer";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(query);
         int newId = 1;
         if(rs.next()) {
            newId = rs.getInt(1) + 1;
         }
         rs.close();
         stmt.close();

         System.out.print("\tEnter first name: ");
         String fname = in.readLine();
         if(fname.length() <= 0 || fname.length() > 40) {
            System.out.println("Error: First name must be between 1 and 40 characters.");
            return;
         }

         if(!isValidName(fname)) {
            System.out.println("Error: Invalid first name.");
            return;
         }

         System.out.print("\tEnter last name: ");
         String lname = in.readLine();
         if(lname.length() <= 0 || lname.length() > 40) {
            System.out.println("Error: Last name must be between 1 and 40 characters.");
            return;
         }

         if(!isValidName(lname)) {
            System.out.println("Error: Invalid last name.");
            return;
         }

         System.out.print("\tEnter phone number: ");
         String phone = in.readLine();
         if(phone.length() <= 0 || phone.length() > 40) {
            System.out.println("Error: Phone number must be between 1 and 40 characters.");
            return;
         }

         if(!isValidPhone(phone)) {
            System.out.println("Error: Invalid phone number.");
            return;
         }

         System.out.print("\tEnter address: ");
         String address = in.readLine();
         if(address.length() <= 0 || address.length() > 40) {
            System.out.println("Error: Address must be between 1 and 40 characters.");
            return;
         }

         String insertQuery = "INSERT INTO Customer (id, fname, lname, phone, address) VALUES (" +
                              newId + ", '" + fname + "', '" + lname + "', '" + phone + "', '" + address + "')";

         esql.executeUpdate(insertQuery);
         System.out.println("Customer successfully added! (ID: " + newId + ")");

      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end AddCustomer

   public static void AddMechanic(MechanicShop esql) {
      try {
         String query = "SELECT MAX(id) FROM Mechanic";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(query);
         int newId = 1;
         if(rs.next()) {
            newId = rs.getInt(1) + 1;
         }
         rs.close();
         stmt.close();

         System.out.print("\tEnter first name: ");
         String fname = in.readLine();
         if(fname.length() <= 0 || fname.length() > 40) {
            System.out.println("Error: First name must be between 1 and 40 characters.");
            return;
         }

         if(!isValidName(fname)) {
            System.out.println("Error: Invalid first name.");
            return;
         }

         System.out.print("\tEnter last name: ");
         String lname = in.readLine();
         if(lname.length() <= 0 || lname.length() > 40) {
            System.out.println("Error: Last name must be between 1 and 40 characters.");
            return;
         }

         if(!isValidName(lname)) {
            System.out.println("Error: Invalid last name.");
            return;
         }

         System.out.print("\tEnter years of experience: ");
         String expInput = in.readLine();
         int experience = Integer.parseInt(expInput);
         if(experience < 0) {
            System.out.println("Error: Experience must be a non-negative integer.");
            return;
         }

         String insertQuery = "INSERT INTO Mechanic (id, fname, lname, experience) VALUES (" +
                              newId + ", '" + fname + "', '" + lname + "', " + experience + ")";

         esql.executeUpdate(insertQuery);
         System.out.println("Mechanic successfully added! (ID: " + newId + ")");

      } catch(NumberFormatException e) {
         System.out.println("Error: Experience must be a valid integer.");
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end AddMechanic

   public static void AddCar(MechanicShop esql) {
      try {
         System.out.print("\tEnter VIN: ");
         String vin = in.readLine();
         if(vin.length() <= 0 || vin.length() > 40) {
            System.out.println("Error: VIN must be between 1 and 40 characters.");
            return;
         }

         if(!vin.matches("[a-zA-Z0-9]+")) {
            System.out.println("Error: VIN must contain only letters and numbers.");
            return;
         }

         String checkVin = "SELECT vin FROM Car WHERE vin = '" + vin + "'";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(checkVin);
         if(rs.next()) {
            System.out.println("Error: A car with this VIN already exists.");
            rs.close();
            stmt.close();
            return;
         }
         rs.close();
         stmt.close();

         System.out.print("\tEnter year: ");
         String yearInput = in.readLine();
         int year = Integer.parseInt(yearInput);
         if(year < 1900 || year > 2026) {
            System.out.println("Error: Year must be between 1900 and 2026.");
            return;
         }

         System.out.print("\tEnter make: ");
         String make = in.readLine();
         if(make.length() <= 0 || make.length() > 40) {
            System.out.println("Error: Make must be between 1 and 40 characters.");
            return;
         }

         if(!make.matches("[a-zA-Z ]+")) {
            System.out.println("Error: Make must contain only letters.");
            return;
         }

         System.out.print("\tEnter model: ");
         String model = in.readLine();
         if(model.length() <= 0 || model.length() > 40) {
            System.out.println("Error: Model must be between 1 and 40 characters.");
            return;
         }

         if(!model.matches("[a-zA-Z0-9 \\-]+")) {
            System.out.println("Error: Model must contain only letters, numbers, and hyphens.");
            return;
         }

         System.out.print("\tEnter customer ID (owner): ");
         String custInput = in.readLine();
         int customerId = Integer.parseInt(custInput);

         // verify customer exists
         String checkQuery = "SELECT id FROM Customer WHERE id = " + customerId;
         stmt = esql._connection.createStatement();
         rs = stmt.executeQuery(checkQuery);
         if(!rs.next()) {
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

      } catch(NumberFormatException e) {
         System.out.println("Error: Year and Customer ID must be valid integers.");
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end AddCar

   public static void InitiateServiceRequest(MechanicShop esql) {
      try {
         System.out.print("\tEnter customer last name: ");
         String lname = in.readLine();

         // find customers with that last name
         String query = "SELECT id, fname, lname, phone FROM Customer WHERE lname = '" + lname + "'";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(query);

         // print matches and count them
         int count = 0;
         while(rs.next()) {
            System.out.println("\tID: " + rs.getInt(1) + " | " + rs.getString(2).trim() + " " + rs.getString(3).trim() + " | Phone: " + rs.getString(4).trim());
            count++;
         }
         rs.close();
         stmt.close();

         int customerId;

         if(count == 0) {
            System.out.println("No customer found with last name: " + lname);
            System.out.print("\tWould you like to add a new customer? (y/n): ");
            String choice = in.readLine();
            if(choice.equalsIgnoreCase("y")) {
               AddCustomer(esql);
            }
            return;
         } else {
            System.out.print("\tEnter the customer ID from above: ");
            customerId = Integer.parseInt(in.readLine());

            // verify the ID is valid
            String checkQuery = "SELECT id FROM Customer WHERE id = " + customerId;
            stmt = esql._connection.createStatement();
            rs = stmt.executeQuery(checkQuery);
            if(!rs.next()) {
               System.out.println("Invalid customer ID.");
               rs.close();
               stmt.close();
               return;
            }
            rs.close();
            stmt.close();
         }

         // list cars for this customer
         String carQuery = "SELECT vin, year, make, model FROM Car WHERE customer_id = " + customerId;
         stmt = esql._connection.createStatement();
         rs = stmt.executeQuery(carQuery);

         int carCount = 0;
         while(rs.next()) {
            System.out.println("\t" + rs.getString(1).trim() + " | " + rs.getInt(2) + " " + rs.getString(3).trim() + " " + rs.getString(4).trim());
            carCount++;
         }
         rs.close();
         stmt.close();

         String carVin;

         if(carCount == 0) {
            System.out.println("No cars found for this customer.");
            System.out.print("\tWould you like to add a new car? (y/n): ");
            String choice = in.readLine();
            if(choice.equalsIgnoreCase("y")) {
               AddCar(esql);
            }
            return;
         } else {
            System.out.print("\tEnter the VIN from above (or 'new' to add a car): ");
            String vinInput = in.readLine();
            if(vinInput.equalsIgnoreCase("new")) {
               AddCar(esql);
               return;
            }
            carVin = vinInput;
         }

         System.out.print("\tEnter date (YYYY-MM-DD): ");
         String date = in.readLine();

         if(!isValidDate(date)) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
         }

         System.out.print("\tEnter odometer reading: ");
         int odometer = Integer.parseInt(in.readLine());
         if(odometer < 0) {
            System.out.println("Odometer can't be negative.");
            return;
         }

         System.out.print("\tEnter complaint: ");
         String complain = in.readLine();
         if(complain.length() <= 0) {
            System.out.println("Complaint cannot be empty.");
            return;
         }

         // get next rid
         String ridQuery = "SELECT MAX(rid) FROM Service_Request";
         stmt = esql._connection.createStatement();
         rs = stmt.executeQuery(ridQuery);
         int newRid = 1;
         if(rs.next()) {
            newRid = rs.getInt(1) + 1;
         }
         rs.close();
         stmt.close();

         System.out.print("\tEnter mechanic ID: ");
         int mechanicId = Integer.parseInt(in.readLine());

         // check if mechanic exists
         String mechQuery = "SELECT id FROM Mechanic WHERE id = " + mechanicId;
         stmt = esql._connection.createStatement();
         rs = stmt.executeQuery(mechQuery);
         if(!rs.next()) {
            System.out.println("Mechanic not found.");
            rs.close();
            stmt.close();
            return;
         }
         rs.close();
         stmt.close();

         String insertQuery = "INSERT INTO Service_Request (rid, date, odometer, complain, car_vin, customer_id, mechanic_id) VALUES (" +
                              newRid + ", '" + date + "', " + odometer + ", '" + complain + "', '" + carVin + "', " + customerId + ", " + mechanicId + ")";

         esql.executeUpdate(insertQuery);
         System.out.println("Service request created! (RID: " + newRid + ")");

      } catch(NumberFormatException e) {
         System.out.println("Please enter a valid number.");
      } catch(Exception e) {
         System.err.println(e.getMessage());

      }
   }//end InitiateServiceRequest

   public static void CloseServiceRequest(MechanicShop esql) {
      try {
         System.out.print("Enter service request number: ");      //getting and validating service request
         int rid = Integer.parseInt(in.readLine());

         // check if service request exists and is open
         String checkQuery =
            "SELECT rid FROM Service_Request WHERE rid = " + rid + " AND close_date IS NULL";
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(checkQuery);
         if (!rs.next()) {
            System.out.println("Error: Service request does not exist or is already closed.");
            rs.close();
            stmt.close();
            return;
         }
         rs.close();
         stmt.close();

         System.out.print("Enter mechanic ID: ");
         int mechanicId = Integer.parseInt(in.readLine());

         //checking if mechanic exists
         String mechQuery = "SELECT id FROM Mechanic WHERE id = " + mechanicId;
         stmt = esql._connection.createStatement();
         rs = stmt.executeQuery(mechQuery);
         if (!rs.next()) {
            System.out.println("Error: Mechanic not found.");
            rs.close();
            stmt.close();
            return;
         }
         rs.close();
         stmt.close();

         System.out.print("Enter closing date (YYYY-MM-DD): ");
         String closeDate = in.readLine();

         if(!isValidDate(closeDate)) {
            System.out.println("Error: Invalid date format. Use YYYY-MM-DD.");
            return;
         }

         //checking closing date is after request date
         String dateQuery = "SELECT \"date\" FROM Service_Request WHERE rid = " + rid;
         stmt = esql._connection.createStatement();
         rs = stmt.executeQuery(dateQuery);
         rs.next();
         String requestDate = rs.getString(1);
         rs.close();
         stmt.close();

         if(closeDate.compareTo(requestDate) < 0) {
            System.out.println("Error: Closing date cannot be before the request date (" + requestDate.trim() + ").");
            return;
         }

         System.out.print("Comment: ");
         String comment = in.readLine();

         System.out.print("Enter bill amount: ");
         int bill = Integer.parseInt(in.readLine());
         if (bill < 0) {
            System.out.println("Error: Bill cannot be negative.");
            return;
         }

         String updateQuery = "UPDATE Service_Request SET close_date = '" + closeDate + "', comment = '" + comment + "', bill = " + bill + " WHERE rid = " + rid;
         esql.executeUpdate(updateQuery);
         System.out.println("Service request " + rid + " successfully closed!");

      } catch(NumberFormatException e) {
         System.out.println("Error: Please enter a valid number.");
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }

   }//end CloseServiceRequest

   public static void ListClosedRequestsUnder100(MechanicShop esql) {
      try {
         String query =
            "SELECT date, comment, bill " +
            "FROM Service_Request " +
            "WHERE close_date IS NOT NULL AND bill < 100";
         int rowCount = esql.executeQuery(query);
         System.out.println("Total rows: " + rowCount);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }//end ListClosedRequestsUnder100

   public static void ListCustomersWithMoreThan20Cars(MechanicShop esql) {
      try {
         String query =
            "SELECT C.fname, C.lname " +
            "FROM Customer C, Car CA " +
            "WHERE C.id = CA.customer_id " +
            "GROUP BY C.id, C.fname, C.lname " +
            "HAVING COUNT(CA.vin) > 20";
         int rowCount = esql.executeQuery(query);
         System.out.println("Total rows: " + rowCount);
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end ListCustomersWithMoreThan20Cars

   public static void ListCarsBefore1995Under50kMiles(MechanicShop esql) {
      try {
         String query =
            "SELECT DISTINCT C.make, C.model, C.year " +
            "FROM Car C, Service_Request SR " +
            "WHERE C.vin = SR.car_vin " +
            "AND C.year < 1995 " +
            "AND SR.odometer < 50000";
         int rowCount = esql.executeQuery(query);
         System.out.println("Total rows: " + rowCount);
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end ListCarsBefore1995Under50kMiles

   public static void ListTopKServiceOrders(MechanicShop esql) {
      try {
         System.out.print("Enter value of k: ");
         String kInput = in.readLine();
         int k = Integer.parseInt(kInput);                     //converts text input into actual number
         if (k <= 0) {
            System.out.println("Error: k must be a positive number.");
            return;
         }

         String query =
            "SELECT c.make, c.model, COUNT(sr.rid) AS num_requests " +
            "FROM Car c JOIN Service_Request sr ON c.vin = sr.car_vin " +
            "WHERE sr.close_date IS NULL GROUP BY c.vin, c.make, c.model ORDER BY num_requests DESC LIMIT " + k;
         int rowCount = esql.executeQuery(query);
         System.out.println("Total rows: " + rowCount);
      } catch(NumberFormatException e) {                                 //catches bad input like strings and throws error
         System.out.println("Error: Please enter a valid integer.");
      } catch(Exception e) {
         System.err.println(e.getMessage());                      //catches general errors
      }
   }//end ListTopKServiceOrders

   public static void ListCustomersByTotalBill(MechanicShop esql) {
      try {
         String query =
            "SELECT C.fname, C.lname, SUM(SR.bill) AS total_bill " +
            "FROM Customer C, Service_Request SR " +
            "WHERE C.id = SR.customer_id " +
            "AND SR.bill IS NOT NULL " +
            "GROUP BY C.id, C.fname, C.lname " +
            "ORDER BY total_bill DESC";
         int rowCount = esql.executeQuery(query);
         System.out.println("Total rows: " + rowCount);
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end ListCustomersByTotalBill

}//end MechanicShop