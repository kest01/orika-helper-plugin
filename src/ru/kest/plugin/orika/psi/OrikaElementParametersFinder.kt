package ru.kest.plugin.orika.psi

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTypesUtil

/**
 * Find classes used for mapping with Orika
 *
 * Created by KKharitonov on 28.06.2017.
 */
object OrikaElementParametersFinder {

    fun getParamClasses(targetElement: PsiElement) : Pair<PsiClass, PsiClass>? {
        val methodName = getMethodName(targetElement)
        when (methodName) {
            "map" -> {
                val methodCallExpressionEl = PsiTreeUtil.getParentOfType(targetElement, PsiMethodCallExpression::class.java)
                val parametersEl = PsiTreeUtil.findChildOfType(methodCallExpressionEl, PsiExpressionList::class.java)

                val sourceClass = getSourceClass(parametersEl)
                val destClass = getDestClass(parametersEl)

                if (sourceClass == null || destClass == null) {
                    return null
                }

                return Pair(sourceClass, destClass)
            }
            else -> TODO("unsupported Orika method $methodName")
        }
    }

    private fun getMethodName(parentElement: PsiElement) : String {
        return parentElement.text
    }

    private fun getSourceClass(parentEl : PsiExpressionList?) : PsiClass? {
        return getClass(
                PsiTreeUtil.findChildOfType(parentEl, PsiReferenceExpression::class.java)?.type
        )
    }

    private fun getDestClass(parentEl : PsiExpressionList?) : PsiClass? {
        return getClass(
                PsiTreeUtil.findChildOfType(parentEl, PsiClassObjectAccessExpression::class.java)?.operand?.type
        )
    }

    private fun getClass(psiType : PsiType?) : PsiClass? {
        return PsiTypesUtil.getPsiClass(psiType)
    }

}