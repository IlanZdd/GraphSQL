package Utils;

public class ColumnInfoDTO {
    private String name = null;
    private int dataType;
    private int columnSize;
    private boolean isPK = false;
    private boolean isNullable = true;
    private boolean isAutoIncrement = false;
    private String referencedTable = null;
    private String referencedPrimaryKey = null;
    private String onDelete = null;
    private String onUpdate = null;
    private boolean isFK = false;

    public ColumnInfoDTO() {

    }

    public boolean isFK() {
        return isFK;
    }

    public void setFK(boolean FK) {
        isFK = FK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public boolean isPK() {
        return isPK;
    }

    public void setPK(boolean PK) {
        isPK = PK;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(String referencedTable) {
        this.referencedTable = referencedTable;
    }

    public String getReferencedPrimaryKey() {
        return referencedPrimaryKey;
    }

    public void setReferencedPrimaryKey(String referencedPrimaryKey) {
        this.referencedPrimaryKey = referencedPrimaryKey;
    }

    public String getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(String onDelete) {
        this.onDelete = onDelete;
    }

    public String getOnUpdate() {
        return onUpdate;
    }

    public void setOnUpdate(String onUpdate) {
        this.onUpdate = onUpdate;
    }
}
