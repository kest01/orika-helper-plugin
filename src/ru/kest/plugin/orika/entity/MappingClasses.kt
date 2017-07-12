package ru.kest.plugin.orika.entity

import com.intellij.psi.PsiClass

/**
 * Oricka mapping properties
 *
 * Created by KKharitonov on 02.07.2017.
 */
data class MappingClasses(val sourceClass: PsiClass, val destClass: PsiClass, val mapperClass: PsiClass)