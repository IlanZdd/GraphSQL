import Graph.Graph;
import Utils.DBConnection;

public class Main {
    public static void main (String[] args) throws Exception {
        DBConnection.setConn("mysql", "localhost:3306", "root", "Password_123", "classicmodels");
        Graph g = new Graph("mysql", DBConnection.getConn(), "classicmodels");/*
        Graph g1 = new Graph("sqlite", "", "", "", "/home/ilan/Downloads/pokemon_db.db");

        System.out.println("\t" + "graph name : " + g.getName());
        System.out.println("\t" + "list tables : " + Arrays.toString(g.listTables().toArray()));
        System.out.println("\t" + "list arcs : ");
        for (String s : g.listArcs().keySet())
            System.out.println("\t" + "\t" + s + ": " + Arrays.toString(g.listArcs().get(s).toArray()));
        System.out.println("\t" + "list pks : ");
        for (String s : g.listPrimaryKeys().keySet())
            System.out.println("\t" + "\t" + s + ": " + Arrays.toString(g.listPrimaryKeys().get(s).toArray()));
        System.out.println("\t" + "list table by type : ");
        for (Graph.nodeType s : Graph.nodeType.values())
            System.out.println("\t" + "\t" + s.toString() + ": " + Arrays.toString(g.listTablesOfType(s).toArray()));
        System.out.println("\t" + "topological sort : " + Arrays.toString(g.sortTopological().toArray()));
        System.out.println("\t" + "bad table : " + Arrays.toString(g.listProblematicTables().toArray()));
        for (String s : g.listProblematicArcs().keySet())
            System.out.println("\t" + "\t" + s + ": " + Arrays.toString(g.listProblematicArcs().get(s).toArray()));


        System.out.println("\t" + "table number : " + g.getTableNumber());
        System.out.println("\t" + "arc number : " + g.getArcNumber());

        System.out.println();
        System.out.println();
        System.out.println();
        for (String s : g.listTables()) {
            System.out.println("table  : " + s.toUpperCase());
            
            System.out.println("\t" + "pk number : " + g.getPrimaryKeyNumberInTable(s));
            System.out.println("\t" + "fk number : " + g.getForeignKeyNumberInTable(s));
            System.out.println("\t" + "entering arc number : " + g.getEnteringArcNumberInTable(s));
            System.out.println("\t" + "column number : " + g.getColumnNumberInTable(s));
            System.out.println("\t" + "records number : " + g.getRecordNumberInTable(s));
            g.setRecordNumberInTable(s, 10);
            System.out.println("\t" + "records number = 10 : " + g.getRecordNumberInTable(s));
            g.addToRecordNumberInTable(s, 10);
            System.out.println("\t" + "records number = 20 : " + g.getRecordNumberInTable(s));

            System.out.println("\t" + "fk names : " + Arrays.toString(g.getForeignKeyNamesInTable(s).toArray()));
            System.out.println("\t" + "fks : " + Arrays.toString(g.getForeignKeysInTable(s).toArray()));
            System.out.println("\t" + "table referred : " + Arrays.toString(g.getTablesReferredBy(s).toArray()));
            System.out.println("\t" + "tables referring : " + Arrays.toString(g.getTablesReferringTo(s).toArray()));
            System.out.println("\t" + "fks referring: ");
            for (String st : g.getForeignKeysReferringTo(s).keySet())
                System.out.println("\t" + "\t" + st + ": " +
                        Arrays.toString(g.getForeignKeysReferringTo(s).get(st).toArray()));
            System.out.println("\t" + "is referring : " + g.isTableReferring(s));
            System.out.println("\t" + "is referred : " + g.isTableReferred(s));

            System.out.println("\t" + "pks : " + Arrays.toString(g.getPrimaryKeysInTable(s).toArray()));
            System.out.println("\t" + "pk at index : ");
            for (int i = 0 ; i < g.getPrimaryKeyNumberInTable(s); ++i)
                System.out.println("\t\t" + "pk at index " + i + ": " + g.getPrimaryKeyAtIndexInTable(s, i));
            System.out.println("\t" + "pk string : " + g.getPrimaryKeysStringInTable(s, ',', "" , ""));
            System.out.println("\t" + "pk composed : " + g.isPrimaryKeyComposedInTable(s));
            System.out.println("\t" + "has pk : " + g.hasPrimaryKeyInTable(s));

            System.out.println("\t" + "columns : " + Arrays.toString(g.getColumnsInTable(s).toArray()));
            System.out.println("\t" + "plain columns : " + Arrays.toString(g.getPlainColumnsInTable(s).toArray()));
            for (Column st : g.getColumnsInTable(s))
                System.out.println("\t" + "\t searching " + st.getName() + ": " + g.searchColumnInTable(st.getName(), s).getName());

            System.out.println("\t" + "type : " + g.getTableType(s));
            System.out.println("\t" + "table tree : " + Arrays.toString(g.getTreeByTable(s).toArray()));

            System.out.println("\t" + "has cycle : " + g.hasProblematicArcs(s));
            for (Column st : g.getColumnsInTable(s))
                System.out.println("\t" + "\tis "+ st.getName() + " bad: " + g.isArcInTableProblematic(s, st.getName()));

        }*/
        g.visualize();
    }
}
