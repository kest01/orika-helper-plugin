package ru.kest.plugin.orika.psi

import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiImmediateClassType
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.psi.util.PsiUtil

/**
 * Methods for working with PsiElements
 *
 * Created by KKharitonov on 07.07.2017.
 */

    val OBJECT = "java.lang.Object"

    fun isImplements(type: PsiType, className: String) : Boolean {
        if (type.canonicalText.startsWith(className)) {
            return true
        }
        for (superType in type.superTypes) {
            if (superType.canonicalText.startsWith(className)) {
                return true
            } else if (superType.canonicalText == OBJECT) {
                continue
            } else {
                if (isImplements(superType, className)) {
                    return true
                }
            }
        }
        return false
    }

    fun getClass(psiType : PsiType?) = PsiTypesUtil.getPsiClass(psiType)

    fun isCollection(type: PsiType) = isImplements(type, "java.util.Collection")

    fun isMap(type: PsiType) = isImplements(type, "java.util.Map")

    fun isEnum(type: PsiType) = isImplements(type, "java.lang.Enum")

    fun getCollectionGenericType(type: PsiType?) = PsiUtil.extractIterableTypeParameter(type, false)

    fun getGenericType(type: PsiImmediateClassType) = type.parameters[0]
