package edu.washington.escience.myria.expression.evaluate;

import edu.washington.escience.myria.ReadableTable;
import edu.washington.escience.myria.TupleBatch;
import edu.washington.escience.myria.column.builder.WritableColumn;
import edu.washington.escience.myria.expression.VariableExpression;

/**
 * Interface for evaluating janino expressions.
 */
public interface EvalInterface {
  /**
   * The interface for applying expressions. We only need a reference to the tuple batch and a row id. The variables
   * will be fetched from the tuple buffer using the rowId provided in {@link VariableExpression}.
   * 
   * @param tb a tuple batch
   * @param rowId the row in the tb that should be used.
   * @param result the result column that the value should be appended to
   * @param state optional state that is passed during evaluation
   * @return the result from the evaluation
   */
  Object evaluate(final TupleBatch tb, final int rowId, final WritableColumn<?> result, final ReadableTable state);
}
