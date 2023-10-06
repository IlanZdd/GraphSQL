package Graph;

import java.util.Objects;

public class Column {
    /** Column name     */
    private final String name;
    /** TRUE if column is a primary key     */
    private boolean isPrimaryKey;
    /** TRUE if column is an autoincrement primary key    */
    private boolean isAutoIncrementing;
    /** TRUE if the column value can be null     */
    private boolean isNullable;
    /** Type of data in the column; saved as a numerical value     */
    private final int datatype;
    /** Size of the column, or max size for SQLite     */
    private final int columnSize;


    /** Constructor: Creates a new column
     * @param name Column name
     * @param datatype Type of data
     * @param columnSize Max size of values
     * @param isPrimaryKey TRUE if column is a primary key
     * @param isAutoIncrementing TRUE if column is autoincrement
     * @param isNullable TRUE if column can contain NULL
     */
    protected Column(String name, int datatype, int columnSize, boolean isPrimaryKey, boolean isAutoIncrementing, boolean isNullable) {
        this.name = name;
        this.datatype = datatype;
        this.columnSize = columnSize;
        this.isPrimaryKey = isPrimaryKey;
        this.isAutoIncrementing = isAutoIncrementing;
        this.isNullable = isNullable;
    }

    /** Return the column's name.
     * @return Column name
     */
    public String getName() {
        return name;
    }

    /** Returns TRUE if values in the column can be NULL, else FALSE.
     * @return TRUE  if values can be NULL
     */
    public boolean isNullable() { return isNullable; }

    /** Sets ìsNullable field.
     * @param nullable TRUE  if values can be NULL
     */
    protected void setNullable(boolean nullable) { isNullable = nullable; }

    /** Returns TRUE if the column is a primary key.
     * @return TRUE if the column is a primary key
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /** Turns an existing column into a primary key
     * @param isPrimaryKey TRUE if column is a primary key
     */
    protected void setPrimaryKey(boolean isPrimaryKey) { this.isPrimaryKey = isPrimaryKey; }

    /** Returns TRUE if column is an autoincrement primary key.
     * @return TRUE if column is autoincrement
     */
    public boolean isAutoIncrementing() { return isAutoIncrementing; }

    /** Turns a column into an autoincrement column
     * @param isAutoIncrementing If TRUE turns the column into an autoincrement column
     */
    protected void setAutoIncrementing(boolean isAutoIncrementing) {
        this.isAutoIncrementing = isAutoIncrementing;
    }

    /** Returns the maximum size allowed for values in this column, or the maximum size
     * currently in the column for SQLite.
     * @return Column size
     */
    public int getColumnSize() { return columnSize; }

    /** Returns data type values can assume in this column, as numbers.
     * <br>e.g. Varchar = 12, int = 4, etc...
     * @return Data type
     */
    public int getDatatype() { return datatype; }

    /** Returns the column name, and if it is a primary key.
     * @return Column's info
     */
    public String toString(){
        String s = name ;
        if (isPrimaryKey()) {
            s+= " (PK)";
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return getName().equals(column.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
