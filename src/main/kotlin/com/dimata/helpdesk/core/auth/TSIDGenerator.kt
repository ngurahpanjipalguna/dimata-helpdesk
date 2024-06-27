package com.dimata.helpdesk.core.auth

import io.hypersistence.tsid.TSID

fun generateTSID() = TSID.Factory.getTsid().toString()