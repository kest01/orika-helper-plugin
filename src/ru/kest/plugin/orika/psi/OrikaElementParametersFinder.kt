package ru.kest.plugin.orika.psi

import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiImmediateClassType
import com.intellij.psi.util.PsiTreeUtil
import ru.kest.plugin.orika.entity.MappingClasses

/**
 * Find classes used for mapping with Orika
 *
 * Created by KKharitonov on 28.06.2017.
 */
object OrikaElementParametersFinder {

    fun getParamClasses(targetElement: PsiElement) : MappingClasses? {
        val methodName = getMethodName(targetElement)
        when (methodName) {
            "map", "mapAsList" -> {
                return getMappingClasses(targetElement)
            }
            else -> TODO("unsupported Orika method $methodName")
        }
    }

    private fun getMappingClasses(targetElement: PsiElement): MappingClasses? {
        val methodCallExpressionEl = PsiTreeUtil.getParentOfType(targetElement, PsiMethodCallExpression::class.java)
        val parametersEl = PsiTreeUtil.findChildOfType(methodCallExpressionEl, PsiExpressionList::class.java)

        val sourceClass = PsiUtils.getClass(getSourceClass(parametersEl))
        val destClass = PsiUtils.getClass(getDestClass(parametersEl))
        val mapperClass = PsiUtils.getClass(getMapperClass(parametersEl))

        if (sourceClass == null || destClass == null || mapperClass == null) {
            return null
        }

        return MappingClasses(sourceClass, destClass, mapperClass)
    }

    private fun getMapperClass(parametersEl: PsiExpressionList?): PsiType? {
        return PsiTreeUtil.getParentOfType(parametersEl, PsiMethodCallExpression::class.java)
                        ?.methodExpression?.qualifierExpression?.type
    }

    private fun getMethodName(parentElement: PsiElement) : String {
        return parentElement.text
    }

    private fun getSourceClass(parentEl : PsiExpressionList?) : PsiType? {
        if (parentEl != null && parentEl.expressions.isNotEmpty()) {
            val sourceType = parentEl.expressions[0].type!!
            if (PsiUtils.isCollection(sourceType)) {
                return PsiUtils.getGenericType(sourceType)
            } else return sourceType
        }
        return null
    }

    private fun getDestClass(parentEl : PsiExpressionList?) : PsiType? {
        if (parentEl != null && parentEl.expressions.isNotEmpty() && parentEl.expressions.size >= 2) {
            val secondParamType = parentEl.expressions[1].type
            if (secondParamType is PsiImmediateClassType) {
                return PsiUtils.getGenericType(secondParamType)
            } else {
                return secondParamType
            }
        }
        return null
    }

}