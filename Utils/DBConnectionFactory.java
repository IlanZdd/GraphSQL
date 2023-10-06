package Utils;

import java.sql.SQLException;

public abstract class DBConnectionFactory {

    //to connect to DBMS server
    //in case of sqlite, the jdbc connection is performed to the file.db/file.sqlite stored in path DB
    public static IDBConnection setConn(String DBMS, String sv, String user, String password, String DB) {
        switch (DBMS.toLowerCase()) {
            case "mysql" -> { return new MySQLConnection(sv, user, password, DB); }
            case "sqlite" -> { return new SQLiteConnection(DB); }
            default -> throw new RuntimeException("DBMS not yet supported.");
        }
    }
}
