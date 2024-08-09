package com.dimata.helpdesk.core.auth.permission

import jakarta.interceptor.InterceptorBinding
import java.lang.annotation.Inherited

@Inherited
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@InterceptorBinding
annotation class Permission(val name: String)