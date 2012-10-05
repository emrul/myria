package edu.washington.escience.myriad.operator;

import java.util.Iterator;

import edu.washington.escience.myriad.DbException;
import edu.washington.escience.myriad.Schema;
import edu.washington.escience.myriad.TupleBatch;
import edu.washington.escience.myriad.accessmethod.SQLiteAccessMethod;
import edu.washington.escience.myriad.table._TupleBatch;

public class SQLiteQueryScan extends Operator {

  private Iterator<TupleBatch> tuples;
  private final Schema schema;
  private final String filename;
  private final String baseSQL;
  private transient String dataDir;

  public SQLiteQueryScan(final String filename, final String baseSQL, final Schema outputSchema) {
    this.baseSQL = baseSQL;
    this.filename = filename;
    schema = outputSchema;
  }

  @Override
  public void close() {
    super.close();
    this.tuples = null;
  }

  @Override
  protected _TupleBatch fetchNext() throws DbException {
    // if (cache != null) {
    // TupleBatch tmp = cache;
    // cache = null;
    // return tmp;
    // } else {
    if (tuples.hasNext()) {
      return this.tuples.next();
    }
    else {
      return null;
    // }
    }
  }

  @Override
  public Operator[] getChildren() {
    return null;
  }

  @Override
  public Schema getSchema() {
    return schema;
  }

  @Override
  public void open() throws DbException {
    super.open();
    tuples = SQLiteAccessMethod.tupleBatchIteratorFromQuery(dataDir + "/" + filename, baseSQL, schema);
  }

  @Override
  public void setChildren(final Operator[] children) {
    throw new UnsupportedOperationException();
  }

  public void setDataDir(final String dataDir) {
    this.dataDir = dataDir;
  }

}
