package ru.kest.plugin.orika.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTypesUtil

/**
 * Methods for working with PsiElements
 *
 * Created by KKharitonov on 07.07.2017.
 */
object PsiUtils {

    fun isImplements(type: PsiType, className: String) : Boolean {
        for (superType in type.superTypes) {
            if (superType.canonicalText.startsWith(className)) {
                return true
            } else if (superType.canonicalText == OrikaElementFinder.OBJECT) {
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

}