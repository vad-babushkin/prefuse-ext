package ieg.prefuse.data.expression;

import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.expression.Expression;
import prefuse.data.expression.FunctionExpression;
import prefuse.data.expression.Predicate;

public class IsNanPredicate extends FunctionExpression implements Predicate {

    public IsNanPredicate(Expression expr) {
        super(1);
        this.addParameter(expr);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getType(Schema s) {
        return boolean.class;
    }
    
    public Object get(Tuple t) {
        return new Boolean(getBoolean(t));
    }
    
    @Override
    public boolean getBoolean(Tuple t) {
        if ( paramCount() == 1) {
            return Double.isNaN(param(0).getDouble(t));
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return "ISNAN";
    }
}
