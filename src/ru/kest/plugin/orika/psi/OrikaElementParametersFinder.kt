package ru.kest.plugin.orika.psi

import com.intellij.psi.*
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
            "map" -> {
                // TODO Add support of non-reference parameters: functions calls, new statements
                val methodCallExpressionEl = PsiTreeUtil.getParentOfType(targetElement, PsiMethodCallExpression::class.java)
                val parametersEl = PsiTreeUtil.findChildOfType(methodCallExpressionEl, PsiExpressionList::class.java)

                val sourceClass = getSourceClass(parametersEl)
                val destClass = getDestClass(parametersEl)
                val mapperClass = getMapperClass(parametersEl)

                if (sourceClass == null || destClass == null || mapperClass == null) {
                    return null
                }

                return MappingClasses(sourceClass, destClass, mapperClass)
            }
            else -> TODO("unsupported Orika method $methodName")
        }
    }

    private fun getMapperClass(parametersEl: PsiExpressionList?): PsiClass? {
//        (targetElement.parent.parent as PsiMethodCallExpressionImpl).methodExpression.qualifierExpression.type.canonicalText
        return PsiUtils.getClass(
                PsiTreeUtil.getParentOfType(parametersEl, PsiMethodCallExpression::class.java)
                        ?.methodExpression?.qualifierExpression?.type)
    }

    private fun getMethodName(parentElement: PsiElement) : String {
        return parentElement.text
    }

    private fun getSourceClass(parentEl : PsiExpressionList?) : PsiClass? {
        return PsiUtils.getClass(
                PsiTreeUtil.findChildOfType(parentEl, PsiReferenceExpression::class.java)?.type
        )
    }

    private fun getDestClass(parentEl : PsiExpressionList?) : PsiClass? {
        return PsiUtils.getClass(
                PsiTreeUtil.findChildOfType(parentEl, PsiClassObjectAccessExpression::class.java)?.operand?.type
        )
    }

}