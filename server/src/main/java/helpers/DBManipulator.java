package helpers;

import java.sql.*;

public class DBManipulator {
    private final String JDBC_DRIVER = "org.postgresql.Driver";
    private Connection connection;
    private final String CREATE_TEBLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS users(id serial PRIMARY KEY, username varchar(80), pswd varchar(4096), salt varchar(4096), lastFile varchar(80) DEFAULT NULL)";

    private String url;
    private String user;
    private String password;
    String currUser;
    boolean isConnected = false;
    public DBManipulator(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        connectToDataBase();
    }
    private void connectToDataBase() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(url, user, password);
            connection.createStatement().executeUpdate(CREATE_TEBLE_IF_NOT_EXISTS);

            isConnected = true;
            //Outputer.println("Соединение с базой данных установлено.");
            //Main.logger.info("Соединение с базой данных установлено.");
        } catch (SQLException exception) {
            isConnected = false;
            //Outputer.printerror("Произошла ошибка при подключении к базе данных!");
            exception.printStackTrace();
            //Main.logger.error("Произошла ошибка при подключении к базе данных!");
        } catch (ClassNotFoundException exception) {
            isConnected = false;
            //Outputer.printerror("Драйвер управления базой дынных не найден!");
            //Main.logger.error("Драйвер управления базой дынных не найден!");
        }
    }
    public boolean getIsConnected(){
        return this.isConnected;
    }
    public boolean createTable(String req) throws SQLException {
        connection.createStatement().executeUpdate(req);
        return true;
    }

    public void setCurrUser(String currUser){
        this.currUser = currUser;
    }
    public String getCurrUser(){
        return currUser;
    }

    public PreparedStatement getPreparedStatement(String sqlStatement, boolean generateKeys) throws SQLException {
        PreparedStatement preparedStatement;
        try {
            if (connection == null) throw new SQLException();
            int autoGeneratedKeys = generateKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
            preparedStatement = connection.prepareStatement(sqlStatement, autoGeneratedKeys);
            //Main.logger.info("Подготовлен SQL запрос '" + sqlStatement + "'.");
            return preparedStatement;
        } catch (SQLException exception) {
            //Main.logger.error("Произошла ошибка при подготовке SQL запроса '" + sqlStatement + "'.");
            if (connection == null)
                ;
                //Main.logger.error("Соединение с базой данных не установлено!");
            throw new SQLException(exception);
        }
    }

    public void closePreparedStatement(PreparedStatement sqlStatement) {
        if (sqlStatement == null) return;
        try {
            sqlStatement.close();
            //Main.logger.info("Закрыт SQL запрос '" + sqlStatement + "'.");
        } catch (SQLException exception) {
            //Main.logger.error("Произошла ошибка при закрытии SQL запроса '" + sqlStatement + "'.");
        }
    }

    /**
     * Close connection to database.
     */
    public void closeConnection() {
        if (connection == null) return;
        try {
            connection.close();
            //Outputer.println("Соединение с базой данных разорвано.");
            //Main.logger.info("Соединение с базой данных разорвано.");
        } catch (SQLException exception) {
            //Outputer.printerror("Произошла ошибка при разрыве соединения с базой данных!");
            //Main.logger.error("Произошла ошибка при разрыве соединения с базой данных!");
        }
    }

    /**
     * Set commit mode of database.
     */
    public void setCommitMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            //Main.logger.error("Произошла ошибка при установлении режима транзакции базы данных!");
        }
    }

    /**
     * Set normal mode of database.
     */
    public void setNormalMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(true);
        } catch (SQLException exception) {
            //Main.logger.error("Произошла ошибка при установлении нормального режима базы данных!");
        }
    }

    /**
     * Commit database status.
     */
    public void commit() {
        try {
            if (connection == null) throw new SQLException();
            connection.commit();
        } catch (SQLException exception) {
            //Main.logger.error("Произошла ошибка при подтверждении нового состояния базы данных!");
        }
    }

    /**
     * Roll back database status.
     */
    public void rollback() {
        try {
            if (connection == null) throw new SQLException();
            connection.rollback();
        } catch (SQLException exception) {
            //Main.logger.error("Произошла ошибка при возврате исходного состояния базы данных!");
        }
    }

    /**
     * Set save point of database.
     */
    public void setSavepoint() {
        try {
            if (connection == null) throw new SQLException();
            connection.setSavepoint();
        } catch (SQLException exception) {
            //Main.logger.error("Произошла ошибка при сохранении состояния базы данных!");
        }
    }
}
