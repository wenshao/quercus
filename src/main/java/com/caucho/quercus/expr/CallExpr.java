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

package com.caucho.quercus.expr;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.QuercusClass;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.function.AbstractFunction;
import com.caucho.util.L10N;

import java.util.ArrayList;

/**
 * A "foo(...)" function call.
 */
public class CallExpr extends Expr {

    private static final L10N L = new L10N(CallExpr.class);

    private final StringValue name;
    private final StringValue nsName;
    private final Expr[]      args;

    private int               _funId;

    private boolean           _isRef;

    public CallExpr(Location location, StringValue name, ArrayList<Expr> args){
        // quercus/120o
        super(location);
        this.name = name;

        int ns = this.name.lastIndexOf('\\');

        if (ns > 0) {
            this.nsName = this.name.substring(ns + 1);
        } else {
            this.nsName = null;
        }

        this.args = new Expr[args.size()];
        args.toArray(this.args);
    }
    
    public Expr[] getArgs() {
        return args;
    }

    public CallExpr(Location location, StringValue name, Expr[] args){
        // quercus/120o
        super(location);
        this.name = name;

        int ns = this.name.lastIndexOf('\\');

        if (ns > 0) {
            this.nsName = name.substring(ns + 1);
        } else {
            this.nsName = null;
        }

        this.args = args;
    }

    public CallExpr(StringValue name, ArrayList<Expr> args){
        this(Location.UNKNOWN, name, args);
    }

    public CallExpr(StringValue name, Expr[] args){
        this(Location.UNKNOWN, name, args);
    }

    /**
     * Returns the name.
     */
    public StringValue getName() {
        return name;
    }

    /**
     * Returns the location if known.
     */
    public String getFunctionLocation() {
        return " [" + name + "]";
    }

    /**
     * Returns the reference of the value.
     * 
     * @param location
     */
    /*
     * @Override public Expr createRef(QuercusParser parser) { return parser.getExprFactory().createCallRef(this); }
     */

    /**
     * Returns the copy of the value.
     * 
     * @param location
     */
    @Override
    public Expr createCopy(ExprFactory factory) {
        return this;
    }

    /**
     * Evaluates the expression.
     * 
     * @param env the calling environment.
     * @return the expression value.
     */
    @Override
    public Value eval(Env env) {
        return evalImpl(env, false, false);
    }

    /**
     * Evaluates the expression.
     * 
     * @param env the calling environment.
     * @return the expression value.
     */
    @Override
    public Value evalCopy(Env env) {
        return evalImpl(env, false, true);
    }

    /**
     * Evaluates the expression.
     * 
     * @param env the calling environment.
     * @return the expression value.
     */
    @Override
    public Value evalRef(Env env) {
        return evalImpl(env, true, true);
    }

    /**
     * Evaluates the expression.
     * 
     * @param env the calling environment.
     * @return the expression value.
     */
    private Value evalImpl(Env env, boolean isRef, boolean isCopy) {
        if (_funId <= 0) {
            _funId = env.findFunctionId(name);

            if (_funId <= 0) {
                if (nsName != null) _funId = env.findFunctionId(nsName);

                if (_funId <= 0) {
                    env.error(getLocationLine(), L.l("'{0}' is an unknown function.", name));

                    return NullValue.NULL;
                }
            }
        }

        AbstractFunction fun = env.findFunction(_funId);

        if (fun == null) {
            env.error(getLocationLine(), L.l("'{0}' is an unknown function.", name));

            return NullValue.NULL;
        }

        Value[] args = evalArgs(env, this.args);

        env.pushCall(this, NullValue.NULL, args);

        // php/0249
        QuercusClass oldCallingClass = env.setCallingClass(null);

        // XXX: qa/1d14 Value oldThis = env.setThis(UnsetValue.NULL);
        try {
            env.checkTimeout();

            /*
             * if (isRef) return fun.callRef(env, args); else if (isCopy) return fun.callCopy(env, args); else return
             * fun.call(env, args);
             */

            if (isRef) return fun.callRef(env, args);
            else if (isCopy) return fun.call(env, args).copyReturn();
            else {
                return fun.call(env, args).toValue();
            }
            // } catch (Exception e) {
            // throw QuercusException.create(e, env.getStackTrace());
        } finally {
            env.popCall();
            env.setCallingClass(oldCallingClass);
            // XXX: qa/1d14 env.setThis(oldThis);
        }
    }

    // Return an array containing the Values to be
    // passed in to this function.

    public Value[] evalArguments(Env env) {
        AbstractFunction fun = env.findFunction(name);

        if (fun == null) {
            return null;
        }

        return fun.evalArguments(env, this, args);
    }

    public String toString() {
        return name + "()";
    }
}
