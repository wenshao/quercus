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

package com.caucho.quercus.function;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import com.caucho.util.L10N;

import java.util.logging.Logger;

/**
 * Represents a compiled function with 4 args
 */
abstract public class CompiledFunctionRef_4 extends CompiledFunctionRef {
  private static final Logger log
    = Logger.getLogger(CompiledFunctionRef_4.class.getName());
  private static final L10N L = new L10N(CompiledFunctionRef_4.class);

  private final String _name;
  private final Expr _default_0;
  private final Expr _default_1;
  private final Expr _default_2;
  private final Expr _default_3;

  public CompiledFunctionRef_4(String name,
                               Expr default_0,
                               Expr default_1,
                               Expr default_2,
                               Expr default_3)
  {
    _name = name;
    _default_0 = default_0;
    _default_1 = default_1;
    _default_2 = default_2;
    _default_3 = default_3;
  }

  /**
   * Returns this function's name.
   */
  @Override
  public String getName()
  {
    return _name;
  }

  /**
   * Binds the user's arguments to the actual arguments.
   *
   * @param args the user's arguments
   * @return the user arguments augmented by any defaults
   */
  public Expr []bindArguments(Env env, Expr fun, Expr []args)
  {
    if (args.length > 4)
      log.fine(L.l(env.getLocation().getMessagePrefix() + "incorrect number of arguments" + env.getFunctionLocation()));

    return args;
  }

  public Value callRef(Env env, Value []argValues)
  {
    switch (argValues.length) {
    case 0:
      return callRef(env,
                     _default_0.eval(env),
                     _default_1.eval(env),
                     _default_2.eval(env),
                     _default_3.eval(env));

    case 1:
      return callRef(env,
                     argValues[0],
                     _default_1.eval(env),
                     _default_2.eval(env),
                     _default_3.eval(env));
    case 2:
      return callRef(env,
                     argValues[0],
                     argValues[1],
                     _default_2.eval(env),
                     _default_3.eval(env));
    case 3:
      return callRef(env,
                     argValues[0],
                     argValues[1],
                     argValues[2],
                     _default_3.eval(env));
    case 4:
    default:
      return callRef(env,
                     argValues[0],
                     argValues[1],
                     argValues[2],
                     argValues[3]);
    }
  }

  public Value callRef(Env env)
  {
    return callRef(env,
                   _default_0.eval(env),
                   _default_1.eval(env),
                   _default_2.eval(env),
                   _default_3.eval(env));
  }

  public Value callRef(Env env, Value a1)
  {
    return callRef(env,
                   a1,
                   _default_1.eval(env),
                   _default_2.eval(env),
                   _default_3.eval(env));
  }

  public Value callRef(Env env, Value a1, Value a2)
  {
    return callRef(env,
                   a1,
                   a2,
                   _default_2.eval(env),
                   _default_3.eval(env));
  }

  public Value callRef(Env env, Value a1, Value a2, Value a3)
  {
    return callRef(env,
                    a1,
                    a2,
                    a3,
                    _default_3.eval(env));
  }

  /**
   * Evaluates the function with arguments
   */
  abstract public Value callRef(Env env, Value a1, Value a2, Value a3, Value a4);

  public String toString()
  {
    return "CompiledFunctionRef_4[" + _name + "]";
  }
}

