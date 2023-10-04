package Graph;

import Utils.DBConnection;
import Graph.Visualize.Visualize;

import java.sql.*;
import java.util.*;

public class Graph {
    /**
     * Nodes can be of following types:
     * <br> Unknown, if setTypes() was never called.
     * <br> Source, if the node has no entering arcs but has at least one exiting arc.
     * <br> Mid_node, if the node has at least one entering arc and one exiting arc.
     * <br> Well, if the node has no exiting arcs but has at least one entering arc.
     * <br> External, if the node has no relationships to other nodes.
     */
    public enum nodeType {source, mid_node, well, external_node, unknown}


    /** Name of the Database the graph is built upon.  */
    private final String name;
    /** List of nodes, or tables, in the database. */
    private final List<Node> tables;
    /** A map containing arcs that create cycles between tables; the table name is used as key,
     * and the value is a list of all problematic arcs for that table. */
    private final Map<String, TreeSet<ForeignKeyColumn>> problematicArcs = new HashMap<>();

    /** Creates a temporary connection to the database, using the given values.
     * For each table, it extracts the Primary Keys, Foreign Keys and other columns, each with datatype, column size,
     * and if nullable to create an oriented graph. Closes the connection to the database afterwards.
     * @param DBMS        DBMS [Supported: MySQL, SQLite]
     * @param server_name Where to access the database [e.g. 'localhost:3306']
     * @param user        Username to access the database [e.g 'root']
     * @param password    Password to access the database
     * @param name        Schema and graph name
     * @throws SQLException If connection cannot be established
     */
    public Graph(String DBMS, String server_name, String user, String password, String name) throws SQLException, RuntimeException{
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(DBMS, "DBMS cannot be null");
        try {
            DBConnection.setConn(DBMS, server_name, user, password, name);
            this.tables = new ArrayList<>();
            fillGraph(DBConnection.getConn(), DBMS);
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {//TODO DELETE WHEN IN REPOSITORY
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeConn();
        }
    }

    /** Receives an existing connection from the user to access the database (will not be closed).
     * For each table, it extracts the Primary Keys, Foreign Keys and other columns, each with datatype, column size,
     * and if nullable to create an oriented graph.
     * @param DBMS       DBMS [Supported: MySQL, SQLite]
     * @param connection Already established connection to the database
     * @param name       Schema and graph name
     */
    public Graph (String DBMS, Connection connection, String name) throws SQLException {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(DBMS, "DBMS cannot be null");
        Objects.requireNonNull(connection, "Connection cannot be null");

        this.tables = new ArrayList<>();
        fillGraph(connection, DBMS);
    }

    /** Visits each table to create the graph, using different methods according to the DBMS.<br>
     * Sets the node's types.
     * @param connection Connection to the database
     * @param DBMS DBMS [Supported: MySQL, SQLite]
     */
    private  void fillGraph(Connection connection, String DBMS) throws SQLException {
        List<String> allTables =  extractTableNames(connection);
        while (allTables.size()>0) {
            String tt = allTables.remove(0);
            switch (DBMS.toLowerCase()) {
                case "mysql" -> extractColumnData(connection, tt);
                case "sqlite" -> extractForeignKeysSQLite(connection, tt);
            }
        }
        setTypes();
    }

    /** Extracts from the database's metadata the names of the tables;
     * returns an empty list if there are no tables.
     * @param connection Connection to the database
     * @return List of table names
     */
    private List<String> extractTableNames(Connection connection) throws SQLException {
        List<String> table_names = new ArrayList<>();
        ResultSet result_set = null;
        try {
            DatabaseMetaData meta = connection.getMetaData();
            String[] types = {"TABLE"};
            result_set = meta.getTables(name, "dbo", "%", types);
            while (result_set.next()) {
                table_names.add(result_set.getString("TABLE_NAME"));
            }
        } catch (SQLException se) {
            throw new SQLException(se);
        } finally {
            DBConnection.closeRs(result_set);
        }
        return table_names;
    }

    /** For DBMS: MysQL.<br>
     * Extracts the primary keys, foreign keys and plain column for a MySql database, using metadata;
     * updates the tables according to discoveries.
     * @param connection Connection to DB
     * @param table      Exploring table
     */
    private void extractColumnData(Connection connection, String table) throws SQLException, RuntimeException {
        Statement st = null;
        ResultSet rs = null;
        try {
            Node n = new Node(table);

            // From the metadata, gets each column and its datatype, column size, and if it can be null;
            //  if it's autoincrement, it will deduce it's a primary key.
            DatabaseMetaData meta = connection.getMetaData();
            rs = meta.getColumns(name, null, table, "%");
            while (rs.next()) {
                if (rs.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES"))
                    n.addPrimaryKey(rs.getString("COLUMN_NAME"),
                            rs.getInt("DATA_TYPE"),
                            rs.getInt("COLUMN_SIZE"),
                            true);
                else n.addColumn(rs.getString("COLUMN_NAME"),
                        rs.getInt("DATA_TYPE"),
                        rs.getInt("COLUMN_SIZE"),
                        rs.getString("IS_NULLABLE").equalsIgnoreCase("YES"));
            }
            rs.close();

            // Searches the metadata for non-autoincrement primary keys, turning the already added plain columns.
            rs = meta.getPrimaryKeys(name, null,table);
            while (rs.next()) {
                n.changeColumnToPrimaryKey(rs.getString("COLUMN_NAME"));
            }
            rs.close();

            // Searches the metadata for foreign keys, turning the already added plain columns.
            //  For each, it gets the on_delete and on_update rules, the referred table and referred primary key.
            rs = meta.getImportedKeys(name, null, table);
            while (rs.next()) {
                String onDelete = switch (rs.getString("DELETE_RULE")) {
                    case "importedKeyCascade" -> "CASCADE";
                    case "importedKeySetNull" -> "SET NULL";
                    case "importedKeySetDefault" -> "SET DEFAULT";
                    default -> "RESTRICT";
                };

                String onUpdate = switch (rs.getString("UPDATE_RULE")) {
                    case "importedKeyCascade" -> "CASCADE";
                    case "importedKeySetNull" -> "SET NULL";
                    case "importedKeySetDefault" -> "SET DEFAULT";
                    default -> "RESTRICT";
                };

                n.changeColumnToForeignKey(rs.getString("FKCOLUMN_NAME"),
                        rs.getString("PKCOLUMN_NAME"),
                        rs.getString("PKTABLE_NAME"),
                        onDelete, onUpdate);
            }
            rs.close();

            // Counts the number of records in the table.
            String query = "SELECT count(*) FROM " + this.getName() + "."+ table;
            st = connection.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                n.setRecordNumber(rs.getInt(1));
            }

            tables.add(n);
        } catch (SQLException se) {
            throw new SQLException(se);
        } catch (NoSuchFieldException e) {
            System.out.println("Error in graph generation: tried to access a non-existing column.");
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeRs(rs);
            DBConnection.closeSt(st);
        }
    }

    /** For DBMS: SQLite.<br>
     * Extracts the primary keys, foreign keys and plain column for a MySql database, using metadata;
     * updates the tables according to discoveries.
     * @param connection Connection to DB
     * @param table      Exploring table
     */
    private void extractForeignKeysSQLite(Connection connection, String table) throws SQLException, RuntimeException {
        Statement st = null;
        ResultSet rs = null;
        ResultSet resultSet = null;
        try {
            Node n = new Node(table);
            String query;

            // From the metadata, gets each column and its datatype, max length, and if it can be null;
            //  if it's autoincrement, it will deduce it's a primary key.
            DatabaseMetaData meta = connection.getMetaData();
            st = connection.createStatement();
            rs = meta.getColumns(null, null, table, null);
            while (rs.next()) {
                String columnName = rs.getString(4);
                int columnType = rs.getInt(5);
                boolean isNullable = rs.getBoolean("IS_NULLABLE");
                boolean autoincrement = rs.getBoolean("IS_AUTOINCREMENT");

                query = "SELECT max(length(" + columnName + ")) AS max_column_size FROM " + table;
                resultSet = st.executeQuery(query);
                int columnSize = -1;
                while (resultSet.next())
                    columnSize = resultSet.getInt(1);

                if (autoincrement)
                    n.addPrimaryKey(columnName, columnType, columnSize, true);
                else n.addColumn(columnName, columnType, columnSize, isNullable);
            }

            // Queries for non-autoincrement primary keys, turning the already added plain columns;
            //  sets the nullability for those.
            query =  "PRAGMA table_info(" + table + ")";
            rs = st.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt(6) > 0)
                    n.changeColumnToPrimaryKey(rs.getString(2));
                if (rs.getInt(4) == 0)
                    n.setNullable(rs.getString(2), true);
            }

            // Queries for foreign keys, turning the already added plain columns.
            //  For each, it gets the on_delete and on_update rules, the referred table and referred primary key.
            query = "PRAGMA foreign_key_list(" + table + ")";
            rs = st.executeQuery(query);
            while (rs.next()) {
                n.changeColumnToForeignKey(rs.getString(4),
                        rs.getString(5),
                        rs.getString(3),
                        rs.getString(7),
                        rs.getString(6));
            }

            // Counts the number of records in the table.
            query = "SELECT count(*) FROM " + table;
            st = connection.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                n.setRecordNumber(rs.getInt(1));
            }

            tables.add(n);
        } catch (SQLException se) {
            throw new SQLException(se);
        } catch (NoSuchFieldException e) {
            System.out.println("Error in graph generation: tried to access a non-existing column.");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBConnection.closeRs(resultSet);
            DBConnection.closeRs(rs);
            DBConnection.closeSt(st);
        }
    }

    /** Searches a node and return it */
    private Node getNode(String name) {
        for (Node node: tables) {
            if (node.getName().equalsIgnoreCase(name))
                return node;
        }
        return null;
    }

    /** Returns the name of the graph.
     * @return Graph name
     */
    public String getName() {
        return name;
    }


    /*- LIST ALL -*/

    /** Return an immutable list of names of all the tables in the graph.
     * @return List of string containing the names of the tables
     */
    public List<String> listTables(){
        List<String> list = new ArrayList<>();
        for (Node n: tables)
            list.add(n.getName());
        return List.copyOf(list);
    }

    /** Returns an immutable map of all the arcs in the graph; the table name is
     * used as key, the object is a list containing its foreign keys;
     * if the table doesn't have any foreign key, there is no entry for said table.
     * @return Map containing a list of arcs for each table.
     */
    public Map<String, List<ForeignKeyColumn>> listArcs() {
        Map<String, List<ForeignKeyColumn>> map = new HashMap<>();
        for (Node node: tables) {
            if (node.getForeignKeyNumber() > 0)
                map.put(node.getName(), node.getForeignKeys());
        }
        return Map.copyOf(map);
    }

    /** Returns an immutable map of all the primary keys in the graph; the table name is
     * used as key, the object is an immutable list containing its primary keys.
     * If the table doesn't have any primary key, there is no entry for said table.
     * @return Map containing a list of primary keys for each table.
     */
    public Map<String, List<Column>> listPrimaryKeys() {
        Map<String, List<Column>> map = new HashMap<>();
        for (Node node: tables) {
            if (node.getPrimaryKeyNumber() > 0)
                map.put(node.getName(), List.copyOf(node.getPrimaryKeys()));
        }
        return Map.copyOf(map);
    }

    /*-- NUMBER OF --*/

    /** Returns the number of tables in the graph.
     * @return Number of tables in the database
     */
    public int getTableNumber(){
        return tables.size();
    }

    /** Returns the number of total arcs in the database.
     * @return Number of all references in the database
     */
    public int getArcNumber(){
        int count = 0;
        Map<String, List<ForeignKeyColumn>> map = listArcs();
        for (String table : map.keySet())
            count += map.get(table).size();
        return count;
    }

    /** Returns the number of primary keys for a given table; if the table
     * has no match in the graph, it returns -1.
     * @param table Table name
     * @return Number of primary keys in a given table
     */
    public int getPrimaryKeyNumberInTable(String table){
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return node.getPrimaryKeyNumber();
        return -1;
    }

    /** Returns the number of foreign keys for a given table; if the table
     * has no match in the graph, it returns -1.
     * @param table Table name
     * @return Number of foreign keys in a given table
     */
    public int getForeignKeyNumberInTable(String table){
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return node.getForeignKeyNumber();
        return -1;
    }

    /** Returns the number of foreign keys entering a given table; if the table
     * has no match in the graph, returns -1.
     * @param table Table name
     * @return Number of entering arcs in a given table
     */
    public int getEnteringArcNumberInTable(String table){
        Objects.requireNonNull(table, "Parameter table cannot be null");
        int count = -1;

        Node node = getNode(table);
        if (node != null) {
            count = 0;
            Map<String, List<ForeignKeyColumn>> map = getForeignKeysReferringTo(table);
            for (String referringTables : map.keySet())
                count += map.get(referringTables).size();
        }
        return count;
    }

    /** Returns the number of columns for a given table; if the table
     * has no match in the graph, returns -1.
     * @param table Table name
     * @return Number of columns in a given table
     */
    public int getColumnNumberInTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null) {
            return node.getColumnNumber();
        }
        return -1;
    }

    /** Returns the number of records in the table; returns -1 if
     * there is no match for the given table.
     * @param table Table name
     * @return Number of records in given table
     */
    public int getRecordNumberInTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null) {
            return node.getRecordNumber();
        }
        return  -1;
    }

    /** Sets the number of records to the given table; returns true if table matches
     * a node in the graph, false if there is no match.
     * @param table Table name
     * @param recordNumber Number of records to set
     * @return True if table exists;
     */
    public boolean setRecordNumberInTable(String table, int recordNumber) throws IllegalArgumentException{
        Objects.requireNonNull(table, "Parameter table cannot be null");
        Node node = getNode(table);
        if (node != null){
            node.setRecordNumber(recordNumber);
            return true;
        }
        return false;
    }

    /** Adds or subtract a number of records to the given table; returns true if table matches
     * a node in the graph, false if there is no match.
     * @param table Table name
     * @param addingNumber Number of records to add or subtract
     * @return True if table exists;
     */
    public boolean addToRecordNumberInTable(String table, int addingNumber) throws IllegalArgumentException{
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null) {
            node.addRecords(addingNumber);
            return true;
        }
        return false;
    }

    /*-FOREIGN KEY by table-*/

    /** Returns an immutable list of column names that are foreign key for
     * a given table; returns an empty list if the table does not have any foreign key,
     * or it doesn't exist.
     * @param table Table name
     * @return List of strings; contains the names of the column that are
     * foreign key for the given table.
     */
    public List<String> getForeignKeyNamesInTable(String table){
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return List.copyOf(node.getForeignKeyNames());

        return List.of();
    }

    /** Returns an immutable list of foreign key columns exiting a given table;
     *  returns an empty list if no table name match the given name, or the arc has no foreign keys.
     * @param table Table name
     * @return List of arcs
     */
    public List<ForeignKeyColumn> getForeignKeysInTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return List.copyOf(node.getForeignKeys());

        return List.of();
    }

    /** Given a table name, returns an immutable list of tables referred
     * by it; return an empty list if the table name doesn't match any
     * table in the graph, or it does not refer any other table.
     * @param table Table name
     * @return List of table names whose primary keys is referred by these
     * tables' foreign keys.
     */
    public List<String> getTablesReferredBy(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return List.copyOf(node.getReferredTables());

        return List.of();
    }


    /** Given a table name, returns an immutable list of table names whose
     * foreign keys refer to this table's primary key(s). It returns an
     * empty list if the table is referred by no other table, or it does not exist.
     * @param table Table name
     * @return List of table names that refer to the given table
     */
    public List<String> getTablesReferringTo(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        return List.copyOf(getForeignKeysReferringTo(table).keySet());
    }

    /** Returns an immutable map of each table that refers to the given table,
     *  and for each an immutable list of exiting arcs. The list is empty if
     *  no foreign key refers the table, or it does not exist.
     * @param table Table name
     * @return Map containing the tables referring to the given table, and for each a
     * list of the arcs referring to it
     */
    public Map<String,List<ForeignKeyColumn>> getForeignKeysReferringTo(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");
        Map<String, List<ForeignKeyColumn>> map = new HashMap<>();

        for (Node node : tables) {
            List<ForeignKeyColumn> list = new ArrayList<>();
            for (ForeignKeyColumn column : node.getForeignKeys()){
                if (column.getReferredTable().equalsIgnoreCase(table))
                    list.add(column);
            }
            if (list.size() != 0)
                map.put(node.getName(), List.copyOf(list));
        }
        return Map.copyOf(map);
    }

    /** Returns true if the table has at least one entering arc;
     * if the table has no entering arcs or there is no such table, it returns false.
     * @param table table name
     * @return TRUE if the table has entering arcs
     */
    public boolean isTableReferred(String table) {
        return !getForeignKeysReferringTo(table).isEmpty();
    }

    /** Returns true if the table has at least one exiting arc;
     * if the table has no exiting arcs or there is no such table, it returns false.
     * @param table table name
     * @return TRUE if the table has exiting arcs
     */
    public boolean isTableReferring(String table) {
        return !getTablesReferredBy(table).isEmpty();
    }

    /*- PRIMARY KEYS -*/

    /** Returns the primary keys for the table;
     * if the table has no primary key or there is no match, returns an empty list.
     * @param table Table name
     * @return List of primary keys for the given table
     */
    public List<Column> getPrimaryKeysInTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return List.copyOf(node.getPrimaryKeys());

        return List.of();
    }

    /** Returns the primary key of a table at the given index, or null
     * if either the index is out of bound or the table does not exist.
     * @param table Table name
     * @param index Index to which retrieve the primary key
     * @return Primary key at requested index
     */
    public Column getPrimaryKeyAtIndexInTable(String table, int index) {
        Objects.requireNonNull(table, "Parameter table cannot be null");
        if (index < 0) throw new IllegalArgumentException("Index cannot be negative");

        Node node = getNode(table);
        if (node != null && index < node.getPrimaryKeyNumber())
            return node.getPrimaryKeys().get(index);

        return null;
    }


    /** If the table only has one primary key, it returns it; else, it composes the primary keys as
     * Start + pk_1 + separator + ... + pk_n + End. <br>
     * It returns an empty string if there is no such table in the graph.
     *
     * @param table Table
     * @param separator separator character. e.g. ','
     * @param start Character at the start of the string, if more than one primary key. e.g. "("
     * @param end Character at the end of the string, if more than one primary key. e.g. ")"
     * @return Primary keys as a composed string E.g.: (pk_1, pk_2 , ... , pk_n)
     */
    public String getPrimaryKeysStringInTable(String table, char separator, String start, String end) {
        Objects.requireNonNull(table, "Parameter table cannot be null");
        List<Column> list = getPrimaryKeysInTable(table);

        if (list.size() == 0) {
            return "";

        } else {
            if (!isPrimaryKeyComposedInTable(table))
                return list.get(0).getName();
            else {
                StringBuilder s = new StringBuilder(start);
                int index = 0;
                s.append(list.get(index++).getName());
                do {
                    s.append(separator).append(" ").append(list.get(index++).getName());
                } while (index < list.size());
                return s.append(end).toString();
            }
        }
    }

    /** Given a table, checks if the primary key is composed or singular field.
     * @param table Table name
     * @return TRUE if the primary key is composed of more than one field; FALSE if
     * it has only one primary key or the table does not exist.
     */
    public boolean isPrimaryKeyComposedInTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        return getPrimaryKeyNumberInTable(table) > 1;
    }

    /** Given a table, checks if it has a primary key.
     * @param table Table name
     * @return TRUE if the table has at least a PK; FALSE if
     * it does not have any primary keys or the table does not exist
     */
    public boolean hasPrimaryKeyInTable(String table) {
        return getPrimaryKeyNumberInTable(table) > 0;
    }


    /*- COLUMNS -*/

    /** Returns an immutable list containing the columns for the requested table; if there is no match or
     * the table has no columns, it returns an empty list.
     * @param table Table name
     * @return List of columns for the given table
     */
    public List<Column> getColumnsInTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return List.copyOf(node.getColumns());

        return List.of();
    }

    /** Returns an immutable list containing all the columns that
     * are neither foreign keys nor primary keys in the requested table;
     * if the table has no plain column or there is no match for it, returns an empty list.
     * @param table Table name
     * @return List of plain columns for the given table
     */
    public List<Column> getPlainColumnsInTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null) {
            List<Column> list = new ArrayList<>(node.getColumns());
            list.removeAll(node.getPrimaryKeys());
            list.removeAll(node.getForeignKeys());
            return List.copyOf(list);
        }

        return List.of();
    }

    /** Searches for a column name in the table, and returns it if it exists;
     * it returns null otherwise.
     * @param column Column name
     * @param table Table name
     * @return The requested column
     */
    public Column searchColumnInTable(String column, String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        Node node = getNode(table);
        if (node != null)
            return node.contains(column);

        return null;
    }


    /*- TYPE -*/
    /** Updates the nodeTypes for all nodes in the graph;
     * this method is called by the constructor.
     */
    public void setTypes () {
        for (Node node : tables) {
            if (isTableReferring(node.getName())) {
                if (isTableReferred(node.getName()))
                    node.setType(nodeType.mid_node);
                else
                    node.setType(nodeType.source);
            } else {
                if (isTableReferred(node.getName()))
                    node.setType(nodeType.well);
                else
                    node.setType(nodeType.external_node);
            }
        }
    }

    /** Returns the type of the requested table;
     * if setTypes() was never called or for some reason the table's
     * type is unknown, it calls it.
     * Unknown will still be returned if it is the table's type, or there is no match.
     * @param table Table name
     * @return Node type
     */
    public nodeType getTableType(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");
        if (!tables.isEmpty() && tables.get(0).getType() == nodeType.unknown)
            setTypes();

        Node node = getNode(table);
        if (node != null)
            return node.getType();

        return nodeType.unknown;
    }

    /** Return an immutable list of all table names for the requested node type.
     * If the graph is empty or there is no table of said type, it returns an empty list.
     * if setTypes() was never called or for some reason the table's
     * type is unknown, it calls it.
     * @param nodeType Requested node type
     * @return List of table names for the node type
     */
    public List<String> listTablesOfType(nodeType nodeType) {
        Objects.requireNonNull(nodeType, "Parameter nodeType cannot be null");
        if (!tables.isEmpty() && tables.get(0).getType() == Graph.nodeType.unknown)
            setTypes();

        List<String> list = new ArrayList<>();
        for (Node node : tables) {
            if (node.getType() == nodeType)
                list.add(node.getName());
        }
        return List.copyOf(list);
    }

    /** Returns the immutable sorted list of table names according to topological order;
     * if there are cycles, the list will still be returned as good as it could.
     * All arcs partaking in the cycle will be marked as problematic.
     * @return List of topologically sorted tables
     */
    public List<String> sortTopological() {
        List<String> list = new ArrayList<>();
        TreeSet<String> unmarked = new TreeSet<>(listTables());
        TreeSet<String> temporary_mark = new TreeSet<>();
        TreeSet<String> marked = new TreeSet<>();

        while (!unmarked.isEmpty()) {
            String node = unmarked.first();
            unmarked.remove(node);
            visit(node, temporary_mark, marked, list);
        }
        return List.copyOf(list);
    }

    private String visit (String node, TreeSet<String> temporary_mark,
                           TreeSet<String> marked, List<String> list) {
        if (marked.contains(node)) {
            return null;
        }
        if (temporary_mark.contains(node)) {
            // All tree is considered a problem in the presence of a cycle
            return node;
        }

        String returning = null;
        temporary_mark.add(node);
        for (ForeignKeyColumn fk : getForeignKeysInTable(node)) {
            String temp = visit(fk.getReferredTable().toLowerCase(), temporary_mark, marked, list);
            if (temp != null) { //if not null there is a cycle, and we need to go back to the node itself
                returning = temp;
                if (returning.equals(node)) {
                    returning = null;
                }
                problematicArcs.computeIfAbsent(node, k -> new TreeSet<>());
                problematicArcs.get(node).add(fk);
            }
        }

        temporary_mark.remove(node);
        marked.add(node);
        list.add(0, node.toLowerCase());
        return returning;
    }

    /** Returns an immutable list of all tables directly
     *  reachable from a certain table, downward or upward.
     * @param table Table name
     * @return List of all tables reachable from a certain table.
     */
    public List<String> getTreeByTable(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");
        if (table.isBlank())
            return List.of();
        else {
            ArrayList<String> list = new ArrayList<>();
            TreeSet<String> temporary_mark = new TreeSet<>();
            TreeSet<String> marked = new TreeSet<>();
            visit(table, temporary_mark, marked, list);

            marked.clear();
            marked.addAll(getTablesReferringTo(table));
            while (!marked.isEmpty()) {
                table = marked.first();
                marked.remove(table);
                if (!list.contains(table)) {
                    marked.addAll(getTablesReferringTo(table));
                    list.add(table);
                }
            }
            return List.copyOf(list);
        }
    }


    /** Returns TRUE if the table has at least one arc marked as problematic,
     *  hence if the table is part of a cycle. It will return FALSE if the
     *  table is not part of a cycle, or it does not exist.
     * @param table Table name
     * @return TRUE if the table s part of a cycle.
     */
    public boolean hasProblematicArcs(String table) {
        Objects.requireNonNull(table, "Parameter table cannot be null");

        return problematicArcs.containsKey(table);
    }

    /** Returns TRUE if the table has a problematic arc with the name; returns FALSE if such arc is not problematic,
     * or either the table or arc do not exist.
     * @param table Table name
     * @param arc Column name
     * @return TRUE if the arc is problematic
     */
    public boolean isArcInTableProblematic(String table, String arc) {
        Objects.requireNonNull(table, "Parameter table cannot be null");
        Objects.requireNonNull(arc, "Parameter arc cannot be null");

        if (hasProblematicArcs(table))
            for (ForeignKeyColumn foreignKey : problematicArcs.get(table)) {
                if (foreignKey.getName().equalsIgnoreCase(arc))
                    return true;
            }
        return false;
    }

    /** Returns an immutable list of all the tables that are part of at least one cycle.
     * @return list of problematic tables
     */
    public List<String> listProblematicTables() {
        return List.copyOf(problematicArcs.keySet());
    }

    /** Returns an immutable map containing all arcs that are part of at least one cycle;
     * the table names are used as keys.
     * @return Map of problematic arcs
     */
    public Map<String, Set<ForeignKeyColumn>> listProblematicArcs() {
        return Map.copyOf(problematicArcs);
    }

    /** Returns an array of strings, containing in this order: table name, node type, number of records,
     * all primary key, all foreign keys and all plain columns.
     * @param table table name
     * @return array of strings containing table information
     */
    public String[] getTableInfo(String table) {
        ArrayList<String> list = new ArrayList<>();
        for (Node node:tables) {
            if (node.getName().equalsIgnoreCase(table)) {
                list.add(table);
                list.add(""+node.getType());
                list.add(node.getRecordNumber()+"");
                list.addAll(node.getPrimaryKeyNames());
                for (ForeignKeyColumn s : node.getForeignKeys())
                    list.add(s.getName() + "->" + s.getReferredTable() + " (" + s.getReferredPrimaryKey() + ")");
                for (Column s : getPlainColumnsInTable(table))
                    list.add(s.getName());
            }
        }
        return list.toArray(new String[0]);
    }

    private void printTableInfo(String table) {
        for (Node node:tables) {
            if (node.getName().equalsIgnoreCase(table)) {
                System.out.println("" + table.toUpperCase() + "");
                System.out.print("\t Its PK are: ");
                printInLine(this.getPrimaryKeysInTable(table), false);
                //System.out.println("\t Its PK is: "+ this.getPKStringByTable(node.getName(), ',', "(", ")"));


                if (this.getPlainColumnsInTable(table).size() == 0)
                    System.out.println("\t All columns have a purpose. ");
                else {
                    System.out.print("\t It has the following  plain columns: ");
                    printInLine(this.getPlainColumnsInTable(table), false);
                }

                System.out.println("\tThis table has " + this.getRecordNumberInTable(table)+ " records.");


                if (getForeignKeyNumberInTable(table) == 0)
                    System.out.println("\t It has no foreign keys. ");
                else {
                    System.out.print("\t It has the following foreign keys: ");
                    printInLine(this.getForeignKeyNamesInTable(table));
                }
                Map<String, List<ForeignKeyColumn>> map = this.getForeignKeysReferringTo(table);
                if (map.size() == 0)
                    System.out.println("\t It has no entering arcs. ");
                else {
                    System.out.println("\n\t It has the following entering arcs: ");
                    for (String t: map.keySet()){
                        System.out.println("\t  "+t+":");
                        printArcs(map.get(t));
                    }
                }
            }
        }
    }


    private static void printArcs(List<ForeignKeyColumn> fKbyTable) {
        for (ForeignKeyColumn a:fKbyTable) {
            System.out.println("\t\t"+a.toString());
        }
    }

    private static void printInLine(List<Column> s, boolean ignoredB) {
        StringBuilder string = new StringBuilder();
        if (s.size() != 0) string.append(s.get(0).toString());
        for (int i =1; i<s.size(); ++i) {
            string.append(", ").append(s.get(i).toString());
        }
        System.out.println(string);
    }


    private static void printInLine(List<String> s) {
        StringBuilder string = new StringBuilder();
        if (s.size() != 0) string.append(s.get(0));
        for (int i =1; i<s.size(); ++i) {
            string.append(", ").append(s.get(i));
        }
        System.out.println(string);
    }

    /** Opens a java application to visualize the graph: the nodes will be displayed on the left canvas
     * according to topological order, connected by labeled arcs. <br>
     * Clicking on a node will highlight it and its tree, and on the right panel will
     * appear relevant information; the node can be moved around the canvas to reach a less confusing display.<br>
     * From the panel, it is possible to highlight nodes of a certain type, zoom in and out, switch between light
     * and dark colors, and access the 'help' panel.<br>
     * Opening the saving mode by clicking on the floppy disk in the panel, two movable dots
     * will appear on the canvas, connected by a dashed line, marking the saving area; you can add a custom name
     * to the image writing on the line to the right of the savingMode button, before saving the .png clicking
     * on the "V" button. Zoom in for higher resolution.
     */
    public void visualize () {
        Visualize visualize = new Visualize(this);
    }

    /** Autogenerated
     * @param o Object
     * @return TRUE if graphs are equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return getName().equals(graph.getName()) && tables.equals(graph.tables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), tables);
    }

}

