package Graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
    /** Table name. */
    private String name;
    /** List of all the columns in the table; they can be PrimaryKeys, plain columns or ForeignKeys.  */
    private final List<Column> columns;
    /** Number of records in the table. */
    private int numOfRecords;

    /** Node type. */
    private Graph.nodeType type;

    /** Constructor: <br>
     * Creates a new empty node with the given name.
     * @param name Table name; can't be null.
     */
    public Node(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name can't be null");
        this.name = name;
        this.numOfRecords = 0;
        this.type = Graph.nodeType.unknown;
        this.columns = new ArrayList<>();
    }


    /** Constructor: <br>
     * Creates a new node with the given name and number of records.
     * @param name Table name; can't be null.
     * @param numOfRecords Number of records in table; can't be negative.
     */
    public Node(String name, int numOfRecords) throws IllegalArgumentException {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name can't be null");
        this.name = name;
        if (numOfRecords < 0)
            throw new IllegalArgumentException("Number of records can't be negative");
        this.numOfRecords = numOfRecords;
        this.type = Graph.nodeType.unknown;
        this.columns = new ArrayList<>();
    }

    /** Constructor: <br>
     * Creates a new node with the given name, list of columns and number of records.
     * @param name Table name; can't be null.
     * @param columns List of columns
     * @param numOfRecords Number of records in table; can't be negative.
     */
    public Node(String name, List<Column> columns, int numOfRecords) throws IllegalArgumentException {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name can't be null");
        this.name = name;
        if (numOfRecords < 0)
            throw new IllegalArgumentException("Number of records can't be negative");
        this.numOfRecords = numOfRecords;
        this.type = Graph.nodeType.unknown;
        this.columns = columns;
    }

    /** Creates and return a clone of this node, with the same name, columns and number of records.
     * @return Clone of the node
     */
    protected Node cloneNode() {
        return new Node(name, new ArrayList<>(columns), numOfRecords);
    }

    /** Searches for an existing column with the given name and returns it.
     * @param column Column name
     * @return Column if it exists, null else.
     */
    protected Column contains (String column) {
        for (Column c: columns) {
            if (c.getName().equalsIgnoreCase(column))
                return c;
        }
        return null;
    }

    /** Returns the table name.
     * @return Table name
     */
    protected String getName() {
        return name;
    }

    /** Adds a new plain column to the node, if there isn't already a column with such name.
     * @param name Column name
     * @param datatype Type of data
     * @param columnSize Size of data (or max size in the table for SQLite)
     * @param isNullable True if the column value can be null
     */
    protected void addColumn(String name, int datatype, int columnSize, boolean isPrimaryKey, boolean isAutoincrement, boolean isNullable) {
        if (name != null && !name.isEmpty()) {
            if (this.contains(name) == null) {
                columns.add(new Column(name, datatype, columnSize, isPrimaryKey, isAutoincrement, isNullable));
            }
        } else throw new IllegalArgumentException("Parameters can't be null or empty");
    }

    /**
     * Adds a new plain column to the node, if there isn't already a column with such name.
     *
     * @param name                 Column name
     * @param datatype             Type of data
     * @param columnSize           Size of data (or max size in the table for SQLite)
     * @param isNullable           True if the column value can be null
     * @param referencedPrimaryKey column referenced by the foreign key
     * @param referencedTable      table referenced by the foreign key
     * @param onDelete             action taken by the db on delete queries
     * @param onUpdate             action taken by the db on update queries
     */
    protected void addForeignKey(String name, int datatype, int columnSize, boolean isPrimaryKey,
                                 boolean isAutoincrement, boolean isNullable, String referencedPrimaryKey,
                                 String referencedTable, String onDelete, String onUpdate) {
        if (name != null && !name.isEmpty()) {
            if (this.contains(name) == null) {
                columns.add(new ForeignKeyColumn(name, datatype, columnSize, isPrimaryKey, isAutoincrement, isNullable,
                        referencedPrimaryKey, referencedTable, onDelete, onUpdate));
            }
        } else throw new IllegalArgumentException("Parameters can't be null or empty");
    }

    /** Sets the nullable field of a column in the table.
     * @param name Column name
     * @param isNullable True if the column value can be null
     */
    protected void setNullable(String name, boolean isNullable){
        if (name != null && !name.isEmpty()) {
            Column c;
            if ((c = this.contains(name)) != null) {
                c.setNullable(isNullable);
            }
        }
    }

    /**
     * Sets the number of records in this table to a specified value.
     * @param n New value
     * @throws IllegalArgumentException if new value is negative
     */
    protected void setRecordNumber(int n) throws IllegalArgumentException {
        if (n >= 0)
            this.numOfRecords = n;
        else throw new IllegalArgumentException("Number of records can't be negative");
    }


    /** Adds or subtracts from the number of records the specified amount.
     * @param n Number to add or subtract
     * @throws IllegalArgumentException if records+n is negative
     */
    protected void addRecords(int n) throws IllegalArgumentException {
        if (this.numOfRecords + n >= 0)
            this.numOfRecords += n;
        else throw new IllegalArgumentException("Number of records can't be negative");
    }

    /** Return the number of records in the table.
     * @return Number of records in the table
     */
    protected int getRecordNumber(){
        return numOfRecords;
    }

    /**
     * Returns the number of all columns in the table.
     * @return Number of columns in the table
     */
    protected int getColumnNumber(){
        return columns.size();
    }

    /** Returns an arraylist of all the columns in the table; changes to the arraylist
     * will not affect the actual node columns.
     * @return Arraylist of columns
     */
    protected List<Column> getColumns(){
        return new ArrayList<>(columns);
    }


    /** Returns an arraylist of all the columns' names in the table.
     * @return List of column names
     */
    protected List<String> getColumnsNames(){
        List<String> c = new ArrayList<>();
        for (Column column: columns)
            c.add(column.getName());
        return c;
    }

    /** Returns the number of Primary Key columns in the table.
     * @return Number of primary keys in the table
     */
    protected int getPrimaryKeyNumber(){
        return getPrimaryKeys().size();
    }

    /** Returns TRUE if the column is a Primary Key column,
     *  FALSE if it isn't or there is no column with that name.
     * @param name Name of the column
     * @return TRUE if the column is a PK, FALSE if it isn't or doesn't exist.
     */
    private boolean isPrimaryKey(String name) {
        return this.getPrimaryKeyNames().contains(name);
    }

    /** Returns an array list of all the Primary Key columns in the table; the list is
     * empty if the table has no primary keys.
     * @return List of primary keys
     */
    protected List<Column> getPrimaryKeys(){
        List<Column> list = new ArrayList<>();
        for (Column column: columns){
            if (column.isPrimaryKey())
                list.add(column);
        }
        return list;
    }

    /** Returns an arraylist of all the primary keys' names in the table.
     * @return List of column names
     */
    protected List<String> getPrimaryKeyNames(){
        List<String> list = new ArrayList<>();
        for (Column column: getPrimaryKeys())
            list.add(column.getName());
        return list;
    }

    /**
     * Returns the number of Foreign Key columns in the table.
     * @return Number of foreign keys in the table
     */
    protected int getForeignKeyNumber(){
        return getForeignKeys().size();
    }

    /** Returns an array list of all the Foreign Key columns in the table; the list is
     * empty if the table has no foreign keys.
     * @return List of foreign keys
     */
    protected List<ForeignKeyColumn> getForeignKeys(){
        List<ForeignKeyColumn> list = new ArrayList<>();
        for (Column column: columns){
            if (column instanceof ForeignKeyColumn)
                list.add((ForeignKeyColumn)column);
        }
        return list;
    }

    /** Returns an arraylist of all the foreign keys' names in the table.
     * @return List of column names
     */
    protected List<String> getForeignKeyNames(){
        List<String> list = new ArrayList<>();
        for (ForeignKeyColumn column: getForeignKeys())
            list.add(column.getName());
        return list;
    }

    /** Returns a list of table names reachable from this table's foreign keys;
     *  the list is empty if this table has no foreign key.
     * @return List of referred table names
     * */
    protected List<String> getReferredTables(){
        List<String> referredTables = new ArrayList<>();
        for (ForeignKeyColumn foreignKey: getForeignKeys()){
            if (!referredTables.contains(foreignKey.getReferredTable()))
                referredTables.add(foreignKey.getReferredTable());
        }
        return referredTables;
    }

    /** Return this table's node type.
     * @return Node type
     */
    protected Graph.nodeType getType() {
        return type;
    }

    /** Sets this table's node type to the given value.
     * @param type Node type
     */
    protected void setType(Graph.nodeType type) {
        this.type = type;
    }

    /** Sets this table's name to the given value.
     * @param name Table name
     */
    protected void setName(String name) {
        this.name = name;
    }
}