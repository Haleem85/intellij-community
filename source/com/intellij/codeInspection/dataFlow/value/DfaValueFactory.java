/*
 * Created by IntelliJ IDEA.
 * User: max
 * Date: Feb 7, 2002
 * Time: 2:33:28 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.intellij.codeInspection.dataFlow.value;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.psi.impl.ConstantExpressionEvaluator;
import gnu.trove.THashSet;
import gnu.trove.TIntObjectHashMap;

public class DfaValueFactory {
  private static final Logger LOG = Logger.getInstance("#com.intellij.codeInspection.dataFlow.value.DfaValueFactory");

  private static volatile DfaValueFactory myInstance = null;
  private int myLastID;
  private TIntObjectHashMap<DfaValue> myValues;

  private DfaValueFactory() {
    myValues = new TIntObjectHashMap<DfaValue>();
    myLastID = 0;
  }

  public static DfaValueFactory getInstance() {
    if (myInstance == null) {
      myInstance = new DfaValueFactory();
    }

    return myInstance;
  }

  int createID() {
    myLastID++;
    LOG.assertTrue(myLastID >= 0, "Overflow");
    return myLastID;
  }

  void registerValue(DfaValue value) {
    myValues.put(value.getID(), value);
  }

  public DfaValue getValue(int id) {
    return myValues.get(id);
  }

  public static DfaValue create(PsiExpression psiExpression) {
    DfaValue result = null;

    if (psiExpression instanceof PsiReferenceExpression) {
      PsiElement psiSource = ((PsiReferenceExpression)psiExpression).resolve();

      if (psiSource != null) {
        if (psiSource instanceof PsiVariable) {
          DfaConstValue constValue = DfaConstValue.Factory.getInstance().create((PsiVariable)psiSource);
          if (constValue != null) return constValue;
        }

        PsiVariable psiVariable = resolveVariable((PsiReferenceExpression)psiExpression);
        if (psiVariable != null) {
          result = DfaVariableValue.Factory.getInstance().create(psiVariable, false);
        }
      }
    }
    else if (psiExpression instanceof PsiLiteralExpression) {
      result = DfaConstValue.Factory.getInstance().create((PsiLiteralExpression)psiExpression);
    }
    else if (psiExpression instanceof PsiNewExpression) {
      result = DfaNewValue.Factory.getInstance().create(psiExpression.getType());
    }
    else {
      final Object value = ConstantExpressionEvaluator.computeConstantExpression(psiExpression, new THashSet<PsiVariable>(), false);
      if (value != null) {
        result = DfaConstValue.Factory.getInstance().createFromValue(value);
      }
    }

    return result;
  }

  public static PsiVariable resolveVariable(PsiReferenceExpression refExpression) {
    PsiExpression qualifier = refExpression.getQualifierExpression();
    if (qualifier == null || qualifier instanceof PsiThisExpression) {
      PsiElement resolved = refExpression.resolve();
      if (resolved instanceof PsiVariable) {
        return (PsiVariable)resolved;
      }
    }

    return null;
  }

  public static void freeInstance() {
    DfaVariableValue.Factory.freeInstance();
    DfaConstValue.Factory.freeInstance();
    DfaNewValue.Factory.freeInstance();
    DfaTypeValue.Factory.freeInstance();
    DfaRelationValue.Factory.freeInstance();
    myInstance = null;
  }
}
