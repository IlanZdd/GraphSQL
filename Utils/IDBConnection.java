package Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class IDBConnection {
    protected Connection connection;
    protected ResultSet rs;
    protected Statement st;
    protected String db;

    /** to get current connection **/
    public Connection getConn() {
        return connection;
    }

    /** reopen the connection */
    public abstract Connection getConnReopen();

    /** to close connection */
    public void closeConn() {
        try {
            if (connection != null){
                connection.close();
            }
        }
        catch (SQLException se) {
            System.out.println("Error: " + se.getMessage());
        }
    }

    /** to close statement */
    public static void closeSt(Statement st) {
        try {
            if(st != null) {
                st.close();
            }
        }catch (SQLException se) {
            System.out.println("Error: " + se.getMessage());
        }
    }

    /** to close resultSets */
    public static void closeRs(ResultSet rs) {
        try {
            if(rs != null) {
                rs.close();
            }
        }catch (SQLException se) {
            System.out.println("Error: " + se.getMessage());
        }
    }


    public abstract Map<String, ColumnInfoDTO> getColumnData(String tableName) ;

    public int countRows(String tableName) {
        try {
            String query = "SELECT count(*) FROM " + tableName;
            st = getConnReopen().createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                return rs.getInt(1);
            }

            return -1;
        } catch (SQLException sqlException) {
            throw new RuntimeException("Exception during record total extraction for table " + tableName);
        } finally {
            closeRs(rs);
            closeSt(st);
        }
    }

    public List<String> getTableNames() {
        List<String> table_names = new ArrayList<>();
        try {
            DatabaseMetaData meta = getConnReopen().getMetaData();
            String[] types = {"TABLE"};
            rs = meta.getTables(db, "dbo", "%", types);
            while (rs.next()) {
                table_names.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException se) {
            throw new RuntimeException(se);
        } finally {
            closeRs(rs);
        }

        return table_names;
    }
}
