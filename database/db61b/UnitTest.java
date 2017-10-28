package db61b;

import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
/** The suite of all JUnit tests for the qirkat package.
 *  @author Jason Xiao
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    @Test
    public void testColumns() {
        String[] seed = new String[]{"love" , "sympathy"};
        Table table = new Table(seed);
        assertEquals(2, table.columns());
    }

    @Test
    public void testGetTitle() {
        String[] seed = new String[]{"love" , "sympathy"};
        Table table = new Table(seed);
        assertEquals("sympathy" , table.getTitle(1));
    }

    @Test
    public void testFindColumn() {
        String[] seed = new String[]{"love" , "sympathy"};
        Table table = new Table(seed);
        assertEquals(1 , table.findColumn("sympathy"));
        assertEquals(-1 , table.findColumn(("hate")));
    }

    @Test
    public void testSize() {
        String[] seed = new String[]{"love", "sympathy"};
        Table table = new Table(seed);
        assertEquals(0 , table.size());
    }

    @Test
    public void testGet() {
        String[] seed = new String[]{"love", "sympathy"};
        Table table = new Table(seed);
        String[] foo = {"happy days", "lovely life"};
        table.add(foo);
        assertEquals("happy days" , table.get(0, 0));
    }

    @Test
    public void testAdd() {
        String[] seed = new String[]{"love", "sympathy"};
        Table table = new Table(seed);
        String[] foo = {"happy days" , "lovely life"};
        table.add(foo);
        assertEquals("lovely life" , table.get(0 , 1));
    }

    @Test
    public void testAdd2() {
        String[] seed = new String[]{"one" , "two"};
        Table table = new Table(seed);
        String[] foo = {"1" , "2"};
        table.add(foo);
        String[] seed2 = new String[]{"three", "four"};
        Table table2 = new Table(seed2);
        String[] foo2 = {"3" , "4"};
        table2.add(foo2);
        String[] seed3 = new String[]{"beyond", "infinity"};
        Table table3 = new Table(seed3);
        Table[] listy = {table , table2};
        Column c1 = new Column("one" , listy);
        Column c2 = new Column("three" , listy);
        List<Column> cList = new ArrayList<Column>();
        cList.add(c1);
        cList.add(c2);
        Integer[] A = {0 , 0};
        table3.add(cList , A);
        assertEquals("1" , table3.get(0 , 0));
    }

    @Test
    public void testReadTable() {
        String[] seederSTRING = new String[]{"SID", "Lastname",
                                             "Firstname", "SemEnter",
                                             "YearEnter", "Major"};
        String[] rows = new String[]{"101" , "Knowles", "Jason",
                                     "F", "2003", "EECS"};
        Table seed = new Table(seederSTRING);
        seed.add(rows);
        Table target = Table.readTable("students");
        assertEquals(seed.get(0, 2), target.get(0, 2));
    }

    @Test
    public void testWriteTable() {
        String[] seederSTRING = new String[]{"SID", "Lastname",
                                             "Firstname", "SemEnter",
                                             "YearEnter", "Major"};
        String[] rows = new String[]{"101", "Knowles", "Jason",
                                     "F", "2003", "EECS"};
        Table seed = new Table(seederSTRING);
        seed.add(rows);
        seed.writeTable("students2");
        Table A = Table.readTable("students");
        Table B = Table.readTable("students2");
        assertEquals(A.get(0, 0), B.get(0, 0));

    }
    @Test
    public void testSelect1() {
        String[] seederSTRING = new String[]{"SID", "Lastname",
                                             "Firstname", "SemEnter",
                                             "YearEnter", "Major"};
        String[] rows = new String[]{"101", "Knowles", "Jason",
                                     "F", "2003", "EECS"};
        Table seed = new Table(seederSTRING);
        seed.add(rows);
        Column col1 = new Column("SID", seed);
        Column col2 = new Column("YearEnter", seed);
        List<String> A = new ArrayList<>();
        A.add("SID");
        A.add("YearEnter");
        String relation = "!=";
        Condition conditions = new Condition(col1, relation, col2);
        List<Condition> B = new ArrayList<>();
        B.add(conditions);
        Table hypo = seed.select(A, B);
        String[] seederSTRING2 = new String[]{"SID", "YearEnter"};
        String[] rows2 = new String[]{"101", "2003"};
        Table seed2 = new Table(seederSTRING2);
        seed2.add(rows2);
        seed2.print();
        seed.print();
        hypo.print();
        assertEquals(hypo.get(0, 0), seed2.get(0, 0));
    }
    @Test
    public void debuggerTest() {
        Table A = Table.readTable("students");
        A.print();
        List<String> columns = new ArrayList<>();
        columns.add("SID");
        columns.add("Firstname");
        List<Condition> conditions = new ArrayList<>();
        Column colly = new Column("Lastname", A);
        conditions.add(new Condition(colly, "=", "Chan"));
        A.select(columns, conditions);
    }
    public static void main(String[] ignored) {

    }

}
