package ru.kest.plugin.orika.psi

import com.intellij.psi.*

/**
 * Helper for find methods of MapperFacade interface
 *
 * Created by KKharitonov on 28.06.2017.
 */
object OrikaElementFinder {

    val ORIKA_MAPPER_INTERFACE = "ma.glasnost.orika.MapperFacade"
    val OBJECT = "java.lang.Object"


    fun isOrikaElement(element: PsiElement) : Boolean {
        if (!element.isWritable) return false

        if (element is PsiIdentifier) {
            val parent = element.parent
            if (parent is PsiReferenceExpression) {
                val grandpa = parent.parent
                if (grandpa is PsiMethodCallExpression) {
                    val psiType = grandpa.methodExpression.qualifierExpression?.type
                    if (psiType != null) {
                        return PsiUtils.isImplements(psiType, ORIKA_MAPPER_INTERFACE)
                    }
                }
            }
        }
        return false
    }




}