package Utils;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLiteConnection extends IDBConnection {

    private SQLiteConfig config;

    public SQLiteConnection(String db) {
        this.db = db;
        this.config = new SQLiteConfig();
        config.enforceForeignKeys(true);  //this is needed to handle foreign keys

        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+db, config.toProperties());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnReopen() {
        try {
            if (connection.isClosed())
                this.connection = DriverManager.getConnection("jdbc:sqlite:"+db, config.toProperties());
            return connection;

        } catch (SQLException e) {
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

            // From the metadata, gets each column and its datatype, max length, and if it can be null;
            //  if it's autoincrement, it will deduce it's a primary key.
            while (rs.next()) {
                newColumn = new ColumnInfoDTO();
                newColumn.setName(rs.getString(4));
                newColumn.setDataType(rs.getInt(5));
                newColumn.setNullable(rs.getBoolean("IS_NULLABLE"));
                newColumn.setAutoIncrement(rs.getBoolean("IS_AUTOINCREMENT"));

                String query = "SELECT max(length(" + newColumn.getName() + ")) AS max_column_size FROM " + tableName;
                rs2 = st.executeQuery(query);
                int columnSize = -1;
                while (rs2.next())
                    columnSize = rs2.getInt(1);
                newColumn.setColumnSize(columnSize);

                if (newColumn.isAutoIncrement()) {
                    newColumn.setPK(true);
                }
                else
                    newColumn.setPK(false);

                map.put(newColumn.getName(), newColumn);
            }
            closeRs(rs);
            closeRs(rs2);

            // Queries for non-autoincrement primary keys, turning the already added plain columns;
            //  sets the nullability for those.
            String query =  "PRAGMA table_info(" + tableName + ")";
            rs = st.executeQuery(query);
            while (rs.next()) {
                newColumn = map.get(rs.getString(2));
                if (rs.getInt(6) > 0) {
                    newColumn.setPK(true);
                }
                if (rs.getInt(4) == 0) {
                    newColumn.setNullable(true);
                }
            }
            closeRs(rs);

            // Queries for foreign keys, turning the already added plain columns.
            //  For each, it gets the on_delete and on_update rules, the referred table and referred primary key.
            query = "PRAGMA foreign_key_list(" + tableName + ")";
            rs = st.executeQuery(query);
            while (rs.next()) {
                newColumn = map.get(rs.getString(4));

                newColumn.setFK(true);
                newColumn.setReferencedPrimaryKey(rs.getString(5));
                newColumn.setReferencedTable(rs.getString(3));
                newColumn.setOnDelete(rs.getString(7));
                newColumn.setOnUpdate(rs.getString(6));
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

}
