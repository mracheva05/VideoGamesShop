import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainForm {

    Connection conn = null;

    public MainForm() {
        btnAddClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isClientInfoValid()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");

                } else {
                    String sql = "INSERT INTO CLIENTS (name, phone, country) VALUES(?,?,?)";

                    try {
                        executeClientQuery(sql);
                        JOptionPane.showMessageDialog(null, "Client added successfully");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error adding client " + ex.getMessage());
                    }
                }
            }
        });
        btnDeleteClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (isClientInfoValid()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");

                } else {
                    String sql = "DELETE FROM CLIENTS WHERE name = ? AND phone = ? AND country = ?";

                    try {
                        executeClientQuery(sql);
                        JOptionPane.showMessageDialog(null, "Client removed successfully");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting client " + ex.getMessage());
                    }
                }


            }
        });
        //Will be implemented after a field where update data can be written is implemented into the GUI
        btnUpdateClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnAddGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameValid()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");

                } else {
                    String sql = "INSERT INTO GAMES (title, price, genre) VALUES(?,?,?)";

                    try {
                        executeGameQuery(sql);
                        JOptionPane.showMessageDialog(null, "Game added successfully");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error adding game " + ex.getMessage());
                    }
                }


            }
        });
        btnDeleteGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGameValid()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");

                } else {
                    String sql = "DELETE FROM GAMES WHERE title = ? AND price = ? AND genre = ?";

                    try {
                        executeGameQuery(sql);
                        JOptionPane.showMessageDialog(null, "Game deleted successfully");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting game " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void executeGameQuery(String sql) throws SQLException {
        conn = DatabaseConnection.getConnection();

        System.out.println("EIRWBIGPGHR "+conn .getMetaData().getURL());
        PreparedStatement prepStatement = conn.prepareStatement(sql);
        prepStatement.setString(1,textGameTitle.getText());
        prepStatement.setString(2,txtGamePrice.getText());
        prepStatement.setString(3,txtGameGenre.getText());
        prepStatement.executeUpdate();
        conn.commit();

        prepStatement.close();
        conn.close();
    }

    private boolean isGameValid() {
        return textGameTitle.getText().equals("")
                || txtGamePrice.getText().equals("")
                || txtGameGenre.getText().equals("");
    }

    private boolean isClientInfoValid() {
        return textClientName.getText().equals("")
                || textClientPhone.getText().equals("")
                || textClientCountry.getText().equals("");
    }

    private void executeClientQuery(String sql) throws SQLException {
        conn = DatabaseConnection.getConnection();

        System.out.println("EIRWBIGPGHR "+conn .getMetaData().getURL());
        PreparedStatement prepStatement = conn.prepareStatement(sql);
        prepStatement.setString(1,textClientName.getText());
        prepStatement.setString(2,textClientPhone.getText());
        prepStatement.setString(3,textClientCountry.getText());
        prepStatement.executeUpdate();
        conn.commit();

        prepStatement.close();
        conn.close();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Video Game Store");
        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTable tableClients;
    private JTextField textClientName;
    private JTextField textClientPhone;
    private JButton btnAddClient;
    private JButton btnDeleteClient;
    private JTable tableGames;
    private JTextField textGameTitle;
    private JTextField txtGamePrice;
    private JTextField txtGameGenre;
    private JButton btnAddGame;
    private JButton btnDeleteGame;
    private JTextField textClientCountry;
    private JComboBox comboSearchClient;
    private JComboBox comboSearchGame;
    private JButton btnSearch;
    private JTable tableSearch;
    private JTable tableOrders;
    private JComboBox comboOrderClient;
    private JComboBox comboOrderGame;
    private JButton btnDeleteOrder;
    private JButton btnAddOrder;
    private JButton btnUpdateClient;
    private JButton btnUpdateGames;
}