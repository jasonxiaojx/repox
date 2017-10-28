package db61b;
import java.util.HashMap;
/** A collection of Tables, indexed by name.
 *  @author Jason Xiao*/
class Database {
    /** An empty database. */
    private HashMap<String, Table> hashMemory;
    /**The DATABASE Constructor, with initialized memory.**/
    public Database() {
        hashMemory = new HashMap<>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        return hashMemory.get(name);
    }

    /**This function is a loader, specifically used to load tables
     * into the database. important for keeping track of data
     * @param tableName is the name of the table that we are supposed to load.
     */
    public void loader(String tableName) {
        Table one = Table.readTable(tableName);
        put(tableName, one);
    }
    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("Null Argument");
        }
        hashMemory.put(name, table);
    }

}

