package ru.kest.plugin.orika.psi

import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

/**
 * Find classes used for mapping with Orika
 *
 * Created by KKharitonov on 28.06.2017.
 */
object OrikaElementParametersFinder {

    fun getParamClasses(targetElement: PsiElement) : Pair<String, String>? {
        val methodName = getMethodName(targetElement)
        when (methodName) {
            "map" -> {
                val methodCallExpressionEl = PsiTreeUtil.getParentOfType(targetElement, PsiMethodCallExpression::class.java)
                val parametersEl = PsiTreeUtil.findChildOfType(methodCallExpressionEl, PsiExpressionList::class.java)

                val sourceType = getSourceType(PsiTreeUtil.findChildOfType(parametersEl, PsiReferenceExpression::class.java))
                val destType = getDestType(PsiTreeUtil.findChildOfType(parametersEl, PsiClassObjectAccessExpression::class.java))

                if (sourceType == null || destType == null) {
                    return null
                }

                return Pair(sourceType.canonicalText, destType.canonicalText)
            }
            else -> TODO("unsupported Orika method $methodName")
        }
    }

    fun getMethodName(parentElement: PsiElement) : String {
        return parentElement.text
    }

    fun getSourceType(refEl : PsiReferenceExpression?) : PsiType? {
        return refEl?.type
    }

    fun getDestType(refEl : PsiClassObjectAccessExpression?) : PsiType? {
        return refEl?.operand?.type
    }

}