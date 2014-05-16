/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lawnmowerapp.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class Controller {

    MySQLAccess m = null;
    java.sql.Date TODAY = null;
    final double COST = 45.0;

    public Controller() {
        TODAY = (new java.sql.Date(((new GregorianCalendar()).getTime()).getTime()));

        Calendar c = new GregorianCalendar(2014, 7, 3);
        //TODAY = (new java.sql.Date(c.getTime().getTime()));

        m = new MySQLAccess(COST);
        //RUNONLYONCE();
    }
    
    public void closeConnection()
    {
        m.close();
    }

    public void setDate(String date) {
        String[] da = date.split("-");

        int y = Integer.parseInt(da[0]);
        int m = Integer.parseInt(da[1]);
        int d = Integer.parseInt(da[2]);

        TODAY = new java.sql.Date((new GregorianCalendar(y, m - 1, d).getTime()).getTime());
    }

    public String getDate() {
        return TODAY.toString();
    }

    public void RUNONLYONCE() {
        setDate("2014-05-19"); addCustomer("Patrick","Swayze","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-16"); addCustomer("John","Philbin","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-15"); addCustomer("Bojesse","Christopher","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-12"); addCustomer("Keanu","Reeves","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-13"); addCustomer("Lee","Tergesen","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-15"); addCustomer("Gary", "Busey", "1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-10"); addCustomer("Gary", "Busey", "1235 Road St., Somewhere, USA, 23221");
        setDate("2014-05-15"); addCustomer("Gary", "Busey", "1236 Road St., Somewhere, USA, 23221");
        setDate("2014-05-16"); addCustomer("Julian","Reyes","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-14"); addCustomer("Lori","Petty","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-18"); addCustomer("Daniel","Beer","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-16"); addCustomer("John","McGinley","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-18"); addCustomer("James","LeGros","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-11"); addCustomer("Tom","Sizemore","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-17"); addCustomer("Peter","Phelps","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-19"); addCustomer("Vincent","Klyn","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-19"); addCustomer("Anthony","Kiedis","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-17"); addCustomer("Dave","Olson","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-12"); addCustomer("Christopher","Pettiet","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-14"); addCustomer("Sydney","Walsh","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-11"); addCustomer("Chris","Pedersen","1234 Road St., Somewhere, USA, 23221");
        setDate("2014-05-20");
    }

    public void addTechnician(String fname, String lname) {
        m.addTechnician(fname, lname);
    }
    
    public void addCustomer(String fname, String lname, String address) {
        int tId = m.getLeastBusyTechnician();

        Calendar c = new GregorianCalendar();
        c.setTime(TODAY);

        m.addCustomer(fname, lname, address, c, calcServiceDate(c), tId);
    }

    private void addNewService(Object[] customer) {
        //(String fname, String lname, String address, Calendar signup, Calendar nextServiceDate, int tId)

        Calendar oriSignup = new GregorianCalendar();
        oriSignup.setTime((java.sql.Date) customer[4]);

        Calendar serviceDate = new GregorianCalendar();
        serviceDate.setTime((java.sql.Date) customer[5]);

        m.addCustomer(
                (String) customer[2],
                (String) customer[1],
                (String) customer[3],
                oriSignup,
                serviceDate,
                (Integer) customer[7]);
    }

    private Calendar calcServiceDate(Calendar first) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(first.getTime());

        int year = cal.get(Calendar.YEAR);
        cal.add(Calendar.DATE, 14);

        int month = cal.get(Calendar.MONTH);
        boolean mod = false;

        switch (month) {
            case 0:
                mod = true;
                break;
            case 1:
                mod = true;
                break;
            case 2:
                mod = false;
                break;
            case 3:
                mod = false;
                break;
            case 4:
                mod = false;
                break;
            case 5:
                mod = false;
                break;
            case 6:
                mod = false;
                break;
            case 7:
                mod = false;
                break;
            case 8:
                mod = false;
                break;
            case 9:
                mod = false;
                break;
            case 10:
                mod = true;
                break;
            case 11:
                mod = true;
                break;
        }

        if (mod) {
            cal.set(Calendar.MONTH, 2);
            if (cal.get(Calendar.YEAR) == year + 2) {
                cal.set(Calendar.YEAR, year + 1);
            }
        }

        return cal;
    }

    private java.sql.Date calcServiceDate(java.sql.Date first) {
        Calendar a = new GregorianCalendar();
        a.setTime(first);

        Calendar b = calcServiceDate(a);
        java.util.Date c = b.getTime();
        java.sql.Date d = new java.sql.Date(c.getTime());

        return d;
    }

    public void printMonthlyBills() {
        ArrayList<Object[]> customers = getCustomers(false); //get unpaid
        ArrayList<Integer> visited = new ArrayList<Integer>();
        for (Object[] c : customers) {
            if (!visited.contains((Integer) c[0])) {
                String lastName = (String) c[1];
                String firstName = (String) c[2];
                String address = (String) c[3];

                double owed = getOwed(lastName, firstName) * COST;

                ArrayList<Object[]> jobsU = getJobsByCustomer(lastName, firstName);
                ArrayList<String> jobs = new ArrayList<String>();
                int count = 1;
                for (Object[] job : jobsU) {
                    jobs.add("\n"
                            + count++ + ".)   SERVICE DATE : " + job[4] + "\n"
                            + "      TECHNICIAN   : " + m.getTechnicianName((Integer) job[0]) + " [ " + job[0] + " ]\n"
                            + "      ADDRESS      : " + job[3] + "\n"
                            + "      PAID         : " + job[1] + "\n"
                            + "      COMPLETED    : " + job[2] + "\n\n");
                }

                IOClass.exportMonthlyBill(TODAY, lastName, firstName, owed, jobs);

                visited.add((Integer) c[0]);
            }
        }
    }

    private ArrayList<Object[]> getJobsByCustomer(String lastName, String firstName) {
        ArrayList<Object[]> jobs = new ArrayList<Object[]>();

        try {
            ResultSet results = m.getJobsByCustomer(lastName, firstName);
            while (results.next()) {
                int techId = results.getInt("technician_id");
                String paid = results.getInt("paid") == 1 ? "YES" : "NO";
                String completed = results.getInt("completed") == 1 ? "YES" : "NO";
                String address = results.getString("address");
                Date serviceDate = results.getDate("service_date");

                if ("NO".equals(paid) && "YES".equals(completed)
                        || (lastMonthsBill(serviceDate) && "YES".equals(completed))) {
                    jobs.add(new Object[]{
                        techId, paid, completed, address, serviceDate
                    });
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jobs;
    }

    private double getOwed(String lastName, String firstName) {
        return m.getOwed(lastName, firstName);
    }

    public void printWeeklyJobs() {
        String r = "Today's Date : " + TODAY + "\n\n\n";

        for (Object[] technician : getTechnicians()) {
            ArrayList<Object[]> customers = getWeeklyCustomers((Integer) technician[0]);
            ArrayList<String> jobs = new ArrayList<String>();

            if (customers.size() > 0) {
                int count = 1;
                for (Object[] customer : customers) {
                    jobs.add("\n"
                            + count++ + ".)   SERVICE DATE : " + customer[3] + "\n"
                            + "      CUSTOMER     : " + customer[0] + ", " + customer[1] + "  [ " + customer[4] + " ]\n"
                            + "      ADDRESS      : " + customer[2] + "\n\n");
                }
            } else {
                jobs.add("(no jobs today)\n");
            }

            IOClass.exportWeeklyJobs(TODAY, (String) technician[1], (String) technician[2], jobs);
        }
    }

    //Expects 4
    public ArrayList<Object[]> getTechnicians() {
        ArrayList<Object[]> technicians = new ArrayList<>();

        try {
            ResultSet results = m.getTechnicians();
            while (results.next()) {
                int id = results.getInt("id");
                String lname = results.getString("last_name");
                String fname = results.getString("first_name");
                int numOfJobs = results.getInt("num_of_jobs");

                technicians.add(new Object[]{
                    id, lname, fname, numOfJobs
                });
            }

        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return technicians;
    }

    public ArrayList<Object[]> getCustomers(boolean all) {
        ArrayList<Object[]> customers = new ArrayList<>();

        try {
            ResultSet results = m.getCustomers();
            while (results.next()) {
                int id = results.getInt("id");                          //0
                String lname = results.getString("last_name");          //1
                String fname = results.getString("first_name");         //2
                String address = results.getString("address");          //3
                Date oriSignup = results.getDate("ori_signup");         //4
                Date serviceDate = results.getDate("service_date");     //5
                boolean paid = results.getBoolean("paid");               //6
                int techId = results.getInt("technician_id");           //7
                boolean completed = results.getBoolean("completed");    //8

                if (all) {
                    customers.add(new Object[]{
                        id, lname, fname, address, oriSignup, serviceDate, paid, techId, completed
                    });
                } else if (!paid && completed) {
                    customers.add(new Object[]{
                        id, lname, fname, address, oriSignup, serviceDate, paid, techId, completed
                    });
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return customers;
    }

    private boolean lastMonthsBill(Date serviceDate) {
        Calendar t = Calendar.getInstance();
        t.setTime(TODAY);

        Calendar t30 = Calendar.getInstance();
        t30.setTime(TODAY);
        t30.add(Calendar.MONTH, -1);

        //date is before today
        System.out.println(serviceDate + "\t" + t.getTime());
        System.out.println("A " + serviceDate.before(t.getTime()));
        //date is after today(-30)
        System.out.println(serviceDate + "\t" + t30.getTime());
        System.out.println("B " + serviceDate.after(t30.getTime()));

        return (serviceDate.before(t.getTime()) && serviceDate.after(t30.getTime()));
    }

    public ArrayList<Object[]> getWeeklyCustomers(int techId) {
        ArrayList<Object[]> customers = new ArrayList<>();

        try {
            ResultSet results = m.getCustomers();
            while (results.next()) {
                int tech_id = results.getInt("technician_id");
                Date serviceDate = results.getDate("service_date");
                int completed = results.getInt("completed");

                if (tech_id == techId && jobForTheWeek(serviceDate) && completed == 0) {
                    String lname = results.getString("last_name");
                    String fname = results.getString("first_name");
                    String address = results.getString("address");
                    String custId = results.getInt("id") + "";

                    customers.add(new Object[]{lname, fname, address, serviceDate, custId});
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return customers;
    }

    private boolean jobForTheWeek(Date serviceDate) {
        Calendar sD = new GregorianCalendar();
        sD.setTime(serviceDate);

        Calendar today = new GregorianCalendar();
        today.setTime(TODAY);

        if (today.get(Calendar.YEAR) == sD.get(Calendar.YEAR)) {
            int tday = today.get(Calendar.DAY_OF_YEAR);
            int sday = sD.get(Calendar.DAY_OF_YEAR);
            int diff = sday - tday;

            return diff >= 0 && diff <= 6;
        }
        return false;
    }

    public void updateAll(String table, Vector<Vector> data) {
        if ("cust".equals(table)) {
            for (Vector row : data) {
                Object[] rowArray = row.toArray();

                rowArray[6] = ((Boolean) rowArray[6] == true) ? 1 : 0;
                rowArray[8] = ((Boolean) rowArray[8] == true) ? 1 : 0;

                if ((Integer) rowArray[8] == 1) {
                    checkNewJobNeeded((Integer) rowArray[0]);
                }

                m.updateCustomerRow(rowArray);
            }

            m.updateTechnicianJobs();
        }
    }

    private void checkNewJobNeeded(int custId) {
        System.out.println(custId);
        try {
            ResultSet results = m.getCustomer(custId);
            while (results.next()) {
                boolean completed = results.getBoolean("completed");

                if (completed == false) {
                    int id = results.getInt("id");
                    String lname = results.getString("last_name");
                    String fname = results.getString("first_name");         //2
                    String address = results.getString("address");          //3
                    Date oriSignup = results.getDate("ori_signup");         //4
                    Date serviceDate = calcServiceDate(TODAY);
                    boolean paid = results.getBoolean("paid");               //6
                    int techId = results.getInt("technician_id");           //7

                    addNewService(new Object[]{
                        id, lname, fname, address, oriSignup, serviceDate, paid, techId, completed
                    });
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
