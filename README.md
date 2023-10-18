# GraphSQL
GraphSQL is a java library to create a read-only oriented graph G from an existing database (Supported DBMS: SQLite, MySQL).
The graph's  nodes are the database's tables, connected between each other with their foreign keys; while the graph is oriented to a read-only use, the number of records in each table can be read, set and records can be added (or subtracted).
For each column, the graph contain information about their name, datatype and datasize, if values can be null, if they are primary keys and if they are auto-increment;
Foreign Keys contain the additional information of what table and primary key they are referencing, and the ON_DELETE and ON_UPDATE rule.

Calling the **visualize()** method on the graph will open a java application to visualize it: the nodes will be displayed on the canvas from top to bottom according to topological order, connected by labeled arcs. 
	Clicking on a node will highlight it and its tree, and on the right panel relevant information will appear; the node can be moved around the canvas to reach a less confusing display; the camera can be moved as well.
	It is possible to highlight nodes of a certain type, zoom in and out, switch between light and dark colors. 
	Opening the saving mode by clicking on the floppy disk on the bottom left corner, two movable dots connected by a dashed line will appear on the canvas, marking the saving area; you can write the custom name writing on the line to the right of the floppy button (by default, it is the graph name), before saving the image in a .png format clicking on the "V" button. Zoom in before saving for higher resolution (your saving area will follow).

## CONSTRUCTOR

The graph can be created with the constructor:

  - **Graph (ServerURL, Username, Password, Schema)**: the constructor tries to establish a new temporary connection to the Database using the given URL, Username and Password; the schema will be used as graph name. The connection will be closed at the end.
  
  The nodes type are set before returning the object.

  The connection constructor has been removed.

## METHODS

- **getName(): String** = Returns the name of the graph.

List all:

- **listTables() : List<String>** = Return an immutable list of names of all the tables in the graph. 

- **listArcs() : Map<String, List<ForeignKeyColumn>>** = Returns an immutable map of all the arcs in the graph; the table name is used as key, the object is an immutable list containing its foreign keys; if the table doesn't have any foreign key, there is no entry for said table.

- **listPrimaryKeys() : Map<String, List<Column>>** = Returns an immutable map of all the primary keys in the graph; the table name is used as key, the object is an immutable list containing its primary keys; if the table doesn't have any primary key, there is no entry for said table.

- **listTablesOfType(nodeType nodeType) : List<String>** = Return an immutable list of all table names for the requested node type. If the graph is empty or there is no table of said type, it returns an empty list. if setTypes() was never called or for some reason the table's type is unknown, it calls it.

- **listProblematicTables() : List<String>** = Returns an immutable list of all the tables that are part of at least one cycle.

- **listProblematicArcs() : Map<String, Set<ForeignKeyColumn>>** = Returns an immutable map containing all arcs that are part of at least one cycle; the table names are used as keys.

- **sortTopological() : List<String>** = Returns the immutable sorted list of table names according to topological order; if there are cycles, the list will still be returned as good as it could. All arcs partaking in the cycle will be marked as problematic.

- **getTreeByTable(String table) : List<String>** = Returns an immutable list of all tables directly reachable from a certain table, downward or upward.

- **getTableInfo(String table) : String[]** = Returns an array of strings, containing in this order: table name, node type, number of records, all primary key, all foreign keys and all plain columns.


Number of:

- **getTableNumber() : int**: Returns the number of tables in the graph.

- **getArcNumber() : int**: Returns the number of total arcs in the database.

- **getPrimaryKeyNumberInTable(String table) : int**: Returns the number of primary keys for a given table; if the table has no match in the graph, it returns -1.

- **getForeignKeyNumberInTable(String table) : int**: Returns the number of foreign keys for a given table; if the table has no match in the graph, it returns -1.

- **getEnteringArcNumberInTable(String table) : int**: Returns the number of foreign keys entering a given table; if the table has no match in the graph, returns -1.

- **getColumnNumberInTable(String table) : int**: Returns the number of columns for a given table; if the table has no match in the graph, returns -1.

- **getRecordNumberInTable(String table) : int**: Returns the number of records in the table; returns -1 if there is no match for the given table.

- **setRecordNumberInTable(String table, int recordNumber) : boolean** : Sets the number of records of the given table to a new non-negative value; returns true if table matches a node in the graph, false if there is no match.

- **addToRecordNumberInTable(String table, int addingNumber) : boolean**: Adds or subtract a number of records to the given table, to a final non-negative value; returns true if table matches a node in the graph, false if there is no match.


Foreign key methods:

- **getForeignKeyNamesInTable(String table) : List<String>** = Returns an immutable list of column names that are foreign key for a given table; returns an empty list if the table does not have any foreign key, or it doesn't exist.

- **getForeignKeysInTable(String table) : List<ForeignKeyColumn>** = Returns an immutable list of foreign key columns exiting a given table; returns an empty list if no table name match the given name, or the arc has no foreign keys.

- **getTablesReferredBy(String table) : List<String>** = Given a table name, returns an immutable list of tables referred by it; it returns an empty list if the table name doesn't match any table in the graph, or it does not refer any other table.

- **getTablesReferringTo(String table) : List<String>** = Given a table name, returns an immutable list of table names whose foreign keys refer to this table's primary key(s). It returns an empty list if the table is referred by no other table, or it does not exist.

- **getForeignKeysReferringTo(String table) : Map<String, List<ForeignKeyColumn>>** = Returns an immutable map of each table that refers to the given table, and for each an immutable list of exiting arcs. The list is empty if no foreign key refers the table, or it does not exist.

- **isTableReferred(String table) : boolean** = Returns true if the table has at least one entering arc; if the table has no entering arcs or there is no such table, it returns false.

- **isTableReferring(String table) : boolean** = Returns true if the table has at least one exiting arc; if the table has no exiting arcs or there is no such table, it returns false.


Primary keys methods:

- **getPrimaryKeysInTable(String table) : List<Column>** = Returns the primary keys for the table; if the table has no primary key or there is no match, returns an empty list.

- **getPrimaryKeyAtIndexInTable(String table, int index) : Column** = Returns the primary key of a table at the given index, or null if either the index is out of bound or the table does not exist.

- **getPrimaryKeysStringInTable(String table, char separator, String start, String end) : String** = If the table only has one primary key, it returns it; else, it composes the primary keys as Start + pk_1 + separator + ... + pk_n + End. It returns an empty string if there is no such table in the graph.

- **isPrimaryKeyComposedInTable(String table) : boolean** = Given a table, checks if the primary key is composed or singular field.

- **hasPrimaryKeyInTable(String table) : boolean** = Given a table, checks if it has a primary key.


Column methods:

- **getColumnsByTable(String table) : List<Column>** = Returns an immutable list containing the columns for the requested table; if there is no match or the table has no columns, it returns an empty list.

- **getPlainColumnsInTable(String table) : List<Column>** = Returns an immutable list containing all the columns that are neither foreign keys nor primary keys in the requested table; if the table has no plain column or there is no match for it, returns an empty list.

- **searchColumnInTable(String column, String table) : Column** = Searches for a column name in the table, and returns it if it exists; it returns null otherwise.


Node type methods:

- **setTypes() : void** = Updates the nodeTypes for all nodes in the graph; this method is called by the constructor.

- **getTableType(String table) : nodeType** = Returns the type of the requested table; if setTypes() was never called or for some reason the table's type is unknown, it calls it. Unknown will still be returned if it is the table's type, or there is no match.



Cycle methods:

- **hasProblematicArcs(String table) : boolean** = Returns TRUE if the table has at least one arc marked as problematic, hence if the table is part of a cycle.

- **isArcInTableProblematic(String table, String arc) : boolean** = Returns TRUE if the table has a problematic arc with the name; returns FALSE if such arc is not problematic, or either the table or arc do not exist.





