package com.alibaba.phpwall;

import org.apache.log4j.Logger;

import com.caucho.quercus.QuercusContext;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.BinaryAssignExpr;
import com.caucho.quercus.expr.CallExpr;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.statement.BlockStatement;
import com.caucho.quercus.statement.ClassDefStatement;
import com.caucho.quercus.statement.ExprStatement;
import com.caucho.quercus.statement.IfStatement;
import com.caucho.quercus.statement.Statement;
import com.caucho.quercus.statement.SwitchStatement;

public class WallIterator {

    private final static Logger  LOG = Logger.getLogger(WallIterator.class);

    private final QuercusContext context;
    private Env                  env;

    public WallIterator(QuercusContext context, Env env){
        this.context = context;
        this.env = env;
    }

    public void checkStatement(Statement stmt) {
        if (stmt == null) {
            return;
        }

        if (stmt instanceof BlockStatement) {
            checkBlockStatement((BlockStatement) stmt);
        } else if (stmt instanceof IfStatement) {
            checkIfStatement((IfStatement) stmt);
        } else if (stmt instanceof ExprStatement) {
            checkExprStatement((ExprStatement) stmt);
        } else if (stmt instanceof SwitchStatement) {
            checkSwitchStatement((SwitchStatement) stmt);
        } else if (stmt instanceof ClassDefStatement) {
            checkClassDefStatement((ClassDefStatement) stmt);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("other : " + stmt.getClass());
            }
        }
    }

    public void checkBlockStatement(BlockStatement block) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("check : " + block.getClass());
        }
        checkStatement(block.getStatements());
    }

    public void checkStatement(Statement[] statements) {
        for (Statement stmt : statements) {
            checkStatement(stmt);
        }
    }

    public void checkIfStatement(IfStatement stmt) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("if : " + stmt.getClass());
        }
        checkStatement(stmt.getTrueBlock());
        checkStatement(stmt.getFalseBlock());
    }

    public void checkSwitchStatement(SwitchStatement stmt) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("switch : " + stmt.getClass());
        }

        stmt.getBlocks();
        checkStatement(stmt.getDefaultBlock());
    }

    public void checkClassDefStatement(ClassDefStatement stmt) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkClassDef : " + stmt.getClass());
        }
    }

    public void checkExprStatement(ExprStatement stmt) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("expr : " + stmt.getExpr());
        }
        checkExpr(stmt.getExpr());
    }

    public void checkExpr(Expr expr) {
        if (expr instanceof BinaryAssignExpr) {
            checkAssign((BinaryAssignExpr) expr);
        } else if (expr instanceof CallExpr) {
            checkCall((CallExpr) expr);
        } else {
            LOG.debug("other expr : " + expr.getClass());
        }
    }

    public void checkAssign(BinaryAssignExpr expr) {
        LOG.debug("assign");
    }

    public void checkCall(CallExpr expr) {
        LOG.debug("assign");

        Expr[] args = expr.getArgs();

        boolean isLiteral = true;
        for (Expr arg : args) {
            if (!arg.isLiteral()) {
                isLiteral = false;
                break;
            }
        }

        if (isLiteral) {
            try {
                Value value = expr.eval(env);
                expr.putAttribute("wall.value", value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Expr arg : args) {
            checkExpr(arg);
        }
    }
}
