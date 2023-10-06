package Utils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQLConnection extends IDBConnection {

    private String serverName;
    private String user;
    private String password;

    public MySQLConnection(String sv, String user, String password, String db) {
        this.db = db;
        this.serverName = sv;
        this.user = user;
        this.password = password;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + sv + "/?serverTimezone=UTC", user, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnReopen() {
        try {
            if (connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + serverName + "/?serverTimezone=UTC", user, password);
            }
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, ColumnInfoDTO> getColumnData(String tableName) {

        Map<String, ColumnInfoDTO> map = new HashMap<>();
        ResultSet rs2 = null;

        try {

            st = getConnReopen().createStatement();
            DatabaseMetaData meta = connection.getMetaData();
            rs = meta.getColumns(null, null, tableName, null);

            ColumnInfoDTO newColumn;

            while (rs.next()) {
                newColumn = new ColumnInfoDTO();
                newColumn.setName(rs.getString("COLUMN_NAME"));
                newColumn.setDataType(rs.getInt("DATA_TYPE"));
                newColumn.setColumnSize(rs.getInt("COLUMN_SIZE"));

                if (rs.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES")) {
                    newColumn.setNullable(false);
                    newColumn.setPK(true);
                    newColumn.setAutoIncrement(true);
                } else {
                    newColumn.setPK(false);
                    newColumn.setAutoIncrement(false);
                    newColumn.setNullable(rs.getString("IS_NULLABLE").equalsIgnoreCase("YES"));
                }

                map.put(newColumn.getName(), newColumn);
            }
            closeRs(rs);


            // Searches the metadata for non-autoincrement primary keys, turning the already added plain columns.
            rs = meta.getPrimaryKeys(db, null, tableName);
            while (rs.next()) {
                if ((newColumn = map.get(rs.getString("COLUMN_NAME"))) != null) {
                    newColumn.setPK(true);
                    newColumn.setNullable(false);
                }
            }
            closeRs(rs);

            // Searches the metadata for foreign keys, turning the already added plain columns.
            //  For each, it gets the on_delete and on_update rules, the referred table and referred primary key.
            rs = meta.getImportedKeys(db, null, tableName);
            while (rs.next()) {
                newColumn = map.get(rs.getString("FKCOLUMN_NAME"));
                newColumn.setFK(true);
                newColumn.setReferencedTable(rs.getString("PKTABLE_NAME"));
                newColumn.setReferencedPrimaryKey(rs.getString("PKCOLUMN_NAME"));
                newColumn.setOnDelete(
                        switch (rs.getString("DELETE_RULE")) {
                            case "importedKeyCascade" -> "CASCADE";
                            case "importedKeySetNull" -> "SET NULL";
                            case "importedKeySetDefault" -> "SET DEFAULT";
                            default -> "RESTRICT";
                        });

                newColumn.setOnUpdate(
                        switch (rs.getString("UPDATE_RULE")) {
                            case "importedKeyCascade" -> "CASCADE";
                            case "importedKeySetNull" -> "SET NULL";
                            case "importedKeySetDefault" -> "SET DEFAULT";
                            default -> "RESTRICT";
                        });
            }
            closeRs(rs);


        } catch (SQLException sqlException) {
            throw new RuntimeException("Exception during column info extraction for table " + tableName);
        } finally {
            closeRs(rs);
            closeSt(st);
        }

        return map;
    }

    @Override
    public int countRows(String tableName) {
        return super.countRows(db + "." + tableName);
    }

}
