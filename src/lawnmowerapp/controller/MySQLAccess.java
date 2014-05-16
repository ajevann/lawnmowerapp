package lawnmowerapp.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MySQLAccess {

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet result = null;
    
    private double COST = 0;

    public MySQLAccess(double cost) {
        COST = cost;
        try {
            createConnection();
            // statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // resultSet gets the result of the SQL query
            result = statement.executeQuery("select * from lawn_mower.customers");

            updateTechnicianJobs();

        } catch (Exception ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void addTechnician(String fname, String lname) {
        try {
            preparedStatement = connect
                    .prepareStatement("insert into lawn_mower.technicians(first_name, last_name) values (?, ?)");
            
            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, lname);
            
            executeUpdate(preparedStatement);
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void addCustomer(String fname, String lname, String address, Calendar signup, Calendar nextServiceDate, int tId) {
        try {
            preparedStatement = connect
                    .prepareStatement("insert into lawn_mower.customers(first_name, last_name, address, ori_signup, service_date, paid, technician_id) values (?, ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS);

            java.util.Date a = signup.getTime();
            java.util.Date b = nextServiceDate.getTime();

            java.sql.Date o = new java.sql.Date(a.getTime());
            java.sql.Date n = new java.sql.Date(b.getTime());

            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, lname);
            preparedStatement.setString(3, address);
            preparedStatement.setDate(4, o);
            preparedStatement.setDate(5, n);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, tId);

            executeUpdate(preparedStatement);

            //update technician job number
            preparedStatement = connect
                    .prepareStatement("update lawn_mower.technicians set num_of_jobs=num_of_jobs+1 where id=?");

            preparedStatement.setInt(1, tId);

            executeUpdate(preparedStatement);

        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected int getOwed(String lastName, String firstName) {
        int owed = -1;
        String statement = "SELECT count(*) FROM lawn_mower.customers where last_name = \'" + lastName + "\' and first_name = \'" + firstName + "\' and paid = 0 and completed = 1";

        try {
            ResultSet r = executeQuery(statement);
            while (r.next()) {
                owed = r.getInt("count(*)");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

        return owed;
    }

    protected int getLeastBusyTechnician() {
        int id = -1;
        try {
            ResultSet r = executeQuery("select id from technicians where num_of_jobs = (select min(num_of_jobs) from technicians) limit 1");
            while (r.next()) {
                id = r.getInt("id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

        return id;
    }

    protected ResultSet getJobsByCustomer(String lastName, String firstName) {
        return executeQuery("select * from lawn_mower.customers where first_name = \'" + firstName + "\' and last_name = \'" + lastName + "\'");
    }

    protected ResultSet getTechnicians() {
        return executeQuery("select * from lawn_mower.technicians");
    }

    protected ResultSet getCustomers() {
        return executeQuery("select * from lawn_mower.customers");
    }

    protected ResultSet getCustomer(int custId) {
        return executeQuery("select * from lawn_mower.customers where id = " + custId);
    }

    protected String getTechnicianName(int techId) {
        String name = "";

        try {
            ResultSet r = executeQuery("select first_name, last_name FROM lawn_mower.technicians where id=" + techId);
            while (r.next()) {
                name += r.getString("last_name");
                name += ", ";
                name += r.getString("first_name");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

        return name;
    }

    protected void updateTechnicianJobs() {
        try {
            ResultSet r = executeQuery("select id from technicians");
            while (r.next()) {
                int id = r.getInt("id");
                ResultSet e = executeQuery("select count(*) from customers where technician_id = " + id + " and paid = 0 and completed = 0");

                while (e.next()) {
                    int count = e.getInt("count(*)");
                    preparedStatement = connect
                            .prepareStatement("update lawn_mower.technicians set num_of_jobs=? where id=?");

                    preparedStatement.setInt(1, count);
                    preparedStatement.setInt(2, id);

                    executeUpdate(preparedStatement);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void updateCustomerRow(Object[] data) {
        double owes = ((Boolean)data[8] && !(Boolean)data[6]) ? COST : 0;
        
        try {
            preparedStatement
                    = connect.prepareStatement("update lawn_mower.customers set "
                            + "last_name = ?, "
                            + "first_name = ?, "
                            + "address = ?, "
                            + "ori_signup = ?, "
                            + "service_date = ?, "
                            + "paid = ?, "
                            + "technician_id = ?, "
                            + "completed = ? "
                            + "amount_owes = ? "
                            + "where id=?");

            preparedStatement.setString(1, (String) data[1]);
            preparedStatement.setString(2, (String) data[2]);
            preparedStatement.setString(3, (String) data[3]);
            preparedStatement.setDate(4, (java.sql.Date) data[4]);
            preparedStatement.setDate(5, (java.sql.Date) data[5]);
            preparedStatement.setInt(6, (Integer) data[6]);
            preparedStatement.setInt(7, (Integer) data[7]);
            preparedStatement.setInt(8, (Integer) data[8]);
            preparedStatement.setDouble(9, owes);
            preparedStatement.setInt(10, (Integer) data[0]);

            executeUpdate(preparedStatement);

        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private int executeUpdate(PreparedStatement preparedStatement) {
        try {
            preparedStatement.executeUpdate();
            return -1;

        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private ResultSet executeQuery(String s) {
        try {
            statement = connect.createStatement();
            return statement.executeQuery(s);
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void createConnection() throws Exception {
        try {
            // this will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // setup the connection with the DB.
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/lawn_mower?"
                            + "user=root&password=password");

        } catch (Exception e) {
            throw e;
        }
    }

    protected void close() {
        try {
            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) { }
    }

}
