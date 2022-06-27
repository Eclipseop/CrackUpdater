package org.eclipseop.crackupdater.util;

import org.eclipseop.crackupdater.util.ast.AbstractSyntaxTree;
import org.eclipseop.crackupdater.util.ast.expression.Expression;
import org.eclipseop.crackupdater.util.ast.expression.impl.InstanceExpression;
import org.eclipseop.crackupdater.util.ast.expression.impl.MathExpression;
import org.eclipseop.crackupdater.util.ast.expression.impl.VarExpression;
import org.eclipseop.crackupdater.util.found.FoundField;
import org.eclipseop.crackupdater.util.found.FoundStuff;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Multiplier {

  private static boolean isInteger(String value) {
    try {
      Integer.parseInt(value);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private static <T> T mostCommon(List<T> list) {
    Map<T, Integer> map = new HashMap<>();

    for (T t : list) {
      Integer val = map.get(t);
      map.put(t, val == null ? 1 : val + 1);
    }

    Map.Entry<T, Integer> max = null;

    for (Map.Entry<T, Integer> e : map.entrySet()) {
      if (max == null || e.getValue() > max.getValue()) max = e;
    }

    return max.getKey();
  }

  // TODO: 6/11/2022 rewrite this :3
  public static void findMultipliers(List<ClassNode> classNodes) {
    List<Expression> ast = AbstractSyntaxTree.find(classNodes, Opcodes.IMUL);

    Map<String, List<Integer>> multiMap = new HashMap<>();
    for (Expression expression : ast) {
      final MathExpression me = (MathExpression) expression;

      String field = null;
      int multi = -1;

      if (me.containsExpression(VarExpression.class)) {
        // System.out.println(me);
        if (me.isExpectedExpressions(VarExpression.class, VarExpression.class)) {
          final VarExpression right = (VarExpression) me.getRight();

          if (isInteger(right.getVarName())) {
            multi = Integer.parseInt(right.getVarName());
          } else {
            field = right.getVarName();
          }
        }

        final VarExpression ve = me.find(VarExpression.class);
        if (ve.getVarName().contains(".")) {
          field = ve.getVarName();
        } else if (isInteger(ve.getVarName())) {
          multi = Integer.parseInt(ve.getVarName());
        }
      } else {
        continue;
      }

      if (me.containsExpression(InstanceExpression.class)) {
        final InstanceExpression ie = me.find(InstanceExpression.class);
        field = ie.getFieldName();
      }

      if (field != null && multi != -1) {
        if (field.contains(".")) {
          multiMap.computeIfAbsent(field, f -> new ArrayList<>()).add(multi);
        }
      }
    }

    multiMap
        .keySet()
        .forEach(
            c -> {
              FoundField identifiedField = FoundStuff.findIdentifiedField(c);
              if (identifiedField != null) {
                identifiedField.setMultiplier(mostCommon(multiMap.get(c)));
              }
            });
  }
}
