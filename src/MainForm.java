import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//TODO: Search feature (Why the fuck is it a combo box ????) :)
public class MainForm {

    Connection conn = null;

    public MainForm() {
        
        loadClientsIntoComboBox();
        loadGamesIntoComboBox();
        
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
        btnAddOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboOrderClient.getSelectedItem() == null || comboOrderGame.getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null, "Please select a client and a game");
                } else {
                    String sql = "INSERT INTO ORDERS (clientid, gameid, orderdate) VALUES(?,?,?)";
                    try {
                        conn = DatabaseConnection.getConnection();

                        int clientId = getClientIdByName(comboOrderClient.getSelectedItem().toString());
                        int gameId = getGameIdByTitle(comboOrderGame.getSelectedItem().toString());

                        PreparedStatement prepStatement = conn.prepareStatement(sql);
                        prepStatement.setInt(1, clientId);
                        prepStatement.setInt(2, gameId);
                        prepStatement.setDate(3, new Date(System.currentTimeMillis())); // current date
                        prepStatement.executeUpdate();
                        conn.commit();

                        prepStatement.close();
                        conn.close();

                        JOptionPane.showMessageDialog(null, "Order added successfully");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error" + ex.getMessage());
                    }
                }
            }
        });
        //Will be implemented when a field, which identifies order to be deleted, is added in the GUI
        btnDeleteOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private int getClientIdByName(String name) throws SQLException {
        String sql = "SELECT id FROM CLIENTS WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            java.sql.ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Client not found");
            }
        }
    }

    private int getGameIdByTitle(String title) throws SQLException {
        String sql = "SELECT id FROM GAMES WHERE title = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, title);
            java.sql.ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Game not found");
            }
        }
    }

    private void loadGamesIntoComboBox() {
        String sql = "SELECT title FROM GAMES";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement prepStatement = conn.prepareStatement(sql);
             java.sql.ResultSet resultSet = prepStatement.executeQuery()) {

            while (resultSet.next()) {
                String gameName = resultSet.getString("title");
                comboOrderGame.addItem(gameName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading clients: " + e.getMessage());
        }
    }

    private void loadClientsIntoComboBox() {
        String sql = "SELECT name FROM CLIENTS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement prepStatement = conn.prepareStatement(sql);
             java.sql.ResultSet resultSet = prepStatement.executeQuery()) {

            while (resultSet.next()) {
                String clientName = resultSet.getString("name");
                comboOrderClient.addItem(clientName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading clients: " + e.getMessage());
        }
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