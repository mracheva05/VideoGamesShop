import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

//TODO: Search feature (Why the fuck is it a combo box ????) :)
public class MainForm {

    Connection conn = null;

    public MainForm() {

        //comboOrderClient.addItem("Select a client");

        loadComboBoxes();
        loadTables();

        clientOperations();

        gameOperations();

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
                        loadOrdersTable();
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

        btnDeleteOrder.addActionListener(e -> {
            int row = tableOrders.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Select a row first");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {

                String clientName = tableOrders.getValueAt(row, 0).toString();
                String gameTitle = tableOrders.getValueAt(row, 1).toString();
                java.sql.Date orderDate = java.sql.Date.valueOf(tableOrders.getValueAt(row, 2).toString());

                int clientId = getClientIdByName(clientName);
                int gameId = getGameIdByTitle(gameTitle);

                String sql = "DELETE FROM ORDERS WHERE clientid = ? AND gameid = ? AND orderdate = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, clientId);
                preparedStatement.setInt(2, gameId);
                preparedStatement.setDate(3, orderDate);

                preparedStatement.executeUpdate();
                conn.commit();

                loadOrdersTable(); // refresh table
                preparedStatement.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting order: " + ex.getMessage());
            }
        });

    }

    private void gameOperations() {
        btnUpdateGames.addActionListener(e -> {
            int row = tableGames.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Select a row first");
                return;
            }

            String oldTitle = tableGames.getValueAt(row, 0).toString();
            String oldPrice = tableGames.getValueAt(row, 1).toString();
            String oldGenre = tableGames.getValueAt(row, 2).toString();

            String sql = "UPDATE GAMES SET title=?, price=?, genre=? WHERE title=? AND price=? AND genre=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

                preparedStatement.setString(1, textGameTitle.getText());
                preparedStatement.setString(2, txtGamePrice.getText());
                preparedStatement.setString(3, txtGameGenre.getText());

                preparedStatement.setString(4, oldTitle);
                preparedStatement.setString(5, oldPrice);
                preparedStatement.setString(6, oldGenre);

                preparedStatement.executeUpdate();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Game updated successfully");

                loadGamesTable(); // refresh table
                loadGamesIntoComboBox();

            } catch (SQLException ex) {
                ex.printStackTrace();
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
                        loadGamesTable();
                        loadGamesIntoComboBox();
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
                        loadGamesTable();
                        loadGamesIntoComboBox();
                        JOptionPane.showMessageDialog(null, "Game deleted successfully");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting game " + ex.getMessage());
                    }
                }
            }
        });

        btnSearchGames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String input = textGamesSearch.getText().trim();
                String sql = "SELECT title, price, genre FROM GAMES " +
                        "WHERE title LIKE ? OR price LIKE ? OR genre LIKE ?";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

                    String searchValue = "%" + input + "%";

                    preparedStatement.setString(1, searchValue);
                    preparedStatement.setString(2, searchValue);
                    preparedStatement.setString(3, searchValue);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    TableModel model = new TableModel(resultSet);
                    tableGames.setModel(model);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnRefreshGamesTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGamesTable();
            }
        });
    }

    private void clientOperations() {
        btnAddClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isClientInfoValid()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");

                } else {
                    String sql = "INSERT INTO CLIENTS (name, phone, country) VALUES(?,?,?)";

                    try {
                        executeClientQuery(sql);
                        loadClientsTable();
                        loadClientsIntoComboBox();

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
                        loadClientsTable();
                        loadClientsIntoComboBox();

                        JOptionPane.showMessageDialog(null, "Client removed successfully");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error deleting client " + ex.getMessage());
                    }
                }


            }
        });

        btnUpdateClient.addActionListener(e -> {
            int row = tableClients.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Select a row first");
                return;
            }

            String oldName = tableClients.getValueAt(row, 0).toString();
            String oldPhone = tableClients.getValueAt(row, 1).toString();
            String oldCountry = tableClients.getValueAt(row, 2).toString();

            String sql = "UPDATE CLIENTS SET name=?, phone=?, country=? WHERE name=? AND phone=? AND country=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

                preparedStatement.setString(1, textClientName.getText());
                preparedStatement.setString(2, textClientPhone.getText());
                preparedStatement.setString(3, textClientCountry.getText());

                preparedStatement.setString(4, oldName);
                preparedStatement.setString(5, oldPhone);
                preparedStatement.setString(6, oldCountry);

                preparedStatement.executeUpdate();
                conn.commit();
                JOptionPane.showMessageDialog(null, "Client updated successfully");


                loadClientsTable(); // refresh table
                loadClientsIntoComboBox();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        btnSearchClients.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String input = textClientSearch.getText().trim();
                String sql = "SELECT name, phone, country FROM CLIENTS " +
                        "WHERE name LIKE ? OR phone LIKE ? OR country LIKE ?";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

                    String searchValue = "%" + input + "%";

                    preparedStatement.setString(1, searchValue);
                    preparedStatement.setString(2, searchValue);
                    preparedStatement.setString(3, searchValue);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    TableModel model = new TableModel(resultSet);
                    tableClients.setModel(model);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnRefreshClientTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadClientsTable();
            }
        });
    }

    private void loadComboBoxes() {
        loadClientsIntoComboBox();
        loadGamesIntoComboBox();
    }

    private void loadTables() {
        loadClientsTable();
        loadGamesTable();
        loadOrdersTable();
    }

    private void loadClientsTable() {
        String sql = "SELECT name,phone,country FROM CLIENTS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            TableModel model = new TableModel(resultSet);
            tableClients.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGamesTable() {
        String sql = "SELECT title, price, genre FROM GAMES";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            TableModel model = new TableModel(resultSet);
            tableGames.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrdersTable() {
        //String sql = "SELECT o.clientid, c.name, o.gameid, g.title, o.orderdate FROM ORDERS o JOIN CLIENTS c ON o.clientid = c.id JOIN GAMES g ON o.gameid = g.id";
        String sql = "SELECT c.name AS client_name, g.title AS game_title, o.orderdate FROM ORDERS o JOIN CLIENTS c ON o.clientid = c.id JOIN GAMES g ON o.gameid = g.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            TableModel model = new TableModel(resultSet);
            tableOrders.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        comboOrderGame.removeAllItems();
        comboOrderGame.addItem("Select a game");


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
        comboOrderClient.removeAllItems();
        comboOrderClient.addItem("Select a client");

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
    private JButton btnSearch;
    private JTable tableSearch;
    private JTable tableOrders;
    private JComboBox comboOrderClient;
    private JComboBox comboOrderGame;
    private JButton btnDeleteOrder;
    private JButton btnAddOrder;
    private JButton btnUpdateClient;
    private JButton btnUpdateGames;
    private JTextField textClientSearch;
    private JTextField textGamesSearch;
    private JTextField textClientAdvancedSearch;
    private JTextField textGameAdvancedSearch;
    private JButton btnSearchClients;
    private JButton btnSearchGames;
    private JButton btnRefreshClientTable;
    private JButton btnRefreshGamesTable;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}