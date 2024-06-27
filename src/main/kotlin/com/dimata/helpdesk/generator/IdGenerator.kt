package com.dimata.helpdesk.generator

import io.hypersistence.tsid.TSID

fun generateTSID() = TSID.Factory.getTsid().toString()

fun generateTSIDNumber() = TSID.Factory.getTsid().toLong()