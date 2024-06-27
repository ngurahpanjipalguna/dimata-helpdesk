package com.dimata.helpdesk.core.auth.logged

import jakarta.interceptor.InterceptorBinding
import java.lang.annotation.Inherited

@Inherited
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@InterceptorBinding
annotation class Logged(val event: String = "")