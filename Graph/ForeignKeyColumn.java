package Graph;

public class ForeignKeyColumn extends Column implements Comparable{
    /** Table referred by the foreign key     */
    private final String referencedTable;
    /** Primary key referred by the foreign key     */
    private final String referencedPrimaryKey;
    /** Action set on the database if the foreign key is deleted     */
    private final String onDelete;
    /** Action set on the database if the foreign key is updated     */
    private final String onUpdate;

    /** Constructor: Creates a new foreign key, that can also be a primary key.
     * @param foreignKey Name of column
     * @param isPK if TRUE the column is a primary key
     * @param referencedPrimaryKey Primary key referred by this FK
     * @param referencedTable Table referred by this FK
     * @param datatype Type of data
     * @param isNullable if column can be null
     * @param columnSize Max size of values
     * @param isAutoIncrement TRUE if column is autoincrement
     * @param onDelete Action the DB will take on delete
     * @param onUpdate Action the DB will take on update
     */
    protected ForeignKeyColumn(String foreignKey,int datatype, int columnSize, boolean isPK,
                               boolean isAutoIncrement, boolean isNullable, String referencedPrimaryKey,
                               String referencedTable, String onDelete, String onUpdate) {
        super(foreignKey, datatype, columnSize, isPK, isAutoIncrement, isNullable);
        this.referencedPrimaryKey = referencedPrimaryKey;
        this.referencedTable = referencedTable;
        this.onDelete = onDelete;
        this.onUpdate = onUpdate;
    }

    /** Returns the name of the primary key in the table
     * referred by this foreign key.
     * @return primary key in the referred table
     */
    public String getReferredPrimaryKey() {
        return referencedPrimaryKey;
    }

    /** Returns the name of the table referred by this foreign key.
     * @return referred table
     */
    public String getReferredTable() {
        return referencedTable;
    }

    /** Returns the action the database will take during DELETE queries.
     * @return Action the database will take on delete
     */
    public String getOnDelete() {
        return onDelete;
    }

    /** Returns the action the database will take on UPDATE queries.
     * @return Action the database will take on update
     */
    public String getOnUpdate() {
        return onUpdate;
    }

    /** Returns a string in the format 'FK -> Table(PK)' with the information of the arc.
     * @return String with the information of the arc
     */
    public String toString() {
        return super.toString() + " -> " + getReferredTable() + "(" + getReferredPrimaryKey() + ")";
    }

    /** Implements the comparable interface; comparison follows the alphabetical order of
     * the name.
     * @param o the object to be compared.
     * @return -1, 0, 1 according to string compare
     */
    @Override
    public int compareTo(Object o) {
        return getName().compareTo(((ForeignKeyColumn) o).getName());
    }
}