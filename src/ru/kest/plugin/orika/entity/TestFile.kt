package ru.kest.plugin.orika.entity

import com.intellij.psi.PsiDirectory

/**
 * Entity represents test file
 *
 * Created by KKharitonov on 30.06.2017.
 */
data class TestFile(
        val className: String,
        val packageName: String,
        val directory: PsiDirectory)