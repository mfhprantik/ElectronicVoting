/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricvoting;

import static electricvoting.Dashboard.email;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

/**
 *
 * @author Prantik
 */
public class SQL {

    private static Connection con;
    private static String className = "com.mysql.cj.jdbc.Driver";
    private static String dbh = "jdbc:mysql://localhost:3306/";
    private static String dbn = "evs";
    private static String dbu = "root";
    private static String dbpw = "";

    final static String MY_POLLS = "SELECT * from polls WHERE uemail = ";
    final static String MY_VOTES = "SELECT * from polls WHERE ";
    final static String SEARCH_POLLS = "Select * from polls where id = ";

    static ArrayList<Poll> getPolls(String pollQuery) {
        ArrayList<Poll> polls = new ArrayList<>();

        try {
            ResultSet rs = getStmt().executeQuery(pollQuery);

            while (rs.next()) {
                Poll p = new Poll(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4), rs.getBoolean(5), rs.getBoolean(6), rs.getBoolean(7), rs.getBoolean(8), rs.getString(9), rs.getBoolean(10), rs.getString(11), rs.getString(12));

                if (p.isFilter()) {
                    if (!p.getCreator().equals(email) && !p.getEmails().contains(email)) {
                        continue;
                    }
                }

                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery("SELECT * from options WHERE pid = " + p.getId());
                ArrayList<Option> options = new ArrayList<>();

                while (rs2.next()) {
                    Option o = new Option(rs2.getInt(1), rs2.getString(3), rs2.getInt(4));

                    Statement stmt3 = con.createStatement();
                    ResultSet rs3 = stmt3.executeQuery("SELECT count(*) from votes WHERE uemail = '" + email + "' and oid = " + o.getId());
                    int count = 0;

                    while (rs3.next()) {
                        count = rs3.getInt(1);
                    }

                    o.setVoted(count != 0);
                    options.add(o);
                }

                p.setOptions(options);
                polls.add(p);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return polls;
    }

    static StringBuilder getMyVotedPolls() {
        StringBuilder polls = new StringBuilder();

        try {
            ResultSet rs = getStmt().executeQuery("SELECT oid from votes where uemail = '" + email + "'");

            while (rs.next()) {
                int oid = rs.getInt(1);

                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery("SELECT pid from options where id = " + oid);

                while (rs2.next()) {
                    int pid = rs2.getInt(1);

                    if (polls.length() == 0) {
                        polls.append("id = " + pid);
                    } else {
                        polls.append(" or id = " + pid);
                    }
                }
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return polls;
    }

    static void getPoll(Poll p) {
        try {
            Statement stmt = getStmt();

            for (int i = 0; i < p.getOptions().size(); i++) {
                ResultSet rs = stmt.executeQuery("SELECT votes from options where id = " + p.getOptions().get(i).getId());
                while (rs.next()) {
                    p.getOptions().get(i).setVotes(rs.getInt(1));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String[] getProfile() {
        String[] info = new String[4];
        try {
            ResultSet rs = getStmt().executeQuery("SELECT * from users where email = '" + email + "'");

            while (rs.next()) {
                info[0] = rs.getString(1);
                info[1] = rs.getString(2);
                info[2] = rs.getString(3);
                info[3] = rs.getString(4);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }

    static void updateProfile(String fname, String lname, String email2, String password) {
        try {
            Statement stmt = getStmt();

            stmt.execute("UPDATE `users` SET fname = '" + fname + "', lname = '" + lname + "', email = '" + email2 + "', password = '" + password + "' WHERE email = '" + email + "'");
            if (!email2.equals(email)) {
                stmt.execute("UPDATE `votes` SET uemail = '" + email2 + "' WHERE uemail = '" + email + "'");
                stmt.execute("UPDATE `polls` SET uemail = '" + email2 + "' WHERE uemail = '" + email + "'");
            }

            con.close();
            email = email2;
            JOptionPane.showMessageDialog(new JFrame(), "Profile successfully updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void verifyAccount(JFrame window, String vcode) {
        try {
            String vcode2 = "";

            Statement stmt = getStmt();

            ResultSet rs = stmt.executeQuery("SELECT vcode from `users` WHERE email = '" + email + "'");
            while (rs.next()) {
                vcode2 = rs.getString(1);
            }

            if (vcode2.equals(vcode)) {
                stmt.execute("UPDATE users set verified = true WHERE email = '" + email + "'");

                JOptionPane.showMessageDialog(new JFrame(), "Your account was successfully verified.", "Success", JOptionPane.INFORMATION_MESSAGE);
                new Dashboard().setVisible(true);
                window.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Invalid verification code.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int getValidity(String email, String password) {
        int count = 0;
        try {
            ResultSet rs = getStmt().executeQuery("select verified from users where email = '" + email + "' and password = '" + password + "'");

            while (rs.next()) {
                count = 1;
                Dashboard.verified = rs.getBoolean(1);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    static void insertUser(String fname, String lname, String email, String password, String vcode) {
        try {
            getStmt().execute("INSERT INTO `users`(`fname`, `lname`, `email`, `password`, `vcode`) VALUES ('" + fname + "', '" + lname + "', '" + email + "', '" + password + "', '" + vcode + "')");
            con.close();

            new EmailSender(email, vcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void vote(JFrame window, JRadioButton[] ops, Poll p) {
        try {
            Statement stmt = getStmt();

            for (int i = 0; i < ops.length; i++) {
                boolean voted = p.getOptions().get(i).isVoted();

                if (ops[i].isSelected() && !voted) {
                    stmt.execute("INSERT INTO `votes`(`uemail`, `oid`) VALUES ('" + email + "', " + p.getOptions().get(i).getId() + ")");
                    stmt.execute("UPDATE `options` SET votes = votes + 1 where id = " + p.getOptions().get(i).getId());
                    p.getOptions().get(i).setVoted(true);
                } else if (!ops[i].isSelected() && voted) {
                    stmt.execute("DELETE from `votes` WHERE oid = " + p.getOptions().get(i).getId() + " and uemail = '" + email + "'");
                    stmt.execute("UPDATE `options` SET votes = votes - 1 where id = " + p.getOptions().get(i).getId());
                    p.getOptions().get(i).setVoted(false);
                }
            }

            con.close();

            JOptionPane.showMessageDialog(new JFrame(), "Voted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new ViewPoll(p).setVisible(true);
            window.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void insertPoll(JFrame window, String title, String[] options) {
        try {
            Statement stmt = getStmt();
            stmt.execute("INSERT INTO `polls`(`title`, `uemail`, `multiple`, `editable`, `canAdd`, `statsHidden`, `filter`, `emails`, `pProtection`, `password`) VALUES ('" + title + "','" + email + "'," + CreatePoll.multiple + "," + CreatePoll.editable + "," + CreatePoll.canAdd + "," + CreatePoll.statsHidden + "," + CreatePoll.emailf + ",'" + CreatePoll.emails + "'," + CreatePoll.pProtection + ",'" + CreatePoll.pw + "')");
            ResultSet rs = stmt.executeQuery("SELECT id from polls where title = '" + title + "' and uemail = '" + email + "' order by id desc");
            int pid = -1;

            while (rs.next()) {
                pid = rs.getInt(1);
                break;
            }

            for (int i = 0; i < options.length; i++) {
                stmt.execute("INSERT INTO `options`(`pid`, `name`, `votes`) VALUES (" + pid + ", '" + options[i] + "', 0)");
            }

            con.close();
            JOptionPane.showMessageDialog(new JFrame(), "Poll created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            new Dashboard().setVisible(true);
            window.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addOption(int pid, String option) {
        try {
            Statement stmt = getStmt();
            stmt.execute("INSERT INTO `options`(`pid`, `name`, `votes`) VALUES (" + pid + ",'" + option + "',0)");
            con.close();

            JOptionPane.showMessageDialog(new JFrame(), "Option added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void deletePoll(JFrame window, Poll p, String options) {
        try {
            Statement stmt = getStmt();

            stmt.execute("DELETE from polls WHERE id = " + p.getId());
            stmt.execute("DELETE from options WHERE pid = " + p.getId());
            stmt.execute("DELETE from votes WHERE " + options);

            con.close();

            JOptionPane.showMessageDialog(new JFrame(), "Poll deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new Dashboard().setVisible(true);
            window.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean getDuplicate(String email) {
        int count = 0;

        try {
            ResultSet rs = getStmt().executeQuery("select count(*) from users where email = '" + email + "'");

            while (rs.next()) {
                count = rs.getInt(1);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count != 0;
    }

    static void updateVcode(String vcode) {
        try {
            getStmt().execute("UPDATE `users` SET vcode = '" + vcode + "' WHERE email = '" + email + "'");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Statement getStmt() {
        try {
            Class.forName(className);
            con = DriverManager.getConnection(dbh + dbn, dbu, dbpw);
            Statement stmt = con.createStatement();
            return stmt;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
