/*
 * Copyright (c) 1998-2012 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.quercus.statement;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;

/**
 * Represents an if statement.
 */
public class IfStatement extends Statement {

    private final Expr      test;
    private final Statement trueBlock;
    private final Statement falseBlock;

    public IfStatement(Location location, Expr test, Statement trueBlock, Statement falseBlock){
        super(location);

        this.test = test;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;

        if (this.trueBlock != null) {
            this.trueBlock.setParent(this);
        }

        if (this.falseBlock != null) {
            this.falseBlock.setParent(this);
        }
    }

    public Expr getTest() {
        return test;
    }

    public Statement getTrueBlock() {
        return trueBlock;
    }

    public Statement getFalseBlock() {
        return falseBlock;
    }

    /**
     * Executes the 'if' statement, returning any value.
     */
    public Value execute(Env env) {
        if (test.evalBoolean(env)) {
            return trueBlock.execute(env);
        } else if (falseBlock != null) {
            return falseBlock.execute(env);
        } else return null;
    }
}
