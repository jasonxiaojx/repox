
package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Jason Xiao
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }

        _titles = columnTitles;
        _columns = new ValueList[_rowSize];
        for (int i = 0; i < _rowSize; i++) {
            _columns[i] = new ValueList();
        }
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _rowSize;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        int index = 0;
        for (String x:_titles) {
            if (x.equals(title)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }

    /** Return the number of rows in this table. */
    public int size() {
        return _size;
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        if (this._size == 0) {
            for (int j = 0; j < this._rowSize; j++) {
                _columns[j].add(values[j]);
            }
            _size++;
            return true;
        } else {
            for (int i = 0; i < this._size; i++) {
                int counter = 0;
                for (int k = 0; k < this._rowSize; k++) {
                    if (values[k].equals(_columns[k].get(i))) {
                        counter++;
                    }
                }
                if (counter == _rowSize) {
                    return false;
                }
            }

            for (int j = 0; j < this._rowSize; j++) {
                _columns[j].add(values[j]);
            }
            _size++;
            return true;
        }
    }
    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        String[] vals = new String[rows.length];
        for (int i = 0; i < columns.size(); i++) {
            vals[i] = columns.get(i).getFrom(rows);
        }
        return add(vals);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                String[] row = line.split(",");
                table.add(row);
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            for (String X:this._titles) {
                if (!X.equals(this._titles[_rowSize - 1])) {
                    sep += X + ",";
                } else {
                    sep += X + "\n";
                }
            }
            output = new PrintStream(name + ".db");
            for (int i = 0; i < _size; i++) {
                for (int col = 0; col < _rowSize; col++) {
                    if (col < _rowSize - 1) {
                        sep = sep + this.get(i, col) + ",";
                    } else {
                        sep = sep + this.get(i, col);
                    }
                }
                sep = sep + "\n";
            }
            output.print(sep);
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        String separation = "  ";
        int index = 0;
        while (index < size()) {
            int least = 0;
            while (least < size() - 1 && _index.contains(least)) {
                least++;
            }
            for (int k = 0; k < size(); k++) {
                if (_index.contains(k)) {
                    continue;
                }
                if (compareRows(least, k) > 0) {
                    least = k;
                }
            }
            _index.add(least);
            index++;
        }
        int row = 0;
        while (row < _index.size()) {
            for (int col = 0; col < columns(); col++) {
                separation += get(_index.get(row), col);
                if (col < columns() - 1) {
                    separation += " ";
                }
            }
            separation += "\n";
            if (row < size() - 1) {
                separation += "  ";
            }
            row++;
        }
        if (size() == 0) {
            return;
        }
        System.out.print(separation);
    }

    /** A method that is specifically used to update the _index variable**/

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        for (int row = 0; row < size(); row++) {
            while (conditions == null) {
                String[] rows = new String[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    rows[i] = get(row, findColumn(columnNames.get(i)));
                }
                result.add(rows);
                break;
            }
            while (Condition.test(conditions, row)) {
                String[] rows = new String[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    rows[i] = get(row, findColumn(columnNames.get(i)));
                }
                result.add(rows);
                break;
            }
        }
        return result;
    }
    /** Adds all the column names of this table and TABLE2 to a
     * string array. @return returns the list of names. */
    List<String> columnAdder(Table table2) {
        List<String> commonColName = new ArrayList<>();
        for (int i = 0; i < this.columns(); i += 1) {
            for (int j = 0; j < table2.columns(); j += 1) {
                String a = this._titles[i];
                String b = table2._titles[j];
                if (a.equals(b)) {
                    commonColName.add(a);
                }
            }
        }
        return commonColName;
    }
    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table output = new Table(columnNames);
        List<Column> common1 = new ArrayList<>();
        List<Column> common2 = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        for (String colName : columnNames) {
            Column c1 = new Column(colName, this, table2);
            columns.add(c1);
        }
        List<String> commonColName = columnAdder(table2);
        for (String name : commonColName) {
            common1.add(new Column(name, this));
            common2.add(new Column(name, table2));
        }
        for (int row1 = 0; row1 < this.size(); row1++) {
            for (int row2 = 0; row2 < table2.size(); row2++) {
                if (equijoin(common1, common2, row1, row2)) {
                    if (conditions == null) {
                        String[] happyrows = new String[columnNames.size()];
                        for (int i = 0; i < happyrows.length; i++) {
                            Column c1 = columns.get(i);
                            happyrows[i] = c1.getFrom(row1, row2);
                        }
                        output.add(happyrows);
                    } else if (Condition.test(conditions, row1, row2)) {
                        String[] happyrows2 = new String[columnNames.size()];
                        for (int i = 0; i < happyrows2.length; i++) {
                            Column c1 = columns.get(i);
                            happyrows2[i] = c1.getFrom(row1, row2);
                        }
                        output.add(happyrows2);
                    }
                }
            }
        }

        return output;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int column = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (column != 0) {
                return column;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        int index = 0;
        while (index < common2.size()) {
            String elem1 = common1.get(index).getFrom(row1);
            String elem2 = common2.get(index).getFrom(row2);
            if (!elem2.equals(elem1)) {
                return false;
            }
            index++;
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}
