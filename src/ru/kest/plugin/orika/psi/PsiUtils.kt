package ru.kest.plugin.orika.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.psi.util.PsiUtil

/**
 * Methods for working with PsiElements
 *
 * Created by KKharitonov on 07.07.2017.
 */
object PsiUtils {

    val OBJECT = "java.lang.Object"

    fun isImplements(type: PsiType, className: String) : Boolean {
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

    fun getClass(psiType : PsiType?) : PsiClass? {
        return PsiTypesUtil.getPsiClass(psiType)
    }

    fun isCollection(type: PsiType) : Boolean {
        return isImplements(type, "java.util.Collection")
    }

    fun isEnum(type: PsiType) : Boolean {
        return isImplements(type, "java.lang.Enum")
    }

    fun getGenericType(type: PsiType?) =
            PsiUtil.extractIterableTypeParameter(type, false)

}